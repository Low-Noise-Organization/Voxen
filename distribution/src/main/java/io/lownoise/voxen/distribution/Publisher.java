package io.lownoise.voxen.distribution;

import java.nio.file.Path;

public interface Publisher {
    String name();
    PublishResult publish(Path artifact, String repository, String groupId, String artifactId, String version);
}
