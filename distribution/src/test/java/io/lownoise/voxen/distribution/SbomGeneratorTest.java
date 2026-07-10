package io.lownoise.voxen.distribution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class SbomGeneratorTest {

    @Test
    void shouldGenerateCycloneDxBom(@TempDir Path tempDir) throws Exception {
        Path artifact = tempDir.resolve("app.jar");
        Files.writeString(artifact, "fake jar");

        Path bomDir = tempDir.resolve("bom");
        SbomGenerator.generateCycloneDx(artifact, "com.example", "my-app", "1.0.0", bomDir);

        Path bomFile = bomDir.resolve("my-app-1.0.0-bom.json");
        assertThat(bomFile).exists();
        assertThat(Files.readString(bomFile)).contains("CycloneDX");
    }

    @Test
    void bomContainsMetadata(@TempDir Path tempDir) throws Exception {
        Path artifact = tempDir.resolve("lib.jar");
        Files.writeString(artifact, "content");

        SbomGenerator.generateCycloneDx(artifact, "com.example", "lib", "2.0", tempDir);

        String bom = Files.readString(tempDir.resolve("lib-2.0-bom.json"));
        assertThat(bom).contains("com.example");
        assertThat(bom).contains("lib");
        assertThat(bom).contains("2.0");
        assertThat(bom).contains("pkg:maven");
    }
}
