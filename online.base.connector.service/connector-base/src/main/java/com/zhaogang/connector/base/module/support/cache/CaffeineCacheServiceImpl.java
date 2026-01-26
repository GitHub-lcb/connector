package com.zhaogang.connector.base.module.support.cache;

import com.google.common.collect.Lists;
import com.zhaogang.connector.base.constant.ReloadConst;
import com.zhaogang.connector.base.module.support.reload.core.annoation.SmartReload;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * caffeine 缓存实现
 *
 * @Author 连接器: 罗伊
 * @Date 2021/10/11 20:07
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */
public class CaffeineCacheServiceImpl implements CacheService {

    @Resource
    private CaffeineCacheManager caffeineCacheManager;

    /**
     * 获取所有缓存名称
     */
    @Override
    public List<String> cacheNames() {
        return Lists.newArrayList(caffeineCacheManager.getCacheNames());
    }

    /**
     * 某个缓存下的所有 key
     */
    @Override
    public List<String> cacheKey(String cacheName) {
        CaffeineCache cache = (CaffeineCache) caffeineCacheManager.getCache(cacheName);
        if (cache == null) {
            return Lists.newArrayList();
        }
        Set<Object> cacheKey = cache.getNativeCache().asMap().keySet();
        return cacheKey.stream().map(e -> e.toString()).collect(Collectors.toList());
    }

    /**
     * 移除某个 key
     */
    @Override
    public void removeCache(String cacheName) {
        CaffeineCache cache = (CaffeineCache) caffeineCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    @SmartReload(ReloadConst.CACHE_SERVICE)
    public void clearAllCache() {
        Collection<String> cacheNames = caffeineCacheManager.getCacheNames();
        for (String name : cacheNames) {
            CaffeineCache cache = (CaffeineCache) caffeineCacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }
}