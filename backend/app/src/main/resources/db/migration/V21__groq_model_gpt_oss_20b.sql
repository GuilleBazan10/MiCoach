-- =====================================================================
-- MiCoach — V21: V19 dejó Groq en openai/gpt-oss-120b tras la
-- deprecación de los Llama. Después, en la práctica, se probó
-- llama-3.3-70b-versatile (también deprecado por Groq, mismo síntoma
-- "model_not_found") y volvió a caer en gpt-oss-120b/-20b como únicas
-- opciones vigentes de propósito general en el tier free (verificado
-- contra console.groq.com/docs/models y /docs/rate-limits, 2026-08-18).
--
-- gpt-oss-120b es un modelo de razonamiento: gasta una porción variable
-- del presupuesto de tokens "pensando" antes de escribir la respuesta
-- visible, y el tier free de Groq solo da 8000 tokens/minuto para estos
-- modelos — en la práctica esto causó generaciones cortadas a mitad del
-- JSON con planes de varios días (ver docs/10-recomendaciones-coach-nutricion.md
-- § I.1/I.5 y AiService.markGenerationPartial). gpt-oss-20b es más rápido
-- (1000 tok/s vs 500) y en la prueba real de esta sesión generó un plan
-- de 5 días en 2.3s sin cortes, con el mismo límite de 8000 TPM.
-- =====================================================================
UPDATE ai_provider_configs
SET model = 'openai/gpt-oss-20b'
WHERE provider = 'groq';
