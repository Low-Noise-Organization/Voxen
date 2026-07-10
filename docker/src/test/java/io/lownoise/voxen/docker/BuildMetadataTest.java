package io.lownoise.voxen.docker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BuildMetadataTest {

    @Test
    void builderSetsDefaults() {
        BuildMetadata m = BuildMetadata.builder().build();

        assertThat(m.buildId()).isNotEmpty();
        assertThat(m.timestamp()).isNotEmpty();
        assertThat(m.voxenVersion()).isEqualTo("0.1.0-SNAPSHOT");
        assertThat(m.projectName()).isEqualTo("app");
        assertThat(m.version()).isEqualTo("latest");
        assertThat(m.runtime()).isEqualTo("java");
        assertThat(m.profile()).isEqualTo("dev");
    }

    @Test
    void builderOverridesAllFields() {
        BuildMetadata m = BuildMetadata.builder()
            .buildId("custom-id")
            .projectName("my-app")
            .version("2.0.0")
            .runtime("node")
            .profile("prod")
            .checksumSha256("abc123")
            .signed(true)
            .signingKey("KEY456")
            .nativeImage(true)
            .port("3000")
            .build();

        assertThat(m.buildId()).isEqualTo("custom-id");
        assertThat(m.projectName()).isEqualTo("my-app");
        assertThat(m.version()).isEqualTo("2.0.0");
        assertThat(m.runtime()).isEqualTo("node");
        assertThat(m.profile()).isEqualTo("prod");
        assertThat(m.checksumSha256()).isEqualTo("abc123");
        assertThat(m.signed()).isTrue();
        assertThat(m.signingKey()).isEqualTo("KEY456");
        assertThat(m.nativeImage()).isTrue();
        assertThat(m.port()).isEqualTo("3000");
    }

    @Test
    void hasSbomFlag() {
        BuildMetadata m = BuildMetadata.builder().hasSbom(true).build();
        assertThat(m.hasSbom()).isTrue();

        m = BuildMetadata.builder().hasSbom(false).build();
        assertThat(m.hasSbom()).isFalse();
    }
}
