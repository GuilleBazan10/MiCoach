package com.micoach.ai.presentation;

import com.micoach.shared.security.AuthenticatedUser;
import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.ai.application.port.in.AiUseCase.GenerationLogFilter;
import com.micoach.ai.application.port.in.AiUseCase.MessageData;
import com.micoach.ai.application.port.in.AiUseCase.PromptData;
import com.micoach.ai.presentation.AiDtos.ChatMessageResponse;
import com.micoach.ai.presentation.AiDtos.ConversationRequest;
import com.micoach.ai.presentation.AiDtos.ConversationResponse;
import com.micoach.ai.presentation.AiDtos.GenerationLogResponse;
import com.micoach.ai.presentation.AiDtos.MessageRequest;
import com.micoach.ai.presentation.AiDtos.PromptActiveRequest;
import com.micoach.ai.presentation.AiDtos.PromptRequest;
import com.micoach.ai.presentation.AiDtos.PromptResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contratos REST del módulo ai (base path /api/v1/ai). Base técnica (persistencia +
 * API); sin integración real con un proveedor de IA todavía (llega en Fase 4).
 */
@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiUseCase useCase;

    public AiController(AiUseCase useCase) {
        this.useCase = useCase;
    }

    // ------------------------- Prompts -------------------------

    @GetMapping("/prompts")
    public List<PromptResponse> listPrompts(@RequestParam(required = false) String slug,
                                            @RequestParam(defaultValue = "false") boolean activeOnly) {
        return useCase.listPrompts(slug, activeOnly).stream().map(PromptResponse::from).toList();
    }

    @GetMapping("/prompts/{promptId}")
    public PromptResponse getPrompt(@PathVariable Long promptId) {
        return PromptResponse.from(useCase.getPrompt(promptId));
    }

    @PostMapping("/prompts")
    @ResponseStatus(HttpStatus.CREATED)
    public PromptResponse createPrompt(@Valid @RequestBody PromptRequest request) {
        PromptData data = new PromptData(request.slug(), request.provider(), request.model(), request.content(),
                request.params());
        return PromptResponse.from(useCase.createPrompt(data));
    }

    @PutMapping("/prompts/{promptId}/active")
    public PromptResponse setPromptActive(@PathVariable Long promptId,
                                          @Valid @RequestBody PromptActiveRequest request) {
        return PromptResponse.from(useCase.setPromptActive(promptId, request.active()));
    }

    // ------------------------- Conversaciones -------------------------

    @GetMapping("/conversations")
    public List<ConversationResponse> listConversations(@AuthenticationPrincipal AuthenticatedUser user,
                                                         @RequestParam(required = false) String topic) {
        return useCase.listConversations(user.id(), topic).stream().map(ConversationResponse::from).toList();
    }

    @GetMapping("/conversations/{conversationId}")
    public ConversationResponse getConversation(@AuthenticationPrincipal AuthenticatedUser user,
                                                @PathVariable Long conversationId) {
        return ConversationResponse.from(useCase.getConversation(user.id(), conversationId));
    }

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse createConversation(@AuthenticationPrincipal AuthenticatedUser user,
                                                    @RequestBody ConversationRequest request) {
        return ConversationResponse.from(useCase.createConversation(user.id(), request.topic()));
    }

    @PutMapping("/conversations/{conversationId}/archive")
    public ConversationResponse archiveConversation(@AuthenticationPrincipal AuthenticatedUser user,
                                                     @PathVariable Long conversationId) {
        return ConversationResponse.from(useCase.archiveConversation(user.id(), conversationId));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageResponse addMessage(@AuthenticationPrincipal AuthenticatedUser user,
                                          @PathVariable Long conversationId,
                                          @Valid @RequestBody MessageRequest request) {
        MessageData data = new MessageData(request.role(), request.content(), request.provider(),
                request.model(), request.tokenUsage());
        return ChatMessageResponse.from(useCase.addMessage(user.id(), conversationId, data));
    }

    // ------------------------- Auditoría -------------------------

    @GetMapping("/generation-logs")
    public List<GenerationLogResponse> listGenerationLogs(@RequestParam(required = false) Long userId,
                                                           @RequestParam(required = false) String promptSlug) {
        return useCase.listGenerationLogs(new GenerationLogFilter(userId, promptSlug)).stream()
                .map(GenerationLogResponse::from).toList();
    }
}
