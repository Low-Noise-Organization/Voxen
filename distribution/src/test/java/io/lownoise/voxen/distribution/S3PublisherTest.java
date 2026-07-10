package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class S3PublisherTest {

    @Test
    void shouldFailWithoutCredentials(@TempDir Path tempDir) throws Exception {
        Path artifact = tempDir.resolve("test.jar");
        Files.writeString(artifact, "test");

        S3Publisher publisher = new S3Publisher();
        PublishResult result = publisher.publish(artifact, "my-bucket", "com.example", "test", "1.0");
        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("credentials");
    }

    @Test
    void shouldReturnCorrectName() {
        S3Publisher publisher = new S3Publisher();
        assertThat(publisher.name()).isEqualTo("s3");
    }
}
