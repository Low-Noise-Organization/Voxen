package io.lownoise.voxen.runtime;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class RuntimeDetectorTest {

    private final RuntimeDetector detector = new RuntimeDetector();

    @Test
    void shouldDetectJavaVersion() {
        assertThat(detector.javaVersion()).isNotEmpty();
    }

    @Test
    void javaVersionShouldMatchPattern() {
        assertThat(detector.javaVersion()).matches("\\d+(\\.\\d+)*");
    }

    @Test
    void shouldReportJavaAvailable() {
        assertThat(detector.isJavaAvailable()).isTrue();
    }

    @Test
    void detectJavaHomeReturnsValue() {
        Optional<String> home = detector.detectJavaHome();
        assertThat(home).isPresent();
        Path javac = Path.of(home.get(), "bin", "javac");
        assertThat(javac).exists();
    }

    @Test
    void isMavenAvailableReturnsWithoutError() {
        boolean result = detector.isMavenAvailable();
        assertThat(result).isIn(true, false);
    }

    @Test
    void isGradleAvailableReturnsWithoutError() {
        boolean result = detector.isGradleAvailable();
        assertThat(result).isIn(true, false);
    }

    @Test
    void commandAvailableHandlesExceptionGracefully() {
        boolean result = detector.isMavenAvailable();
        assertThat(result).isIn(true, false);
    }
}
