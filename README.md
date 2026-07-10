# Voxen

> Build Orchestrator + Supply Chain Layer para Docker.

Voxen se sienta **delante de Docker** y cubre sus carencias: build quality, trazabilidad, supply chain, imágenes mínimas multi-lenguaje.

```bash
# Un pipeline, dos comandos:
voxen init -n my-app -r java
voxen build && voxen dockerize --tag ghcr.io/user/my-app:1.0 --push
```

## Quick Start

```bash
# Instalar
curl -sSL https://raw.githubusercontent.com/lownoise/voxen/main/install.sh | bash

# Crear proyecto
voxen init -n my-app -r java
cd my-app

# Build + Dockerize + Push (pipeline completo)
voxen build --profile prod
voxen dockerize --tag ghcr.io/user/my-app:1.0 --push --sign --lint
```

## Features

### Build Orchestrator
- **Multi-lenguaje** — Java, Node.js, Python, Go, Rust, .NET detectados automáticamente
- **Build tool auto-detection** — Maven, Gradle, npm, pip, go, cargo, dotnet
- **Plugins** — Arquitectura SPI para lenguajes adicionales
- **Perfiles** — `dev`, `staging`, `prod` con configuraciones diferenciadas

### Dockerize Engine
- **`voxen dockerize`** — Genera imágenes Docker óptimas desde artefactos ya compilados
- **BaseImageResolver** — Imagen base óptima según runtime (distroless, slim, aspnet)
- **Multi-stage** automático — Sin build tools en la imagen final
- **Multi-arch** — `linux/amd64` + `linux/arm64` con `docker buildx`

### Supply Chain
- **SBOM** — CycloneDX 1.6 embebido como label y dentro de la imagen
- **Cosign signing** — Firma keyless (sigstore) o con clave privada
- **SLSA Provenance** — Atestaciones in-toto SLSA v1 para cada build
- **Trivy scanning** — Escaneo de vulnerabilidades con gateo por severidad
- **Image Linter** — Verifica tamaño, capas, usuario no-root, puertos, labels
- **OCI Annotations** — Todas las anotaciones OCI estándar en el manifiesto

### Enterprise
- **Audit logging** — Traza estructurada JSONL de cada operación
- **Telemetry** — Métricas locales opt-in
- **Plugin Sandbox** — Aislamiento legacy con SecurityManager
- **GitHub Actions** — Workflows oficiales para CI/CD

## Documentación

- [CLI Reference](docs/cli.html)
- [Instalación](docs/install.html)
- [Quick Start](docs/quickstart.html)
- [Configuración](docs/configuration.html)
- [Plugins](docs/plugins.html)
- [Enterprise / Supply Chain](docs/enterprise.html)
- [FAQ](docs/faq.html)

## Project Structure

```
voxen/
├── cli/           # CLI (picocli) — 8 subcomandos
├── core/          # Modelo de proyecto, configuración, auditoría
├── docker/        # Dockerize Engine + Supply Chain
├── plugins/
│   ├── api/       # SPI de plugins
│   └── java/      # Plugin Java (Maven + Gradle)
├── runtime/       # RuntimePlugin — 6 implementaciones
├── packaging/     # Archivos (zip, tar.gz, tar.bz2)
├── distribution/  # Publicación y deploy
├── testing/       # Testing utilities
├── docs/          # Documentación web
└── examples/      # Proyectos de ejemplo
```

## Development

```bash
./gradlew build                 # Compilar + testear
./gradlew test                  # Solo tests
./gradlew :cli:jar              # Generar JAR ejecutable
./gradlew qualityCheck          # Tests + cobertura + PMD
./gradlew generateCompletions   # Completions bash/zsh
```

**Requisitos:** JDK 21+, Gradle 9.6+ (wrapper incluido), Docker (para `dockerize`).

## Licencia

Apache 2.0 — [LICENSE](LICENSE).

## Seguridad

Reportar vulnerabilidades a security@lownoise.io — [SECURITY.md](SECURITY.md).
