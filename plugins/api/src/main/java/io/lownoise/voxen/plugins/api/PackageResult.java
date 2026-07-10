package io.lownoise.voxen.plugins.api;

import java.nio.file.Path;

public record PackageResult(
    boolean success,
    Path packagePath,
    String format,
    long sizeBytes,
    String output
) {

    public static PackageResult success(Path packagePath, String format, long sizeBytes, String output) {
        return new PackageResult(true, packagePath, format, sizeBytes, output);
    }

    public static PackageResult failure(String errorMessage) {
        return new PackageResult(false, null, null, 0, errorMessage);
    }
}
