package io.lownoise.voxen.docker;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MultiArchBuilder {

    private static final List<String> DEFAULT_PLATFORMS = List.of(
        "linux/amd64", "linux/arm64"
    );

    public int build(Path context, String tag, List<String> platforms, boolean push) {
        return build(context, tag, platforms, push, null);
    }

    public int build(Path context, String tag, List<String> platforms,
                      boolean push, String dockerfile) {
        try {
            List<String> cmd = new ArrayList<>(List.of(
                "docker", "buildx", "build"));

            cmd.add("--platform");
            cmd.add(String.join(",", platforms != null && !platforms.isEmpty()
                ? platforms : DEFAULT_PLATFORMS));

            cmd.add("-t");
            cmd.add(tag);

            if (push) {
                cmd.add("--push");
            }

            if (dockerfile != null) {
                cmd.add("-f");
                cmd.add(dockerfile);
            }

            cmd.add("--label");
            cmd.add("voxen.multiarh=true");

            cmd.add("--provenance");
            cmd.add("mode=max");

            cmd.add("--sbom");
            cmd.add("true");

            cmd.add(context.toString());

            ProcessBuilder pb = new ProcessBuilder(cmd)
                .directory(context.toFile())
                .inheritIO();

            Process p = pb.start();
            return p.waitFor();
        } catch (Exception e) {
            System.err.println("Multi-arch build failed: " + e.getMessage());
            return 1;
        }
    }
}
