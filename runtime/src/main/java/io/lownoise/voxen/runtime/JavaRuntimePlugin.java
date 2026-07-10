package io.lownoise.voxen.runtime;

import java.nio.file.Path;

public class JavaRuntimePlugin implements RuntimePlugin {

    @Override
    public String runtime() {
        return "java";
    }

    @Override
    public boolean detect(Path projectDir) {
        return projectDir.resolve("pom.xml").toFile().exists()
            || projectDir.resolve("build.gradle").toFile().exists()
            || projectDir.resolve("build.gradle.kts").toFile().exists();
    }

    @Override
    public String buildCommand(Path projectDir) {
        if (projectDir.resolve("pom.xml").toFile().exists()) {
            return "mvn package -DskipTests";
        }
        if (projectDir.resolve("gradlew").toFile().exists()) {
            return "./gradlew build -x test";
        }
        return "gradle build -x test";
    }

    @Override
    public String defaultArtifactPattern() {
        return "target/*.jar";
    }

    @Override
    public String buildImage() {
        return "eclipse-temurin:21-jdk-alpine";
    }
}
