package io.lownoise.voxen.docker;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImageLinter {

    private final List<String> warnings;
    private final List<String> errors;

    public ImageLinter() {
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public LintResult lint(String imageTag) {
        warnings.clear();
        errors.clear();

        checkImageExists(imageTag);
        checkSize(imageTag);
        checkLayers(imageTag);
        checkUser(imageTag);
        checkPorts(imageTag);
        checkLabels(imageTag);
        checkSecrets(imageTag);

        return new LintResult(
            List.copyOf(errors),
            List.copyOf(warnings),
            errors.isEmpty()
        );
    }

    private void checkImageExists(String tag) {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "inspect", tag)
                .redirectErrorStream(true);
            Process p = pb.start();
            int exit = p.waitFor();
            if (exit != 0) {
                errors.add("Image not found: " + tag);
            }
        } catch (Exception e) {
            errors.add("Cannot inspect image: " + e.getMessage());
        }
    }

    private void checkSize(String tag) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "image", "inspect", tag,
                "--format", "{{.Size}}"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String output = new String(p.getInputStream().readAllBytes()).trim();
            int exit = p.waitFor();
            if (exit == 0 && !output.isEmpty()) {
                long bytes = Long.parseLong(output);
                if (bytes > 500_000_000) {
                    warnings.add("Image size is " + (bytes / 1_000_000)
                        + "MB — consider using a smaller base image");
                }
            }
        } catch (Exception e) {
            warnings.add("Cannot check image size");
        }
    }

    private void checkLayers(String tag) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "image", "inspect", tag,
                "--format", "{{len .RootFS.Layers}}"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String output = new String(p.getInputStream().readAllBytes()).trim();
            int exit = p.waitFor();
            if (exit == 0 && !output.isEmpty()) {
                int layers = Integer.parseInt(output);
                if (layers > 10) {
                    warnings.add("Image has " + layers
                        + " layers — consider squashing or multi-stage builds");
                }
            }
        } catch (Exception e) {
            warnings.add("Cannot count layers");
        }
    }

    private void checkUser(String tag) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "image", "inspect", tag,
                "--format", "{{.Config.User}}"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String output = new String(p.getInputStream().readAllBytes()).trim();
            int exit = p.waitFor();
            if (exit == 0 && (output.isEmpty() || output.equals("0"))) {
                warnings.add("Image runs as root — consider using a non-root user");
            }
        } catch (Exception e) {
            warnings.add("Cannot check user");
        }
    }

    private void checkPorts(String tag) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "image", "inspect", tag,
                "--format", "{{range $k, $v := .Config.ExposedPorts}}{{$k}}{{\" \"}}{{end}}"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String output = new String(p.getInputStream().readAllBytes()).trim();
            int exit = p.waitFor();
            if (exit == 0 && output.isEmpty()) {
                warnings.add("No ports exposed — consider adding EXPOSE in Dockerfile");
            }
        } catch (Exception e) {
            warnings.add("Cannot check exposed ports");
        }
    }

    private void checkLabels(String tag) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "image", "inspect", tag,
                "--format", "{{.Config.Labels}}"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String output = new String(p.getInputStream().readAllBytes()).trim();
            int exit = p.waitFor();
            if (exit == 0 && (output.isEmpty() || output.equals("map[]"))) {
                warnings.add("No labels set — consider adding OCI annotations");
            }
        } catch (Exception e) {
            warnings.add("Cannot check labels");
        }
    }

    private void checkSecrets(String tag) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm", tag, "cat", "/app/bom.json"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            int exit = p.waitFor();
            if (exit != 0) {
                warnings.add("SBOM not found at /app/bom.json");
            }
        } catch (Exception e) {
            warnings.add("Cannot verify SBOM presence");
        }
    }

    public record LintResult(List<String> errors, List<String> warnings, boolean passed) {}
}
