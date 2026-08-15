package com.micoach.ai.presentation;

import com.micoach.ai.domain.ChatMessage;
import com.micoach.ai.domain.Conversation;
import com.micoach.ai.domain.GenerationLog;
import com.micoach.ai.domain.Prompt;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * DTOs del módulo ai. Cada verbose class es un contrato de entrada/salida.
 */
public final class AiDtos {

    private AiDtos() {
    }

    // ------------------------- Prompts -------------------------

    public record PromptResponse(Long id, String slug, Integer version, String provider, String model,
                                 String content, Map<String, Object> params, boolean active) {

        static PromptResponse from(Prompt p) {
            return new PromptResponse(p.getId(), p.getSlug(), p.getVersion(), p.getProvider(), p.getModel(),
                    p.getContent(), p.getParams(), p.isActive());
        }
    }

    public record PromptRequest(@NotBlank String slug, String provider, String model, @NotBlank String content,
                                Map<String, Object> params) {
    }

    public record PromptActiveRequest(@NotNull Boolean active) {
    }

    // ------------------------- Conversaciones -------------------------

    public record ChatMessageResponse(Long id, String role, String content, String provider, String model,
                                      Map<String, Object> tokenUsage, Instant createdAt) {

        static ChatMessageResponse from(ChatMessage m) {
            return new ChatMessageResponse(m.getId(), m.getRole(), m.getContent(), m.getProvider(), m.getModel(),
                    m.getTokenUsage(), m.getCreatedAt());
        }
    }

    public record ConversationResponse(Long id, String topic, String status, List<ChatMessageResponse> messages) {

        static ConversationResponse from(Conversation c) {
            return new ConversationResponse(c.getId(), c.getTopic(), c.getStatus(),
                    c.getMessages().stream().map(ChatMessageResponse::from).toList());
        }
    }

    public record ConversationRequest(String topic) {
    }

    public record MessageRequest(@NotBlank String role, @NotBlank String content, String provider, String model,
                                 Map<String, Object> tokenUsage) {
    }

    // ------------------------- Auditoría -------------------------

    public record GenerationLogResponse(Long id, Long userId, String promptSlug, Integer promptVersion,
                                        String provider, String model, Map<String, Object> inputContext,
                                        Map<String, Object> output, Integer durationMs, String status,
                                        Instant createdAt) {

        static GenerationLogResponse from(GenerationLog e) {
            return new GenerationLogResponse(e.getId(), e.getUserId(), e.getPromptSlug(), e.getPromptVersion(),
                    e.getProvider(), e.getModel(), e.getInputContext(), e.getOutput(), e.getDurationMs(),
                    e.getStatus(), e.getCreatedAt());
        }
    }
}
