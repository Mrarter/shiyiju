package com.shiyiju.modules.admin.support;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AdminUploadPaths {

    private AdminUploadPaths() {
    }

    public static Path root() {
        return Paths.get(System.getProperty("user.home"), ".shiyiju", "uploads");
    }
}
