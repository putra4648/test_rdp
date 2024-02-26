package com.pradana.test_rdp.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class FileService {
    public File cacheFile(String content) {
        try {
            File file = ResourceUtils.getFile("cache.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            Path path = Paths.get(file.getPath());
            Files.writeString(path, content);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
