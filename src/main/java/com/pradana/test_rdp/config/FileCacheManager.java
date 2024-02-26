package com.pradana.test_rdp.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pradana.test_rdp.dto.SearchResponseDto;

public class FileCacheManager implements Cache {

    private String name;
    private File cacheFile;

    public FileCacheManager(String name) {
        try {
            this.cacheFile = ResourceUtils.getFile("cache.json");
        } catch (FileNotFoundException e) {
            try {
                Files.createFile(Paths.get(ResourceUtils.getFile("cache.json").getPath()));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    @Nullable
    public ValueWrapper get(Object key) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(Files.readString(Paths.get(cacheFile.getPath())), Map.class);

            if (data.get("expired") != null) {
                Integer longDate = Integer.valueOf(data.get("expired").toString());
                boolean isExpired = Instant.now().isAfter(Instant.ofEpochSecond(longDate));
                System.out.println(isExpired);
                if (!isExpired) {
                    return new SimpleValueWrapper(data);
                } else {
                    return null;
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        return null;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        if (value instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) value;
            List<SearchResponseDto> values = (List<SearchResponseDto>) data.get("results");
            ObjectMapper mapper = new ObjectMapper();
            try {
                HashMap<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("expired", Instant.now().plus(Duration.ofDays(1)).getEpochSecond());
                jsonMap.put("etag", data.get("etag"));
                jsonMap.put("data", values);
                Files.writeString(Paths.get(cacheFile.getPath()), mapper.writeValueAsString(jsonMap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void evict(Object key) {
        if (cacheFile != null) {
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
        }
    }

    @Override
    public void clear() {
        if (cacheFile != null) {
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
        }
    }

}
