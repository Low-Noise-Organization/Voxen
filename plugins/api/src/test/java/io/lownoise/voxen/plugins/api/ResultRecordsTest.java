package io.lownoise.voxen.plugins.api;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ResultRecordsTest {

    @Test
    void shouldCreateSuccessfulBuildResult() {
        List<Path> artifacts = List.of(Path.of("target/app.jar"));
        BuildResult result = BuildResult.success(artifacts, 1500, "Build OK");
        assertThat(result.success()).isTrue();
        assertThat(result.artifacts()).containsExactly(Path.of("target/app.jar"));
        assertThat(result.durationMs()).isEqualTo(1500);
        assertThat(result.output()).isEqualTo("Build OK");
    }

    @Test
    void shouldCreateFailedBuildResult() {
        BuildResult result = BuildResult.failure("Missing pom.xml");
        assertThat(result.success()).isFalse();
        assertThat(result.artifacts()).isEmpty();
        assertThat(result.output()).isEqualTo("Missing pom.xml");
    }

    @Test
    void shouldCreateSuccessfulPackageResult() {
        PackageResult result = PackageResult.success(Path.of("dist/app.jar"), "jar", 1024, "Packaged");
        assertThat(result.success()).isTrue();
        assertThat(result.packagePath()).isEqualTo(Path.of("dist/app.jar"));
        assertThat(result.format()).isEqualTo("jar");
        assertThat(result.sizeBytes()).isEqualTo(1024);
    }

    @Test
    void shouldCreateFailedPackageResult() {
        PackageResult result = PackageResult.failure("No artifacts found");
        assertThat(result.success()).isFalse();
        assertThat(result.packagePath()).isNull();
    }

    @Test
    void shouldCreateSuccessfulPublishResult() {
        PublishResult result = PublishResult.success("central", "https://repo/artifact", "Published");
        assertThat(result.success()).isTrue();
        assertThat(result.targetRepository()).isEqualTo("central");
        assertThat(result.artifactUrl()).isEqualTo("https://repo/artifact");
    }

    @Test
    void shouldCreateFailedPublishResult() {
        PublishResult result = PublishResult.failure("Auth failed");
        assertThat(result.success()).isFalse();
        assertThat(result.targetRepository()).isNull();
    }

    @Test
    void shouldCreateSuccessfulDeployResult() {
        DeployResult result = DeployResult.success("production", "https://app.example.com", "Deployed");
        assertThat(result.success()).isTrue();
        assertThat(result.targetEnvironment()).isEqualTo("production");
        assertThat(result.deploymentUrl()).isEqualTo("https://app.example.com");
    }

    @Test
    void shouldCreateFailedDeployResult() {
        DeployResult result = DeployResult.failure("Connection refused");
        assertThat(result.success()).isFalse();
        assertThat(result.targetEnvironment()).isNull();
    }

    @Test
    void buildResultShouldCopyArtifacts() {
        List<Path> original = new java.util.ArrayList<>(List.of(Path.of("a.jar")));
        BuildResult result = BuildResult.success(original, 100, "OK");
        original.add(Path.of("b.jar"));
        assertThat(result.artifacts()).hasSize(1);
    }
}
