package io.lownoise.voxen.plugins.api;

public class VoxenException extends RuntimeException {

    private final String userMessage;
    private final String hint;

    public VoxenException(String userMessage, String hint) {
        super(userMessage + "\n" + hint);
        this.userMessage = userMessage;
        this.hint = hint;
    }

    public VoxenException(String userMessage, String hint, Throwable cause) {
        super(userMessage + "\n" + hint, cause);
        this.userMessage = userMessage;
        this.hint = hint;
    }

    public String userMessage() {
        return userMessage;
    }

    public String hint() {
        return hint;
    }
}
