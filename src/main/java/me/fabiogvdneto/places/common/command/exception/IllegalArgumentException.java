package me.fabiogvdneto.places.common.command.exception;

public class IllegalArgumentException extends CommandExecutionException {

    private final int index;

    public IllegalArgumentException(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}