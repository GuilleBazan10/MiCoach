# MiCoach — Kubernetes (PLAN)

> **Estado: documentación únicamente.** En esta etapa NO se implementa Kubernetes.
> Justificación: complejidad operativa sin necesidad para un proyecto de curso; el
> monolito modular + Docker Compose cubre el desarrollo y la demo.

## Cuándo dar el paso

Cuando haya (al menos) un despliegue estable y la carga lo justifique:
1. Tráfico/usuarios reales y necesidad de alta disponibilidad.
2. Necesidad de extraer módulos a microservicios (ver ADR-001).
3. Requisito académico/examen específico de K8s.

## Manifiestos previstos (Fase 6 — solo como plan)

```
infra/k8s/
├── namespace.yaml          # namespace "micoach"
├── backend-deployment.yaml # Deployment + Service + ConfigMap + Secret
├── infra-deployments/      # postgres, redis, rabbitmq, minio (o usar operadores/managed)
├── ingress.yaml            # nginx-ingress
└── README.md               # comandos kubectl y guía de instalación local (kind/k3s)
```

## Comandos base para probar local

```bash
# con kind o k3s
kind create cluster --name micoach
kubectl apply -f infra/k8s/
kubectl get pods -n micoach
```

## Alternativas de despliegue en producción (gratuitas)

- **VPS + Docker Compose** (recomendado para empezar): un VPS barato/free tier
  (Oracle Cloud Free Tier, AWS Free Tier) con Docker Compose. Simple, un comando.
- **K3s** (Kubernetes ligero) en el mismo VPS cuando se necesite.
- Web: build de Flutter estático servido por Nginx en el mismo VPS, o Vercel (free tier).

## Pendiente

- Dockerfiles multi-stage (Fase 6).
- Secrets via `.env` → `kubectl create secret generic`.
- ConfigMaps por perfil (dev/test/prod).
