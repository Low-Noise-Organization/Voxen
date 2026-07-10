package io.lownoise.voxen.cli;

import picocli.AutoComplete;
import picocli.CommandLine;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class CompletionGenerator {

    public static void main(String[] args) throws Exception {
        Path outputDir = args.length > 0 ? Path.of(args[0]) : Path.of("build/completions");
        Files.createDirectories(outputDir);

        CommandLine cmd = new CommandLine(new VoxenCli());

        String bashScript = AutoComplete.bash("voxen", cmd);
        Path bashFile = outputDir.resolve("voxen_completion.bash");
        Files.writeString(bashFile, bashScript);
        System.out.println("Generated: " + bashFile.toAbsolutePath());

        String zshScript = AutoComplete.bash("voxen", cmd)
            .replace("#compdef voxen", "#compdef voxen\ncompdef _voxen voxen");
        Path zshFile = outputDir.resolve("voxen_completion.zsh");
        Files.writeString(zshFile, zshScript);
        System.out.println("Generated: " + zshFile.toAbsolutePath());
    }
}
