package com.albedo.java.spring.boot.autoconfigure.lock.redis.lock;

import lombok.Data;
import lombok.ToString;

/**
 * @author zengzh
 * @date create at 2018/5/16 15:06
 */
@Data
@ToString
public class RedisLockInfo {

    /**
     * 锁ID UUID
     */
    private String lockId;

    /**
     * REDIS KEY
     */
    private String redisKey;

    /**
     * 过期时间
     */
    private Long expire;


    public RedisLockInfo(String lockId, String redisKey, Long expire) {
        this.lockId = lockId;
        this.redisKey = redisKey;
        this.expire = expire;
    }

}
