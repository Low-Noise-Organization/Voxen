package io.lownoise.voxen.runtime;

import java.nio.file.Path;

public class RustRuntimePlugin implements RuntimePlugin {

    @Override
    public String runtime() {
        return "rust";
    }

    @Override
    public boolean detect(Path projectDir) {
        return projectDir.resolve("Cargo.toml").toFile().exists();
    }

    @Override
    public String buildCommand(Path projectDir) {
        return "cargo build --release";
    }

    @Override
    public String defaultArtifactPattern() {
        return "target/release/*";
    }

    @Override
    public String buildImage() {
        return "rust:1.77-slim-bookworm";
    }
}
