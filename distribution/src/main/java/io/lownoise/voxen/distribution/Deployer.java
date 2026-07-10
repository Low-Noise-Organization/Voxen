package io.lownoise.voxen.distribution;

import java.nio.file.Path;

public interface Deployer {
    String name();
    DeployResult deploy(Path artifact, String target);
}
