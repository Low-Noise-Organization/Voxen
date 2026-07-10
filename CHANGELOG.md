# Changelog

## [0.1.0] - 2026-07-10

### Added
- **Dockerize Engine** — Nuevo módulo `:docker` con pipeline completo de generación de imágenes
  - `BaseImageResolver` — Imagen base óptima por runtime (distroless, slim, aspnet)
  - `DockerfileGenerator` — Dockerfiles multi-stage con labels OCI + Voxen
  - `DockerLabeler` — 18 labels de supply chain embebidos en la imagen
  - `BuildMetadata` — 27 campos de metadatos del build
  - `MultiArchBuilder` — Build multi-arquitectura con buildx
  - `ImageLinter` — Verificación de calidad de imagen (tamaño, capas, root, puertos)
  - `OCIAnnotator` — Anotaciones OCI en manifiesto
  - `VulnerabilityScanner` — Integración con Trivy
  - `ManifestSigner` — Firma con Cosign (keyless + key-based) y Docker Trust GPG
  - `SlsaProvenance` — Atestaciones SLSA v1 (in-toto)
  - `DockerPublisher` — Publicación a Docker Hub, GHCR, ECR, GCR
- **Comandos CLI**
  - `voxen dockerize` — Genera imagen Docker desde artefacto compilado
  - `voxen publish:docker` — Publica imágenes a registros
  - `voxen info` — Información del sistema, plugins y proyecto
- **Multi-lenguaje** — 6 RuntimePlugin implementaciones
  - Java (Maven/Gradle), Node.js (npm), Python (pip/build), Go, Rust, .NET
  - Detección automática del runtime y herramienta de build
  - Imágenes base específicas por lenguaje
- **Enterprise**
  - `PluginSandbox` — Sandboxing legacy con SecurityManager
  - `ProfileConfig` — Perfiles multi-entorno (dev/staging/prod + custom)
  - `AuditLogger` — Log estructurado JSONL en `.voxen/audit.jsonl`
  - `TelemetryCollector` — Métricas locales opt-in
  - `ConsoleProgress` — Spinner + barra de progreso
- **CI/CD**
  - Workflow `dockerize.yml` — Build + dockerize + multi-arch + cosign + trivy
  - Workflow `release.yml` — Release con GHCR push + native image + SBOM
  - Acción compuesta `voxen-build-push` — Reutilizable en cualquier repo
- **Documentación web** — 8 páginas en español con tema oscuro
  - index, install, quickstart, cli, configuration, plugins, enterprise, faq
- **Ejemplos** — `examples/java-library/`, `examples/node-app/`, `examples/python-service/`

### Changed
- Voxen pasa de "Universal Software Packaging Platform" a **"Build Orchestrator + Supply Chain Layer para Docker"**
- `RuntimeDetector` extendido con detección para 6 rúntimes
- `PublisherFactory` extendido con `DockerPublisher`
- Imagen Docker ahora multi-stage, labels OCI estándar, usuario no-root
- 254 tests, 104 tareas de build, cobertura JaCoCo 30%

### Fixed
- Esquema JSON: eliminado `/` del pattern `name` para evitar path traversal
- `@JsonInclude(NON_NULL)` en VoxenConfig para evitar fallos de validación
- Pruebas de disponibilidad de Maven/Gradle ahora portables

### Security
- Integración con Cosign (sigstore) para firmar imágenes
- Escaneo con Trivy y gateo por vulnerabilidades críticas
- SLSA Provenance para cada build
- Imágenes distroless sin shell ni herramientas de build
