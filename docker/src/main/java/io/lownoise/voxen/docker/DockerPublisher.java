package io.lownoise.voxen.docker;

import io.lownoise.voxen.distribution.CredentialManager;
import io.lownoise.voxen.distribution.Publisher;
import io.lownoise.voxen.distribution.PublishResult;
import io.lownoise.voxen.distribution.PublisherFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DockerPublisher implements Publisher {

    static {
        PublisherFactory.register(new DockerPublisher());
    }

    @Override
    public String name() {
        return "docker";
    }

    @Override
    public PublishResult publish(Path artifact, String repository,
                                  String groupId, String artifactId, String version) {
        String localTag = artifact.toString();
        String remoteTag = repository + "/" + artifactId + ":" + version;

        try {
            CredentialManager.CredentialEntry creds = CredentialManager.get("docker");
            String token = creds != null ? creds.token() : null;
            String username = creds != null ? creds.username() : null;

            if (token != null && username != null) {
                exec("docker", "login", "-u", username, "--password-stdin", repository);
            }

            exec("docker", "tag", localTag, remoteTag);
            exec("docker", "push", remoteTag);

            return PublishResult.success(repository, remoteTag, "Published to " + remoteTag);
        } catch (Exception e) {
            return PublishResult.failure("Docker publish failed: " + e.getMessage());
        }
    }

    public PublishResult publishMultiArch(Path localTag, String repository,
                                           String artifactId, String version,
                                           List<String> platforms) {
        String remoteTag = repository + "/" + artifactId + ":" + version;
        try {
            List<String> cmd = new ArrayList<>(List.of(
                "docker", "buildx", "build", "--push"));
            for (String platform : platforms) {
                cmd.add("--platform");
                cmd.add(platform);
            }
            cmd.add("-t");
            cmd.add(remoteTag);
            cmd.add(localTag.toString());

            exec(cmd.toArray(new String[0]));
            return PublishResult.success(repository, remoteTag,
                "Published multi-arch to " + remoteTag);
        } catch (Exception e) {
            return PublishResult.failure("Multi-arch publish failed: " + e.getMessage());
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
