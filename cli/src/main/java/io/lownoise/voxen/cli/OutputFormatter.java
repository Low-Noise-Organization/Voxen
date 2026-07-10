package io.lownoise.voxen.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OutputFormatter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final boolean json;

    public OutputFormatter(boolean json) {
        this.json = json;
    }

    public boolean isJson() {
        return json;
    }

    public void log(String message) {
        if (!json) {
            System.out.println(message);
        }
    }

    public void verbose(boolean verbose, String message) {
        if (verbose && !json) {
            System.out.println(message);
        }
    }

    public void error(String message) {
        System.err.println(message);
    }

    public void result(JsonResult result) {
        if (json) {
            try {
                System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result.node()));
            } catch (Exception e) {
                System.err.println("{\"error\":\"Failed to serialize output\"}");
            }
        }
    }

    public interface JsonResult {
        ObjectNode node();
    }

    public static ObjectNode object() {
        return MAPPER.createObjectNode();
    }

    public static ArrayNode array() {
        return MAPPER.createArrayNode();
    }

    public record MessageResult(String status, String message) implements JsonResult {
        @Override
        public ObjectNode node() {
            return object().put("status", status).put("message", message);
        }
    }

    public record BuildJsonResult(
        String status, String project, long durationMs,
        java.util.List<String> artifacts, String output
    ) implements JsonResult {
        @Override
        public ObjectNode node() {
            var arr = array();
            artifacts.forEach(arr::add);
            var n = object();
            n.put("status", status);
            n.put("project", project);
            n.put("durationMs", durationMs);
            n.set("artifacts", arr);
            n.put("output", output);
            return n;
        }
    }

    public record PackageJsonResult(
        String status, String project, String path,
        String format, long sizeBytes
    ) implements JsonResult {
        @Override
        public ObjectNode node() {
            return object()
                .put("status", status)
                .put("project", project)
                .put("path", path)
                .put("format", format)
                .put("sizeBytes", sizeBytes);
        }
    }

    public record PublishJsonResult(
        String status, String project, String repository, String url
    ) implements JsonResult {
        @Override
        public ObjectNode node() {
            var node = object()
                .put("status", status)
                .put("project", project)
                .put("repository", repository);
            if (url != null) node.put("url", url);
            return node;
        }
    }

    public record DeployJsonResult(
        String status, String project, String environment, String url
    ) implements JsonResult {
        @Override
        public ObjectNode node() {
            var node = object()
                .put("status", status)
                .put("project", project)
                .put("environment", environment);
            if (url != null) node.put("url", url);
            return node;
        }
    }

    public record InitJsonResult(
        String status, String project, String runtime, String path
    ) implements JsonResult {
        @Override
        public ObjectNode node() {
            return object()
                .put("status", status)
                .put("project", project)
                .put("runtime", runtime)
                .put("path", path);
        }
    }

    public record DockerizeJsonResult(
        String status, String project, String tag,
        String dockerfile, boolean pushed
    ) implements JsonResult {
        @Override
        public ObjectNode node() {
            return object()
                .put("status", status)
                .put("project", project)
                .put("tag", tag)
                .put("dockerfile", dockerfile)
                .put("pushed", pushed);
        }
    }
}
