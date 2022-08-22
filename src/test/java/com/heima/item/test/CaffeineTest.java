package com.heima.item.test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class CaffeineTest {

    /*
      基本用法测试
      结果：
        gf = 迪丽热巴
        null
        defaultGF = 柳岩
        柳岩
     */
    @Test
    void testBasicOps() {
        // 创建缓存对象
        Cache<String, String> cache = Caffeine.newBuilder().build();

        // 存数据
        cache.put("gf", "迪丽热巴");

        // 取数据，不存在则返回null
        String gf = cache.getIfPresent("gf");
        System.out.println("gf = " + gf);


        String defaultGFBeforeGet = cache.getIfPresent("defaultGF");
        System.out.println(defaultGFBeforeGet);
        /*
         get方法：
          取数据，包含两个参数：
          参数一：缓存的key
          参数二：Lambda表达式，表达式参数就是缓存的key，方法体是查询数据库的逻辑
          优先根据key查询JVM缓存，如果未命中，则执行Lambda表达式，去数据库查询，查出后返回结果并自动设置到JVM缓存
         */
        // 取数据，不存在则去数据库查询
        String defaultGF = cache.get("defaultGF", key -> {
            // 这里可以去数据库根据 key查询value
            return "柳岩";
        });
        System.out.println("defaultGF = " + defaultGF);
        String defaultGFAfterGet = cache.getIfPresent("defaultGF");
        System.out.println(defaultGFAfterGet);
    }

    /*
     基于大小设置驱逐策略：
     */
    @Test
    void testEvictByNum() throws InterruptedException {
        // 创建缓存对象
        Cache<String, String> cache = Caffeine.newBuilder()
                // 设置缓存大小上限为 1
                .maximumSize(1)
                .build();
        // 存数据
        cache.put("gf1", "柳岩");
        cache.put("gf2", "范冰冰");
        cache.put("gf3", "迪丽热巴");
        // 延迟10ms，给清理线程一点时间
        Thread.sleep(10L);
        // 获取数据
        System.out.println("gf1: " + cache.getIfPresent("gf1"));
        System.out.println("gf2: " + cache.getIfPresent("gf2"));
        System.out.println("gf3: " + cache.getIfPresent("gf3"));
    }

    /*
     基于时间设置驱逐策略：
     */
    @Test
    void testEvictByTime() throws InterruptedException {
        // 创建缓存对象
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(1)) // 设置缓存有效期为 10 秒
                .build();
        // 存数据
        cache.put("gf", "柳岩");
        // 获取数据
        System.out.println("gf: " + cache.getIfPresent("gf"));
        // 休眠一会儿
        Thread.sleep(1200L);
        System.out.println("gf: " + cache.getIfPresent("gf"));
    }
}
