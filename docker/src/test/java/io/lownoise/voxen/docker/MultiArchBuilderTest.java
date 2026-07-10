package io.lownoise.voxen.docker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MultiArchBuilderTest {

    private final MultiArchBuilder builder = new MultiArchBuilder();

    @Test
    void buildReturnsNonZeroForMissingDocker(@TempDir Path tempDir) {
        int exit = builder.build(tempDir, "test:latest", List.of("linux/amd64"), false);
        assertThat(exit).isNotZero();
    }

    @Test
    void buildWithDefaultPlatforms(@TempDir Path tempDir) {
        int exit = builder.build(tempDir, "test:latest", null, false);
        assertThat(exit).isNotZero();
    }

    @Test
    void buildWithPushAndDockerfile(@TempDir Path tempDir) {
        int exit = builder.build(tempDir, "test:latest",
            List.of("linux/amd64", "linux/arm64"), true, tempDir.resolve("Dockerfile").toString());
        assertThat(exit).isNotZero();
    }
}
