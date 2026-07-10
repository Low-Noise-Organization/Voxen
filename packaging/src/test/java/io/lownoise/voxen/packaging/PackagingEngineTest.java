package io.lownoise.voxen.packaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class PackagingEngineTest {

    @Test
    void shouldCreateZipArchive(@TempDir Path tempDir) throws Exception {
        Files.createDirectories(tempDir.resolve("lib"));
        Files.writeString(tempDir.resolve("lib/app.jar"), "fake jar content");
        Files.writeString(tempDir.resolve("voxen.json"), "{}");

        PackagingEngine engine = new PackagingEngine();
        Path zipFile = engine.createZip(tempDir, tempDir.resolve("output.zip"));

        assertThat(zipFile).exists();
        assertThat(zipFile).isRegularFile();
        assertThat(Files.size(zipFile)).isGreaterThan(0);
    }

    @Test
    void shouldCreateTarGzArchive(@TempDir Path tempDir) throws Exception {
        Files.writeString(tempDir.resolve("app.jar"), "fake jar content");

        PackagingEngine engine = new PackagingEngine();
        Path tarFile = engine.createTarGz(tempDir, tempDir.resolve("output.tar.gz"));

        assertThat(tarFile).exists();
        assertThat(tarFile).isRegularFile();
        assertThat(Files.size(tarFile)).isGreaterThan(0);
    }

    @Test
    void shouldCollectJarArtifacts(@TempDir Path tempDir) throws Exception {
        Files.createDirectories(tempDir.resolve("target"));
        Files.createFile(tempDir.resolve("target/app.jar"));
        Files.createFile(tempDir.resolve("target/app-sources.jar"));
        Files.createFile(tempDir.resolve("README.txt"));

        PackagingEngine engine = new PackagingEngine();
        var jars = engine.collectArtifacts(tempDir, ".jar");

        assertThat(jars).hasSize(2);
        assertThat(jars).allMatch(p -> p.toString().endsWith(".jar"));
    }

    @Test
    void shouldReturnEmptyWhenNoArtifacts(@TempDir Path tempDir) throws Exception {
        PackagingEngine engine = new PackagingEngine();
        assertThat(engine.collectArtifacts(tempDir, ".jar")).isEmpty();
    }
}
