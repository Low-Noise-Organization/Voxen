package io.lownoise.voxen.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lownoise.voxen.plugins.api.VoxenException;

import java.nio.file.Path;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VoxenConfig(
    String name,
    String runtime,
    String target,
    String output
) {

    private static final String FILENAME = "voxen.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static VoxenConfig fromProjectDir(Path dir) {
        Path configFile = dir.resolve(FILENAME);
        if (!configFile.toFile().exists()) {
            throw new VoxenException(
                "No " + FILENAME + " found in " + dir.toAbsolutePath(),
                "Run 'voxen init' to create one."
            );
        }
        try {
            String json = java.nio.file.Files.readString(configFile);
            ConfigValidator.validate(json);
            return MAPPER.readValue(json, VoxenConfig.class);
        } catch (VoxenException e) {
            throw e;
        } catch (Exception e) {
            throw new VoxenException(
                "Failed to read " + FILENAME + ": the file is malformed.",
                "Check the JSON syntax in " + configFile.toAbsolutePath(),
                e
            );
        }
    }

    public ProjectConfig toProjectConfig(Path dir) {
        return new ProjectConfig(name, runtime(), target(), output, dir);
    }

    public String toJson() {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            throw new VoxenException(
                "Failed to serialize configuration to JSON.",
                "This is an internal error. Report it at https://github.com/lownoise/voxen/issues",
                e
            );
        }
    }

    public void write(Path dir) {
        try {
            java.nio.file.Files.createDirectories(dir);
            String json = toJson();
            java.nio.file.Files.writeString(dir.resolve(FILENAME), json);
        } catch (Exception e) {
            throw new VoxenException(
                "Failed to write " + FILENAME + " to " + dir.toAbsolutePath(),
                "Check that the directory is writable.",
                e
            );
        }
    }
}
