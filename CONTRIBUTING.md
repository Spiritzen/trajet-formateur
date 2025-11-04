# Contribuer

## Branches
- `main`: production (protégée)
- `develop`: intégration
- `feat/*`, `fix/*`, `chore/*`: travail quotidien

## Cycle PR
1. Branche `feat/…`
2. Commits **Conventional Commits**
3. PR vers `develop`
4. Revue (checks + relecture), squash & merge

## Conventional Commits
- `feat: …` nouvelle fonctionnalité
- `fix: …` correction de bug
- `docs: …` documentation
- `chore: …` outils/infra
- `refactor: …` changements internes
- `test: …` tests
- `perf: …` perfs

Exemples :
- `feat(user): ajout rôle FORMATEUR/ECOLE`
- `fix(auth): corrige expiration refresh token`

## Code style
- Java: formatage IDE, Javadoc là où utile.
- JS/TS: ESLint + Prettier (ajouté côté frontend).
- `.editorconfig` contrôle les bases.

## Sécurité
- Aucun secret en dur (utiliser `.env`).
- Ne pousse jamais de clés/API dans Git.
