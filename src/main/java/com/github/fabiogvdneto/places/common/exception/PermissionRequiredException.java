package com.github.fabiogvdneto.places.common.exception;

public class PermissionRequiredException extends CommandExecutionException {

    private final String permission;

    public PermissionRequiredException(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
