package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ArtifactoryPublisherTest {

    @Test
    void shouldFailWithoutCredentials(@TempDir Path tempDir) throws Exception {
        Path artifact = tempDir.resolve("test.jar");
        Files.writeString(artifact, "test");

        ArtifactoryPublisher publisher = new ArtifactoryPublisher();
        PublishResult result = publisher.publish(artifact, "libs-release", "com.example", "test", "1.0");
        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("credentials");
    }

    @Test
    void shouldReturnCorrectName() {
        ArtifactoryPublisher publisher = new ArtifactoryPublisher();
        assertThat(publisher.name()).isEqualTo("artifactory");
    }
}
