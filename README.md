# Trajet Formateur

Plateforme pro pour gérer les formateurs, leurs affectations et les trajets optimisés vers des écoles partenaires.

## Monorepo
- `backend/` : Spring Boot (Java 21), modules Maven.
- `frontend/` : React + Vite.
- `docker/` : docker-compose (PostgreSQL, MailHog) pour le dev.

## Démarrer
- Prérequis: Git, JDK 21, Node 18+, Docker Desktop.
- Lire `CONTRIBUTING.md` pour les conventions (commits, branches, PR).

## Qualité & Sécurité
- Conventional Commits, PR review obligatoire.
- Secrets en variables d’environnement (jamais dans Git).
