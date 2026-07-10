#!/usr/bin/env bash
set -euo pipefail

REPO="lownoise/voxen"
VERSION="${1:-latest}"
INSTALL_DIR="${INSTALL_DIR:-/usr/local/bin}"

# --- Colores ---
RED='\033[0;31m'
GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

info()  { echo -e "${CYAN}[INFO]${NC} $1"; }
ok()    { echo -e "${GREEN}[OK]${NC} $1"; }
err()   { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# --- Detectar SO/Arquitectura ---
OS=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)

case "$ARCH" in
    x86_64|amd64) ARCH="amd64" ;;
    aarch64|arm64) ARCH="arm64" ;;
    *) err "Arquitectura no soportada: $ARCH" ;;
esac

case "$OS" in
    linux|darwin) ;;
    *) err "Sistema operativo no soportado: $OS" ;;
esac

# --- Determinar versión ---
if [ "$VERSION" = "latest" ]; then
    info "Obteniendo última versión..."
    VERSION=$(curl -sSL "https://api.github.com/repos/$REPO/releases/latest" \
        | grep '"tag_name":' | cut -d'"' -f4)
    if [ -z "$VERSION" ]; then
        err "No se pudo determinar la última versión"
    fi
    ok "Última versión: $VERSION"
fi

# --- Descargar ---
JAR_URL="https://github.com/$REPO/releases/download/$VERSION/voxen-$VERSION.jar"
JAR_NAME="voxen.jar"

info "Descargando Voxen $VERSION ($OS/$ARCH)..."
curl -sSL "$JAR_URL" -o "/tmp/$JAR_NAME"
ok "Descargado: $JAR_NAME ($(du -h "/tmp/$JAR_NAME" | cut -f1))"

# --- Instalar ---
if [ ! -d "$INSTALL_DIR" ]; then
    info "Creando $INSTALL_DIR..."
    mkdir -p "$INSTALL_DIR"
fi

install -m 755 "/tmp/$JAR_NAME" "$INSTALL_DIR/$JAR_NAME"

# --- Crear wrapper ---
WRAPPER="$INSTALL_DIR/voxen"
cat > "$WRAPPER" << 'WRAPPER_EOF'
#!/usr/bin/env bash
exec java -jar "$(dirname "$0")/voxen.jar" "$@"
WRAPPER_EOF
chmod +x "$WRAPPER"

ok "Instalado: $WRAPPER"
info "Ejecuta 'voxen --help' para comenzar"

# --- Limpiar ---
rm -f "/tmp/$JAR_NAME"

# --- Generar completions (opcional) ---
if command -v voxen &>/dev/null; then
    SHELL_NAME=$(basename "${SHELL:-bash}")
    COMP_DIR="${XDG_DATA_HOME:-$HOME/.local/share}/$SHELL_NAME/completions"
    mkdir -p "$COMP_DIR" 2>/dev/null || true
    case "$SHELL_NAME" in
        bash)
            voxen completion bash > "$COMP_DIR/voxen.bash" 2>/dev/null || true
            ok "Completions bash generados en $COMP_DIR/voxen.bash"
            ;;
        zsh)
            voxen completion zsh > "$COMP_DIR/_voxen" 2>/dev/null || true
            ok "Completions zsh generados en $COMP_DIR/_voxen"
            ;;
    esac
fi

echo ""
info "Voxen $VERSION instalado correctamente."
echo "  Usa: voxen init -n my-app -r java"
echo "       voxen build && voxen dockerize --tag user/app:1.0 --push"
