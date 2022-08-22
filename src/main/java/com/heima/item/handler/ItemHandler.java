package com.heima.item.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.heima.item.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

/**
 * @author CHAN
 * @since 2022/8/22
 */
@Component
@CanalTable("tb_item")
public class ItemHandler implements EntryHandler<Item> {
    @Autowired
    private RedisHandler redisHandler;
    @Autowired
    private Cache<Long, Item> itemCache;

    @Override
    public void insert(Item item) {
        // 写数据到JVM进程缓存
        itemCache.put(item.getId(), item);
        // 写数据到redis
        redisHandler.saveItem(item);
    }

    @Override
    public void update(Item before, Item after) {
        // 写数据到JVM进程缓存
        itemCache.put(after.getId(), after);
        // 写数据到redis
        redisHandler.saveItem(after);
    }

    @Override
    public void delete(Item item) {
        // 删除JVM进程缓存中的数据
        itemCache.invalidate(item.getId());
        // 删除redis中的数据
        redisHandler.deleteItemById(item.getId());
    }
}
