package com.micoach.ai.application.service;

import com.micoach.shared.crypto.TextEncryptor;
import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.ai.application.port.out.AiProviderStrategy;
import com.micoach.ai.application.port.out.AiRepository;
import com.micoach.ai.application.port.out.ResolvedProvider;
import com.micoach.ai.domain.AiProviderConfig;
import com.micoach.ai.domain.ChatMessage;
import com.micoach.ai.domain.Conversation;
import com.micoach.ai.domain.GenerationLog;
import com.micoach.ai.domain.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Implementación de casos de uso del módulo ai. Depende del puerto de salida de
 * persistencia y de las estrategias de proveedor de IA (Strategy Pattern) — el proveedor
 * a usar ya no es una property estática, se lee de {@code ai_provider_configs} (editable
 * en runtime desde el panel de admin, sin reiniciar el backend).
 */
@Service
public class AiService implements AiUseCase {

    private final AiRepository repository;
    private final List<AiProviderStrategy> strategies;
    private final TextEncryptor encryptor;

    public AiService(AiRepository repository, List<AiProviderStrategy> strategies, TextEncryptor encryptor) {
        this.repository = repository;
        this.strategies = strategies;
        this.encryptor = encryptor;
    }

    // ------------------------- Config de proveedores (admin) -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<AiProviderConfig> listProviderConfigs() {
        return repository.findProviderConfigs();
    }

    @Override
    @Transactional
    public AiProviderConfig updateProviderConfig(String provider, ProviderConfigData data) {
        AiProviderConfig config = requireProviderConfig(provider);
        String newKeyEncrypted = data.apiKey() == null || data.apiKey().isBlank()
                ? null
                : encryptor.encrypt(data.apiKey());
        config.update(data.displayName(), data.baseUrl(), data.model(), newKeyEncrypted, data.enabled());
        return repository.saveProviderConfig(config);
    }

    @Override
    @Transactional
    public AiProviderConfig activateProvider(String provider) {
        AiProviderConfig config = requireProviderConfig(provider);
        repository.deactivateAllProviderConfigs();
        config.activate();
        return repository.saveProviderConfig(config);
    }

    @Override
    public ProviderTestResult testProvider(String provider) {
        AiProviderConfig config = requireProviderConfig(provider);
        try {
            String reply = strategyFor(config.getProvider()).complete(
                    "Respondé únicamente con la palabra OK.", resolve(config));
            return new ProviderTestResult(true, reply == null ? "" : reply.trim());
        } catch (RuntimeException e) {
            return new ProviderTestResult(false, e.getMessage());
        }
    }

    private AiProviderConfig requireProviderConfig(String provider) {
        return repository.findProviderConfigByProvider(provider)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "Proveedor de IA desconocido: '" + provider + "'"));
    }

    // ------------------------- Prompts -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Prompt> listPrompts(String slug, boolean activeOnly) {
        return repository.findPrompts(slug, activeOnly);
    }

    @Override
    @Transactional(readOnly = true)
    public Prompt getPrompt(Long promptId) {
        return repository.findPromptById(promptId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Prompt no encontrado"));
    }

    @Override
    @Transactional
    public Prompt createPrompt(PromptData data) {
        int nextVersion = repository.findMaxVersion(data.slug()) + 1;
        Prompt prompt = Prompt.create(data.slug(), nextVersion, data.provider(), data.model(), data.content(),
                data.params());
        return repository.savePrompt(prompt);
    }

    @Override
    @Transactional
    public Prompt setPromptActive(Long promptId, boolean active) {
        Prompt prompt = repository.findPromptById(promptId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Prompt no encontrado"));
        prompt.setActive(active);
        return repository.savePrompt(prompt);
    }

    // ------------------------- Conversaciones -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Conversation> listConversations(Long userId, String topic) {
        return repository.findConversationsByUser(userId, topic);
    }

    @Override
    @Transactional(readOnly = true)
    public Conversation getConversation(Long userId, Long conversationId) {
        return requireOwnedConversation(userId, conversationId);
    }

    @Override
    @Transactional
    public Conversation createConversation(Long userId, String topic) {
        return repository.saveConversation(Conversation.create(userId, topic));
    }

    @Override
    @Transactional
    public Conversation archiveConversation(Long userId, Long conversationId) {
        Conversation conversation = requireOwnedConversation(userId, conversationId);
        conversation.archive();
        return repository.saveConversation(conversation);
    }

    @Override
    @Transactional
    public ChatMessage addMessage(Long userId, Long conversationId, MessageData data) {
        requireOwnedConversation(userId, conversationId);
        ChatMessage message = ChatMessage.create(conversationId, data.role(), data.content(), data.provider(),
                data.model(), data.tokenUsage());
        return repository.saveMessage(message);
    }

    private Conversation requireOwnedConversation(Long userId, Long conversationId) {
        Conversation conversation = repository.findConversationById(conversationId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Conversación no encontrada"));
        if (!conversation.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Conversación no encontrada");
        }
        return conversation;
    }

    // ------------------------- Auditoría -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<GenerationLog> listGenerationLogs(GenerationLogFilter filter) {
        return repository.findGenerationLogs(filter);
    }

    // ------------------------- Generación -------------------------

    // REQUIRES_NEW: la llamada al proveedor de IA y su auditoría deben persistir aunque
    // el llamador (ej. workout.generateWorkout, que arma el Workout con esta respuesta)
    // termine haciendo rollback de su propia transacción por un error posterior — si no,
    // Spring revierte también el INSERT en ai_generation_logs y se pierde justo el dato
    // que hace falta para diagnosticar qué devolvió el modelo.
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GenerationResult generate(Long userId, String promptSlug, Map<String, Object> variables) {
        Prompt prompt = repository.findPrompts(promptSlug, true).stream().findFirst()
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "No hay un prompt activo para '" + promptSlug + "'"));
        AiProviderConfig activeConfig = repository.findActiveProviderConfig()
                .orElseThrow(() -> new DomainException(502, ErrorCode.INTERNAL_ERROR,
                        "No hay ningún proveedor de IA activo (configuralo en /admin)"));
        AiProviderStrategy strategy = strategyFor(activeConfig.getProvider());
        ResolvedProvider resolved = resolve(activeConfig);
        String rendered = renderTemplate(prompt.getContent(), variables);

        long start = System.currentTimeMillis();
        String output;
        try {
            output = strategy.complete(rendered, resolved);
        } catch (RuntimeException e) {
            logAttempt(userId, prompt, resolved, variables, Map.of("error", String.valueOf(e.getMessage())),
                    (int) (System.currentTimeMillis() - start), "error");
            throw e;
        }
        int durationMs = (int) (System.currentTimeMillis() - start);
        GenerationLog saved = logAttempt(userId, prompt, resolved, variables, Map.of("raw", output), durationMs,
                "success");
        return new GenerationResult(saved.getId(), output, resolved.provider(), resolved.model(), durationMs);
    }

    // REQUIRES_NEW: igual que generate() arriba — si no, la DomainException que el
    // llamador lanza justo después de marcar "partial" hace rollback de esta misma
    // transacción y el UPDATE se pierde, dejando el log en "success" pese al rechazo.
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markGenerationPartial(Long logId) {
        repository.updateGenerationLogStatus(logId, "partial");
    }

    private ResolvedProvider resolve(AiProviderConfig config) {
        String apiKey = config.hasApiKey() ? encryptor.decrypt(config.getApiKeyEncrypted()) : null;
        return new ResolvedProvider(config.getProvider(), config.getBaseUrl(), apiKey, config.getModel());
    }

    private AiProviderStrategy strategyFor(String providerId) {
        return strategies.stream()
                .filter(s -> s.providerId().equals(providerId))
                .findFirst()
                .orElseThrow(() -> new DomainException(500, ErrorCode.INTERNAL_ERROR,
                        "No hay implementación para el proveedor de IA '" + providerId + "'"));
    }

    private GenerationLog logAttempt(Long userId, Prompt prompt, ResolvedProvider provider, Map<String, Object> input,
                            Map<String, Object> output, int durationMs, String status) {
        return repository.saveGenerationLog(GenerationLog.create(userId, prompt.getSlug(), prompt.getVersion(),
                provider.provider(), provider.model(), input, output, durationMs, status));
    }

    private String renderTemplate(String template, Map<String, Object> variables) {
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}
