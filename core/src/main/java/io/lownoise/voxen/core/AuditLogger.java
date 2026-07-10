package io.lownoise.voxen.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lownoise.voxen.plugins.api.VoxenException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AuditLogger {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));

    private final Path logFile;
    private final boolean enabled;

    public AuditLogger(Path logFile, boolean enabled) {
        this.logFile = logFile;
        this.enabled = enabled;
    }

    public AuditLogger(Path projectDir) {
        this(projectDir.resolve(".voxen").resolve("audit.jsonl"), true);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void record(String operation, String project, String status, String detail) {
        if (!enabled) return;
        try {
            Files.createDirectories(logFile.getParent());
            ObjectNode entry = MAPPER.createObjectNode();
            entry.put("timestamp", FORMATTER.format(Instant.now()));
            entry.put("operation", operation);
            entry.put("project", project);
            entry.put("status", status);
            if (detail != null) entry.put("detail", detail);
            String line = MAPPER.writeValueAsString(entry) + System.lineSeparator();
            Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Audit log write failed: " + e.getMessage());
        }
    }

    public void init(String project, String runtime) {
        record("init", project, "success", "runtime=" + runtime);
    }

    public void build(String project, boolean success, long durationMs) {
        record("build", project, success ? "success" : "failure", "durationMs=" + durationMs);
    }

    public void packageOp(String project, boolean success, String format) {
        record("package", project, success ? "success" : "failure", "format=" + format);
    }

    public void publish(String project, boolean success, String repository) {
        record("publish", project, success ? "success" : "failure", "repository=" + repository);
    }

    public void deploy(String project, boolean success, String environment) {
        record("deploy", project, success ? "success" : "failure", "environment=" + environment);
    }

    public void configChange(String project, String field) {
        record("config:change", project, "success", "field=" + field);
    }
}
