package io.lownoise.voxen.runtime;

import java.nio.file.Path;

public interface RuntimePlugin {

    String runtime();

    boolean detect(Path projectDir);

    String buildCommand(Path projectDir);

    String defaultArtifactPattern();

    String buildImage();
}
