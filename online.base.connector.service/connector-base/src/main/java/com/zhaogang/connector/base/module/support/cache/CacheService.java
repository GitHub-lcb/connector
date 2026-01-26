package com.zhaogang.connector.base.module.support.cache;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 缓存服务
 *
 * @Author 连接器: 罗伊
 * @Date 2021/10/11 20:07
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a>
 */
@Service
public interface CacheService {

    /**
     * 获取所有缓存名称
     */
    List<String> cacheNames();

    /**
     * 某个缓存下的所有 key
     */
    List<String> cacheKey(String cacheName);

    /**
     * 移除某个 key
     */
    void removeCache(String cacheName);

}