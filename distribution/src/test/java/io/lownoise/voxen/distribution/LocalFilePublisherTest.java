package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class LocalFilePublisherTest {

    @Test
    void shouldPublishFileToLocalRepository(@TempDir Path tempDir) throws Exception {
        Path artifact = tempDir.resolve("app.jar");
        Files.writeString(artifact, "fake jar");
        Path repo = tempDir.resolve("repo");

        LocalFilePublisher publisher = new LocalFilePublisher();
        PublishResult result = publisher.publish(artifact, repo.toString(),
            "com.example", "my-app", "1.0.0");

        assertThat(result.success()).isTrue();
        assertThat(result.target()).contains("repo");
        assertThat(Files.exists(Path.of(result.target(), "app.jar"))).isTrue();
    }

    @Test
    void shouldPublishDirectory(@TempDir Path tempDir) throws Exception {
        Path dir = tempDir.resolve("dist");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("app.jar"), "content");

        LocalFilePublisher publisher = new LocalFilePublisher();
        PublishResult result = publisher.publish(dir, tempDir.resolve("repo").toString(),
            "com.example", "my-app", "1.0.0");

        assertThat(result.success()).isTrue();
    }

    @Test
    void shouldHandleMissingArtifact(@TempDir Path tempDir) {
        LocalFilePublisher publisher = new LocalFilePublisher();
        PublishResult result = publisher.publish(tempDir.resolve("nonexistent.jar"),
            "repo", "com.example", "app", "1.0");

        assertThat(result.success()).isFalse();
    }
}
