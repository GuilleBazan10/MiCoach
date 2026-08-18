// =====================================================================
// MiCoach — Validación de formularios de auth (Zod).
// Espejo de la validación del backend (docs/03-api-contracts.md): email
// válido, password >= 8 caracteres.
// =====================================================================
import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().min(1, 'Ingresá tu email').email('Email inválido'),
  password: z.string().min(1, 'Ingresá tu contraseña'),
});
export type LoginFormValues = z.infer<typeof loginSchema>;

export const registerSchema = z
  .object({
    email: z.string().min(1, 'Ingresá tu email').email('Email inválido'),
    password: z.string().min(8, 'La contraseña debe tener al menos 8 caracteres'),
    confirmPassword: z.string().min(1, 'Confirmá tu contraseña'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Las contraseñas no coinciden',
    path: ['confirmPassword'],
  });
export type RegisterFormValues = z.infer<typeof registerSchema>;
