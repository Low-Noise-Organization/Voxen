package io.lownoise.voxen.docker;

public class BaseImageResolver {

    public BaseImage resolve(String runtime, boolean nativeImage) {
        return switch (runtime.toLowerCase()) {
            case "java" -> nativeImage
                ? new BaseImage("gcr.io/distroless/cc", "nonroot", "base")
                : new BaseImage("gcr.io/distroless/java21-debian12", "nonroot", "java");
            case "kotlin" -> new BaseImage("gcr.io/distroless/java21-debian12", "nonroot", "java");
            case "node" -> new BaseImage("gcr.io/distroless/nodejs20-debian12", "nonroot", "node");
            case "python" -> new BaseImage("python", "3.11-slim", "python");
            case "go" -> new BaseImage("gcr.io/distroless/static-debian12", "nonroot", "base");
            case "rust" -> new BaseImage("gcr.io/distroless/cc", "nonroot", "base");
            case "dotnet" -> new BaseImage("mcr.microsoft.com/dotnet/aspnet", "8.0", "dotnet");
            default -> new BaseImage("debian", "bookworm-slim", "base");
        };
    }

    public record BaseImage(String image, String tag, String type) {
        public String fullName() {
            return image + ":" + tag;
        }
    }
}
