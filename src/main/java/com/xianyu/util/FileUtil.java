package com.xianyu.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {

    private FileUtil() {
    }

    public static Path ensureDirectory(String directory) throws IOException {
        Path dir = Path.of(directory);
        return Files.exists(dir) ? dir : Files.createDirectories(dir);
    }
}

