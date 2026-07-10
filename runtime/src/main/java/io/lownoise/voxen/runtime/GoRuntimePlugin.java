package io.lownoise.voxen.runtime;

import java.nio.file.Path;

public class GoRuntimePlugin implements RuntimePlugin {

    @Override
    public String runtime() {
        return "go";
    }

    @Override
    public boolean detect(Path projectDir) {
        return projectDir.resolve("go.mod").toFile().exists();
    }

    @Override
    public String buildCommand(Path projectDir) {
        return "go build -o dist/ ./...";
    }

    @Override
    public String defaultArtifactPattern() {
        return "dist/*";
    }

    @Override
    public String buildImage() {
        return "golang:1.22-alpine";
    }
}
