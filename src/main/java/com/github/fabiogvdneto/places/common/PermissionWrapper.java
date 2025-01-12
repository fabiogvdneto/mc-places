package com.github.fabiogvdneto.places.common;

import com.github.fabiogvdneto.places.common.exception.PermissionRequiredException;
import org.bukkit.permissions.Permissible;

public record PermissionWrapper(String name) {

    public PermissionWrapper(String name) {
        this.name = name.strip();
    }

    public boolean test(Permissible target) {
        return target.hasPermission(name);
    }

    public void require(Permissible target) throws PermissionRequiredException {
        if (!test(target))
            throw new PermissionRequiredException(name);
    }
}
