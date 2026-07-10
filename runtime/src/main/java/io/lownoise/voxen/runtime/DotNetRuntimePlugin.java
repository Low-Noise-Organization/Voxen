package io.lownoise.voxen.runtime;

import java.nio.file.Path;

public class DotNetRuntimePlugin implements RuntimePlugin {

    @Override
    public String runtime() {
        return "dotnet";
    }

    @Override
    public boolean detect(Path projectDir) {
        return projectDir.toFile().listFiles((dir, name) -> name.endsWith(".csproj")
            || name.endsWith(".fsproj")).length > 0;
    }

    @Override
    public String buildCommand(Path projectDir) {
        return "dotnet publish -c Release -o dist";
    }

    @Override
    public String defaultArtifactPattern() {
        return "dist/*.dll";
    }

    @Override
    public String buildImage() {
        return "mcr.microsoft.com/dotnet/sdk:8.0";
    }
}
