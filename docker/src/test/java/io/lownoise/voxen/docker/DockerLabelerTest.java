package io.lownoise.voxen.docker;

import io.lownoise.voxen.core.ProjectConfig;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DockerLabelerTest {

    @Test
    void generatesAllOCILabels() {
        var config = new ProjectConfig("test-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("test-app")
            .version("1.0.0")
            .commit("abc123")
            .repository("https://github.com/user/test-app")
            .runtime("java")
            .buildId("build-1")
            .pluginName("java")
            .profile("prod")
            .checksumSha256("aabbccdd")
            .signed(true)
            .signingKey("KEY123")
            .build();

        List<String> labels = DockerLabeler.generateLabels(metadata, config);

        assertThat(labels).anyMatch(l -> l.startsWith("org.opencontainers.image.created="));
        assertThat(labels).anyMatch(l -> l.contains("org.opencontainers.image.version=\"1.0.0\""));
        assertThat(labels).anyMatch(l -> l.contains("org.opencontainers.image.revision=\"abc123\""));
        assertThat(labels).anyMatch(l -> l.contains("org.opencontainers.image.source=\"https://github.com/user/test-app\""));
        assertThat(labels).anyMatch(l -> l.contains("org.opencontainers.image.title=\"test-app\""));
        assertThat(labels).anyMatch(l -> l.contains("voxen.build.id=\"build-1\""));
        assertThat(labels).anyMatch(l -> l.contains("voxen.build.profile=\"prod\""));
        assertThat(labels).anyMatch(l -> l.contains("voxen.build.runtime=\"java\""));
        assertThat(labels).anyMatch(l -> l.contains("voxen.checksum.sha256=\"aabbccdd\""));
        assertThat(labels).anyMatch(l -> l.contains("voxen.signed=true"));
        assertThat(labels).anyMatch(l -> l.contains("voxen.signed.key=\"KEY123\""));
        assertThat(labels).anyMatch(l -> l.contains("voxen.sbom.filename=bom.json"));
        assertThat(labels).anyMatch(l -> l.contains("voxen.sbom.format=CycloneDX-1.6"));
    }

    @Test
    void unsignedImageOmitsSigningLabels() {
        var config = new ProjectConfig("test-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("test-app")
            .runtime("java")
            .signed(false)
            .build();

        List<String> labels = DockerLabeler.generateLabels(metadata, config);
        assertThat(labels).anyMatch(l -> l.contains("voxen.signed=false"));
    }

    @Test
    void withoutChecksumsOmitsChecksumLabels() {
        var config = new ProjectConfig("test-app", "java", "jar", "dist", Path.of("/app"));
        var metadata = BuildMetadata.builder()
            .projectName("test-app")
            .runtime("java")
            .build();

        List<String> labels = DockerLabeler.generateLabels(metadata, config);
        assertThat(labels).noneMatch(l -> l.startsWith("voxen.checksum."));
    }
}
