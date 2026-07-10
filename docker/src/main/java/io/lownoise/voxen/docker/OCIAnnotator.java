package io.lownoise.voxen.docker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OCIAnnotator {

    public void annotate(String imageTag, BuildMetadata metadata) throws Exception {
        List<String> annotations = new ArrayList<>();

        annotations.add("org.opencontainers.image.created=" + metadata.timestamp());
        annotations.add("org.opencontainers.image.version=" + metadata.version());
        annotations.add("org.opencontainers.image.revision=" + metadata.commit());
        annotations.add("org.opencontainers.image.source=" + metadata.repository());
        annotations.add("org.opencontainers.image.title=" + metadata.projectName());
        annotations.add("org.opencontainers.image.description=" + metadata.description());
        annotations.add("org.opencontainers.image.licenses=" + metadata.license());

        for (String annotation : annotations) {
            String[] parts = annotation.split("=", 2);
            if (parts.length == 2) {
                exec("docker", "annotate", imageTag,
                    "--annotation", parts[0] + "=" + parts[1]);
            }
        }
    }

    public void annotateFromFile(String imageTag, Path annotationsFile) throws Exception {
        if (!Files.exists(annotationsFile)) return;
        List<String> lines = Files.readAllLines(annotationsFile);
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            String[] parts = trimmed.split("=", 2);
            if (parts.length == 2) {
                exec("docker", "annotate", imageTag,
                    "--annotation", parts[0] + "=" + parts[1]);
            }
        }
    }

    private void exec(String... cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd)
            .inheritIO()
            .redirectErrorStream(true);
        Process p = pb.start();
        int exit = p.waitFor();
        if (exit != 0) {
            throw new RuntimeException("Command failed: " + String.join(" ", cmd));
        }
    }
}
