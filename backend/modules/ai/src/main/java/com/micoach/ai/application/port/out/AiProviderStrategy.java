package com.micoach.ai.application.port.out;

public interface AiProviderStrategy {
    String providerId();
    String complete(String prompt, ResolvedProvider provider);
}
