package com.heima.item.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * @author CHAN
 * @since 2022/8/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void testRedisIncr() {
        long increment = redisTemplate.opsForValue().increment("incr:test");
        System.out.println(increment);
        String uuid1 = UUID.randomUUID().toString();//生成UUID
        System.out.println("uuid1 默认的uuid: " + uuid1);
        String uuid2 = UUID.randomUUID().toString().replace("-", "");//格式化UUID将“-”去掉
        System.out.println("uuid2 将“-”去掉:" + uuid2);
    }
}
