package com.micoach.ai.application.port.out;

public record ResolvedProvider(
    String provider,
    String baseUrl,
    String apiKey,
    String model
) {}
