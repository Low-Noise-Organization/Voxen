package io.lownoise.voxen.cli;

import io.lownoise.voxen.cli.command.*;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.assertj.core.api.Assertions.*;

class VoxenCliTest {

    @Test
    void helpShowsUsage() {
        int exit = new CommandLine(new VoxenCli()).execute("--help");
        assertThat(exit).isZero();
    }

    @Test
    void dockerizeIsRegisteredAsSubcommand() {
        var cmd = new CommandLine(new VoxenCli());
        assertThat(cmd.getSubcommands()).containsKey("dockerize");
    }

    @Test
    void publishDockerIsRegisteredAsSubcommand() {
        var cmd = new CommandLine(new VoxenCli());
        assertThat(cmd.getSubcommands()).containsKey("publish:docker");
    }

    @Test
    void dockerizeHelpDoesNotThrow() {
        var cmd = new CommandLine(new VoxenCli());
        assertThatCode(() -> cmd.execute("dockerize", "--help")).doesNotThrowAnyException();
    }

    @Test
    void publishDockerHelpDoesNotThrow() {
        var cmd = new CommandLine(new VoxenCli());
        assertThatCode(() -> cmd.execute("publish:docker", "--help")).doesNotThrowAnyException();
    }

    @Test
    void dockerizeFailsWithoutTag() {
        int exit = new CommandLine(new VoxenCli()).execute("dockerize");
        assertThat(exit).isNotZero();
    }

    @Test
    void publishDockerFailsWithoutRequiredOptions() {
        int exit = new CommandLine(new VoxenCli()).execute("publish:docker");
        assertThat(exit).isNotZero();
    }

    @Test
    void versionShowsVersion() {
        int exit = new CommandLine(new VoxenCli()).execute("--version");
        assertThat(exit).isZero();
    }
}
