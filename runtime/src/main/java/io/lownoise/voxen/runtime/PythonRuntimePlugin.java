package io.lownoise.voxen.runtime;

import java.nio.file.Path;

public class PythonRuntimePlugin implements RuntimePlugin {

    @Override
    public String runtime() {
        return "python";
    }

    @Override
    public boolean detect(Path projectDir) {
        return projectDir.resolve("setup.py").toFile().exists()
            || projectDir.resolve("pyproject.toml").toFile().exists()
            || projectDir.resolve("requirements.txt").toFile().exists();
    }

    @Override
    public String buildCommand(Path projectDir) {
        if (projectDir.resolve("setup.py").toFile().exists()) {
            return "python setup.py sdist bdist_wheel";
        }
        if (projectDir.resolve("pyproject.toml").toFile().exists()) {
            return "python -m build";
        }
        return "pip install -r requirements.txt";
    }

    @Override
    public String defaultArtifactPattern() {
        return "dist/*.whl";
    }

    @Override
    public String buildImage() {
        return "python:3.11-slim";
    }
}
