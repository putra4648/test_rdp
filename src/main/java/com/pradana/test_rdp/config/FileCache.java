package com.pradana.test_rdp.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.lang.Nullable;

public class FileCache extends AbstractCacheManager {

    List<Cache> caches = new ArrayList<>();
    Map<String, Cache> cacheMap = new HashMap<>();

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return caches;
    }

    @Override
    @Nullable
    public Cache getCache(String name) {
        if (cacheMap.containsKey(name)) {
            return cacheMap.get(name);
        } else {
            try {
                FileCacheManager restCache = new FileCacheManager("restCache");
                cacheMap.put(name, restCache);
                return restCache;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
