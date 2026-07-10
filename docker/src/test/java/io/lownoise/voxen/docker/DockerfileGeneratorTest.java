package io.lownoise.voxen.docker;

import io.lownoise.voxen.core.ProjectConfig;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class DockerfileGeneratorTest {

    private final DockerfileGenerator generator = new DockerfileGenerator();

    @Test
    void generatesValidDockerfile() {
        var config = new ProjectConfig("my-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .runtime("java")
            .version("1.0.0")
            .artifactName("my-app.jar")
            .build();

        String df = generator.generate(config, metadata);

        assertThat(df).contains("FROM gcr.io/distroless/java21-debian12");
        assertThat(df).contains("WORKDIR /app");
        assertThat(df).contains("COPY my-app.jar /app/my-app.jar");
        assertThat(df).contains("ENTRYPOINT [\"java\", \"-jar\", \"/app/my-app.jar\"]");
        assertThat(df).contains("LABEL");
        assertThat(df).contains("voxen.build.id");
        assertThat(df).contains("org.opencontainers.image");
    }

    @Test
    void dockerfileContainsSBOMCopy() {
        var config = new ProjectConfig("my-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .runtime("java")
            .artifactName("my-app.jar")
            .hasSbom(true)
            .build();

        String df = generator.generate(config, metadata);
        assertThat(df).contains("COPY bom.json");
    }

    @Test
    void dockerfileContainsGpgSignatureCopy() {
        var config = new ProjectConfig("my-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .runtime("java")
            .artifactName("my-app.jar")
            .ascFile("my-app.jar.asc")
            .build();

        String df = generator.generate(config, metadata);
        assertThat(df).contains("my-app.jar.asc");
    }

    @Test
    void nativeImageUsesDifferentEntrypoint() {
        var config = new ProjectConfig("my-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .runtime("java")
            .nativeImage(true)
            .build();

        String df = generator.generate(config, metadata);
        assertThat(df).contains("[\"/app/my-app\"]");
    }

    @Test
    void generatesBuildStageWhenNeeded() {
        var config = new ProjectConfig("my-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .runtime("java")
            .needsBuildStage(true)
            .buildImage("eclipse-temurin:21-jdk-alpine")
            .buildCommand("mvn package")
            .build();

        String df = generator.generate(config, metadata);
        assertThat(df).contains("FROM eclipse-temurin:21-jdk-alpine AS builder");
        assertThat(df).contains("RUN mvn package");
    }

    @Test
    void nodeRuntimeGeneratesNodeEntrypoint() {
        var config = new ProjectConfig("my-app", "node", "zip", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .runtime("node")
            .artifactName("app.zip")
            .build();

        String df = generator.generate(config, metadata);
        assertThat(df).contains("gcr.io/distroless/nodejs20-debian12");
        assertThat(df).contains("[\"node\", \"/app/app.zip\"]");
    }

    @Test
    void includesExposeWhenPortSet() {
        var config = new ProjectConfig("my-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("my-app")
            .runtime("java")
            .artifactName("my-app.jar")
            .port("8080")
            .build();

        String df = generator.generate(config, metadata);
        assertThat(df).contains("EXPOSE 8080");
    }
}
