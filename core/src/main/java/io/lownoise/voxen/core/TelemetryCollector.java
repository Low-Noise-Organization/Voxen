package io.lownoise.voxen.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TelemetryCollector {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Path TELEMETRY_FILE = Path.of(
        System.getProperty("user.home"), ".voxen", "telemetry.jsonl");

    private final boolean optIn;
    private final Map<String, TelemetryEvent> active = new ConcurrentHashMap<>();
    private long totalOperations;
    private long failedOperations;

    public TelemetryCollector(boolean optIn) {
        this.optIn = optIn;
    }

    public TelemetryCollector() {
        this(false);
    }

    public boolean isOptedIn() {
        return optIn;
    }

    public void startOperation(String operationId, String operation, String project) {
        if (!optIn) return;
        active.put(operationId, new TelemetryEvent(operation, project, Instant.now()));
        totalOperations++;
    }

    public void finishOperation(String operationId, boolean success, String detail) {
        if (!optIn) return;
        TelemetryEvent event = active.remove(operationId);
        if (event == null) return;
        if (!success) failedOperations++;
        long durationMs = Duration.between(event.startTime(), Instant.now()).toMillis();
        record(event.operation(), event.project(), success, durationMs, detail);
    }

    public long totalOperations() {
        return totalOperations;
    }

    public long failedOperations() {
        return failedOperations;
    }

    private void record(String operation, String project, boolean success,
                        long durationMs, String detail) {
        try {
            Files.createDirectories(TELEMETRY_FILE.getParent());
            ObjectNode entry = MAPPER.createObjectNode();
            entry.put("operation", operation);
            entry.put("project", project);
            entry.put("success", success);
            entry.put("durationMs", durationMs);
            if (detail != null) entry.put("detail", detail);
            String line = MAPPER.writeValueAsString(entry) + System.lineSeparator();
            Files.writeString(TELEMETRY_FILE, line,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Telemetry write failed: " + e.getMessage());
        }
    }

    private record TelemetryEvent(String operation, String project, Instant startTime) {}
}
