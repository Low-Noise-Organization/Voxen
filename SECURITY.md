# Security Policy

## Reporting a Vulnerability

Report security vulnerabilities to **security@lownoise.io**.

Do **not** report security vulnerabilities via public GitHub issues.

### What to Include

- Description of the vulnerability
- Steps to reproduce
- Affected versions
- Potential impact
- Any suggested fix (if known)

### Response Timeline

- **24 hours**: Acknowledgment of receipt
- **7 days**: Initial assessment and remediation plan
- **30 days**: Fix released (depending on severity)

## Scope

Supported versions: Latest stable release and the previous minor version.

## Security Practices

- All configuration files are validated against a JSON Schema.
- Plugins run in the same JVM — only install plugins from trusted sources.
- Credentials should use environment variables or `~/.voxen/credentials`.
- TLS is enforced for all remote repository interactions.

## Disclosure Policy

We follow coordinated disclosure:

1. Reporter notifies security@lownoise.io
2. We investigate and develop a fix
3. Fix is released and announced
4. Vulnerability is publicly disclosed after 90 days or when a fix is available
