package io.lownoise.voxen.core;

import java.io.PrintStream;

public class ConsoleProgress {

    private static final char[] SPINNERS = {'|', '/', '-', '\\'};
    private static final int BAR_WIDTH = 40;

    private final PrintStream out;
    private final boolean enabled;
    private int spinnerIndex;

    public ConsoleProgress(PrintStream out, boolean enabled) {
        this.out = out;
        this.enabled = enabled;
    }

    public ConsoleProgress(boolean enabled) {
        this(System.out, enabled);
    }

    private boolean shouldPrint(boolean jsonMode) {
        return enabled && !jsonMode;
    }

    public void showSpinner(String message, boolean jsonMode) {
        if (!shouldPrint(jsonMode)) return;
        spinnerIndex = (spinnerIndex + 1) % SPINNERS.length;
        out.print("\r" + SPINNERS[spinnerIndex] + " " + message);
    }

    public void showProgress(String message, int current, int total, boolean jsonMode) {
        if (!shouldPrint(jsonMode)) return;
        if (total <= 0) return;
        int percent = Math.min(100, current * 100 / total);
        int filled = Math.min(BAR_WIDTH, current * BAR_WIDTH / total);
        StringBuilder bar = new StringBuilder("\r[");
        for (int i = 0; i < BAR_WIDTH; i++) {
            bar.append(i < filled ? '=' : (i == filled ? '>' : ' '));
        }
        bar.append("] ").append(percent).append("% ").append(message);
        out.print(bar.toString());
        if (current >= total) {
            out.println();
        }
    }

    public void done(String message, boolean jsonMode) {
        if (!shouldPrint(jsonMode)) return;
        out.println("\r" + " ".repeat(60) + "\r" + message);
    }

    public void fail(String message, boolean jsonMode) {
        if (!shouldPrint(jsonMode)) return;
        out.println("\r" + " ".repeat(60) + "\rFAILED: " + message);
    }
}
