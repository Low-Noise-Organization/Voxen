package io.lownoise.voxen.runtime;

import java.nio.file.Path;

public class NodeRuntimePlugin implements RuntimePlugin {

    @Override
    public String runtime() {
        return "node";
    }

    @Override
    public boolean detect(Path projectDir) {
        return projectDir.resolve("package.json").toFile().exists();
    }

    @Override
    public String buildCommand(Path projectDir) {
        return "npm run build";
    }

    @Override
    public String defaultArtifactPattern() {
        return "dist/*.zip";
    }

    @Override
    public String buildImage() {
        return "node:20-alpine";
    }
}
