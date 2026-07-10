package io.lownoise.voxen.core;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;

class ConsoleProgressTest {

    @Test
    void disabledProgressDoesNotPrint() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleProgress progress = new ConsoleProgress(new PrintStream(baos), false);
        progress.showSpinner("working", false);
        progress.done("done", false);
        assertThat(baos.toString()).isEmpty();
    }

    @Test
    void disabledProgressInJsonModeDoesNotPrint() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleProgress progress = new ConsoleProgress(new PrintStream(baos), false);
        progress.showSpinner("working", true);
        progress.done("done", true);
        assertThat(baos.toString()).isEmpty();
    }

    @Test
    void enabledProgressPrintsSpinner() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleProgress progress = new ConsoleProgress(new PrintStream(baos), true);
        progress.showSpinner("working", false);
        assertThat(baos.toString()).contains("working");
    }

    @Test
    void enabledProgressPrintsDone() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleProgress progress = new ConsoleProgress(new PrintStream(baos), true);
        progress.done("complete", false);
        assertThat(baos.toString()).contains("complete");
    }

    @Test
    void progressBarRendersCorrectly() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleProgress progress = new ConsoleProgress(new PrintStream(baos), true);
        progress.showProgress("test", 10, 100, false);
        assertThat(baos.toString()).contains("10%");
        assertThat(baos.toString()).contains("test");
    }

    @Test
    void progressBarAt100PrintsNewline() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleProgress progress = new ConsoleProgress(new PrintStream(baos), true);
        progress.showProgress("done", 100, 100, false);
        assertThat(baos.toString()).contains("100%");
        assertThat(baos.toString()).contains(System.lineSeparator());
    }

    @Test
    void failPrintsFailureMessage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleProgress progress = new ConsoleProgress(new PrintStream(baos), true);
        progress.fail("error occurred", false);
        assertThat(baos.toString()).contains("FAILED");
        assertThat(baos.toString()).contains("error occurred");
    }

    @Test
    void spinnerCyclesCharacters() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ConsoleProgress progress = new ConsoleProgress(new PrintStream(baos), true);
        progress.showSpinner("cycle", false);
        progress.showSpinner("cycle", false);
        progress.showSpinner("cycle", false);
        progress.showSpinner("cycle", false);
        String output = baos.toString();
        assertThat(output).contains("cycle");
    }
}
