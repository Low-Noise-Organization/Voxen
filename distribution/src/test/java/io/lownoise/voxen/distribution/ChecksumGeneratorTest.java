package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ChecksumGeneratorTest {

    @Test
    void shouldGenerateChecksums(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.jar");
        Files.writeString(file, "fake jar content for checksum test");

        ChecksumGenerator.Checksums checksums = ChecksumGenerator.generate(file);
        assertThat(checksums.sha256()).isNotEmpty();
        assertThat(checksums.sha512()).isNotEmpty();
        assertThat(checksums.md5()).isNotEmpty();
    }

    @Test
    void shouldGenerateDeterministicChecksums(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.jar");
        Files.writeString(file, "deterministic content");

        ChecksumGenerator.Checksums first = ChecksumGenerator.generate(file);
        ChecksumGenerator.Checksums second = ChecksumGenerator.generate(file);

        assertThat(first.sha256()).isEqualTo(second.sha256());
        assertThat(first.sha512()).isEqualTo(second.sha512());
        assertThat(first.md5()).isEqualTo(second.md5());
    }

    @Test
    void shouldWriteChecksumFiles(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("app.jar");
        Files.writeString(file, "content");

        ChecksumGenerator.writeChecksumFiles(file);

        assertThat(tempDir.resolve("app.jar.sha256")).exists();
        assertThat(tempDir.resolve("app.jar.sha512")).exists();
        assertThat(tempDir.resolve("app.jar.md5")).exists();
    }

    @Test
    void checksumFilesContainExpectedFormat(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("artifact.jar");
        Files.writeString(file, "content");

        ChecksumGenerator.writeChecksumFiles(file);

        String sha256Content = Files.readString(tempDir.resolve("artifact.jar.sha256"));
        assertThat(sha256Content).contains("artifact.jar");
    }
}
