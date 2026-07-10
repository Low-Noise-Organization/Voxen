package io.lownoise.voxen.docker;

import io.lownoise.voxen.distribution.PublishResult;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DockerPublisherTest {

    private final DockerPublisher publisher = new DockerPublisher();

    @Test
    void nameReturnsDocker() {
        assertThat(publisher.name()).isEqualTo("docker");
    }

    @Test
    void publishFailsWithoutDocker() {
        PublishResult result = publisher.publish(
            Path.of("nonexistent:latest"), "ghcr.io/user",
            "io.voxen", "my-app", "1.0.0");
        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("Docker publish failed");
    }

    @Test
    void publishMultiArchFailsWithoutDocker() {
        PublishResult result = publisher.publishMultiArch(
            Path.of("nonexistent:latest"), "ghcr.io/user",
            "my-app", "1.0.0", List.of("linux/amd64", "linux/arm64"));
        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("Multi-arch publish failed");
    }
}
