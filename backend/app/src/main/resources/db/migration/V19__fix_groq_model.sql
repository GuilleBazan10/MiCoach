-- =====================================================================
-- MiCoach — V19: Groq deprecó los modelos Llama (incluido
-- llama-3.3-70b-versatile, sembrado en V11) — cualquier generación con IA
-- falla con "model_not_found" si Groq está activo. Verificado contra
-- console.groq.com/docs/models (2026-08): los modelos de propósito
-- general vigentes en el tier de producción son openai/gpt-oss-120b y
-- openai/gpt-oss-20b. Se usa el de 120b por mejor calidad de JSON
-- estructurado (ver docs/10-recomendaciones-coach-nutricion.md § A.1/A.2)
-- — Groq corre en su propio hardware (LPU), el modelo más grande no
-- cuesta la latencia que costaría en un proveedor tradicional.
-- =====================================================================
UPDATE ai_provider_configs
SET model = 'openai/gpt-oss-120b'
WHERE provider = 'groq';
