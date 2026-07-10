package io.lownoise.voxen.plugins.api;

import java.nio.file.Path;
import java.util.List;

public interface PluginContext {

    Path workingDirectory();

    Path outputDirectory();

    boolean verbose();

    boolean debug();

    void log(String message);

    void verbose(String message);

    void debug(String message);

    void error(String message);
}
