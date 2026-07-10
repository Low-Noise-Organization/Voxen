# Contributing to Voxen

Thanks for your interest in contributing! Voxen follows the [Low Noise engineering philosophy](AI_CONTEXT.md).

## Code of Conduct

Be respectful, constructive, and professional.

## How to Contribute

1. **Discuss first** — Open an issue before starting significant work.
2. **Fork and branch** — Create a feature branch from `main`.
3. **Follow the conventions** — See below.
4. **Write tests** — Every public feature needs unit + integration tests.
5. **Write documentation** — Every feature needs explanation, examples, and CLI output.
6. **Open a PR** — Link the related issue.

## Development Setup

- JDK 21+
- Gradle 9.6+ (wrapper included)

```bash
git clone https://github.com/lownoise/voxen
cd voxen
./gradlew build
```

## Coding Conventions

- **Composition over inheritance**
- **Interfaces over implementations**
- **Immutable objects preferred**
- **Small classes, single responsibility**
- **No static state**
- **No reflection unless necessary**
- **No magic values**

See [AI_CONTEXT.md](AI_CONTEXT.md) for the full engineering values.

## Project Structure

```
cli/           CLI commands (picocli)
core/          Language-agnostic project model
plugins/api/   Plugin SPI
plugins/java/  Java plugin (Maven + Gradle)
runtime/       Runtime detection
packaging/     Archive creation
distribution/  Artifact publishing
testing/       Test utilities and integration tests
docs/          User documentation
examples/      Example projects
```

## Testing

```bash
./gradlew test                    # All tests
./gradlew :core:test              # Single module
./gradlew test --tests "*VoxenConfigTest"  # Single test class
```

All tests must pass before merging.

## Documentation

Documentation lives in `docs/` as Markdown files. Every feature requires:

- Explanation
- Examples
- CLI output
- Edge cases
- Troubleshooting

## Pull Request Process

1. Ensure tests pass and coverage doesn't decrease.
2. Update documentation if needed.
3. Add a changelog entry.
4. Request review from a maintainer.

## Questions?

Open a discussion or issue on GitHub.
