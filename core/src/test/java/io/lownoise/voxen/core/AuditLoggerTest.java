package io.lownoise.voxen.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class AuditLoggerTest {

    @Test
    void auditLoggerIsEnabledByDefault(@TempDir Path tempDir) {
        AuditLogger logger = new AuditLogger(tempDir);
        assertThat(logger.isEnabled()).isTrue();
    }

    @Test
    void loggerCanBeDisabled() {
        AuditLogger logger = new AuditLogger(Path.of("/tmp/audit.log"), false);
        assertThat(logger.isEnabled()).isFalse();
    }

    @Test
    void recordWritesLogFile(@TempDir Path tempDir) throws Exception {
        Path logFile = tempDir.resolve("audit.jsonl");
        AuditLogger logger = new AuditLogger(logFile, true);

        logger.record("build", "my-project", "success", "durationMs=100");

        assertThat(logFile).exists();
        String content = java.nio.file.Files.readString(logFile);
        assertThat(content).contains("build");
        assertThat(content).contains("my-project");
        assertThat(content).contains("success");
    }

    @Test
    void recordMultipleEntries(@TempDir Path tempDir) throws Exception {
        Path logFile = tempDir.resolve("audit.jsonl");
        AuditLogger logger = new AuditLogger(logFile, true);

        logger.record("build", "proj-a", "success", null);
        logger.record("deploy", "proj-a", "failure", "timeout");

        String content = java.nio.file.Files.readString(logFile);
        assertThat(content).contains("proj-a");
        assertThat(content).contains("timeout");
    }

    @Test
    void convenienceMethodsWriteToLog(@TempDir Path tempDir) throws Exception {
        Path logFile = tempDir.resolve("audit.jsonl");
        AuditLogger logger = new AuditLogger(logFile, true);

        logger.init("my-app", "java");
        logger.build("my-app", true, 500);
        logger.publish("my-app", true, "maven-central");
        logger.deploy("my-app", true, "staging");

        String content = java.nio.file.Files.readString(logFile);
        assertThat(content).contains("init");
        assertThat(content).contains("build");
        assertThat(content).contains("publish");
        assertThat(content).contains("deploy");
    }

    @Test
    void disabledLoggerDoesNotWrite(@TempDir Path tempDir) throws Exception {
        Path logFile = tempDir.resolve("audit.jsonl");
        AuditLogger logger = new AuditLogger(logFile, false);

        logger.record("build", "proj", "success", null);

        assertThat(logFile).doesNotExist();
    }
}
