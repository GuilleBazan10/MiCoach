package com.micoach.ai.presentation;

import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.ai.application.port.in.AiUseCase.ProviderConfigData;
import com.micoach.ai.presentation.AiProviderConfigDtos.ProviderConfigRequest;
import com.micoach.ai.presentation.AiProviderConfigDtos.ProviderConfigResponse;
import com.micoach.ai.presentation.AiProviderConfigDtos.ProviderTestResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Panel de admin para elegir y configurar qué proveedor de IA usa la app (base path
 * /api/v1/admin/ai/providers). Los proveedores son fijos (sembrados en V11): acá solo se
 * editan (baseUrl/model/api key/enabled) y se activa uno a la vez.
 */
@RestController
@RequestMapping("/api/v1/admin/ai/providers")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AiProviderConfigController {

    private final AiUseCase useCase;

    public AiProviderConfigController(AiUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public List<ProviderConfigResponse> list() {
        return useCase.listProviderConfigs().stream().map(ProviderConfigResponse::from).toList();
    }

    @PutMapping("/{provider}")
    public ProviderConfigResponse update(@PathVariable String provider,
                                         @Valid @RequestBody ProviderConfigRequest request) {
        ProviderConfigData data = new ProviderConfigData(request.displayName(), request.baseUrl(), request.model(),
                request.apiKey(), request.enabled());
        return ProviderConfigResponse.from(useCase.updateProviderConfig(provider, data));
    }

    @PostMapping("/{provider}/activate")
    public ProviderConfigResponse activate(@PathVariable String provider) {
        return ProviderConfigResponse.from(useCase.activateProvider(provider));
    }

    @PostMapping("/{provider}/test")
    public ProviderTestResponse test(@PathVariable String provider) {
        return ProviderTestResponse.from(useCase.testProvider(provider));
    }
}
