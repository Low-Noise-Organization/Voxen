package io.lownoise.voxen.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lownoise.voxen.plugins.api.VoxenException;

import java.nio.file.Path;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfileConfig(
    String name,
    String extends_,
    Map<String, String> variables,
    Map<String, String> settings
) {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Map<String, ProfileConfig> BUILTIN = Map.of(
        "dev", new ProfileConfig("dev", null,
            Map.of("env", "development", "optimization", "none", "debug", "true"),
            Map.of("verbose", "true", "skipTests", "false")),
        "staging", new ProfileConfig("staging", null,
            Map.of("env", "staging", "optimization", "standard", "debug", "false"),
            Map.of("verbose", "false", "skipTests", "false")),
        "prod", new ProfileConfig("prod", null,
            Map.of("env", "production", "optimization", "aggressive", "debug", "false"),
            Map.of("verbose", "false", "skipTests", "true"))
    );

    public static ProfileConfig builtin(String name) {
        ProfileConfig profile = BUILTIN.get(name);
        if (profile == null) {
            throw new VoxenException(
                "Unknown built-in profile: " + name,
                "Available profiles: " + BUILTIN.keySet());
        }
        return profile;
    }

    public static ProfileConfig load(Path projectDir, String profileName) {
        ProfileConfig builtin = BUILTIN.get(profileName);
        if (builtin != null) return builtin;

        Path profileFile = projectDir.resolve("voxen-" + profileName + ".json");
        if (!profileFile.toFile().exists()) {
            throw new VoxenException(
                "Profile not found: " + profileName,
                "Create voxen-" + profileName + ".json in " + projectDir
                    + " or use a built-in profile: " + BUILTIN.keySet());
        }
        try {
            String json = java.nio.file.Files.readString(profileFile);
            return MAPPER.readValue(json, ProfileConfig.class);
        } catch (VoxenException e) {
            throw e;
        } catch (Exception e) {
            throw new VoxenException(
                "Failed to read profile: " + profileFile,
                "Check that the file contains valid JSON.", e);
        }
    }

    public Map<String, String> effectiveVariables() {
        return variables != null ? variables : Map.of();
    }

    public Map<String, String> effectiveSettings() {
        return settings != null ? settings : Map.of();
    }
}
