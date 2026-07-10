package io.lownoise.voxen.packaging;

public enum ArchiveFormat {
    ZIP(".zip"),
    TAR_GZ(".tar.gz"),
    TAR_BZ2(".tar.bz2");

    private final String extension;

    ArchiveFormat(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }

    public static ArchiveFormat fromString(String format) {
        return switch (format.toLowerCase()) {
            case "zip" -> ZIP;
            case "tar.gz", "tgz" -> TAR_GZ;
            case "tar.bz2", "tbz2" -> TAR_BZ2;
            default -> throw new IllegalArgumentException(
                "Unsupported archive format: " + format + "\n" +
                "Supported formats: zip, tar.gz, tar.bz2");
        };
    }
}
