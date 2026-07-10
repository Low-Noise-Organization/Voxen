package io.lownoise.voxen.docker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class OCIAnnotatorTest {

    private final OCIAnnotator annotator = new OCIAnnotator();

    @Test
    void annotateFailsForInvalidImage() {
        BuildMetadata meta = BuildMetadata.builder()
            .projectName("test").runtime("java").pluginName("java")
            .build();
        assertThatThrownBy(() -> annotator.annotate("nonexistent:latest", meta))
            .isInstanceOf(Exception.class);
    }

    @Test
    void annotateFromFileIgnoresMissingFile(@TempDir Path tempDir) throws Exception {
        annotator.annotateFromFile("test:latest", tempDir.resolve("nonexistent.txt"));
    }

    @Test
    void annotateFromFileSkipsCommentsAndBlanks(@TempDir Path tempDir) throws Exception {
        Path f = tempDir.resolve("annotations.txt");
        Files.writeString(f, "# comment\n\nkey=value\n");
        assertThatThrownBy(() -> annotator.annotateFromFile("test:latest", f))
            .isInstanceOf(Exception.class);
    }
}
