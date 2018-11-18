package com.albedo.java.spring.boot.autoconfigure.lock.redis.lock.impl;

import com.albedo.java.spring.boot.autoconfigure.lock.redis.lock.AbstractDistributedLock;
import com.albedo.java.spring.boot.autoconfigure.lock.redis.lock.RedisLockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;

/**
 * @author lijie
 * @date 2017年6月14日 下午3:11:14
 * @version 1.0.0
 */
public class RedisDistributedLock extends AbstractDistributedLock {
	
	private final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);
	
	private RedisTemplate<Object, Object> redisTemplate;

    private static final String LUA_SCRIPT_LOCK = "return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])";
    private static final RedisScript<String> SCRIPT_LOCK = new DefaultRedisScript<String>(LUA_SCRIPT_LOCK, String.class);
    //    private static final String LUA_SCRIPT_UNLOCK = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final String LUA_SCRIPT_UNLOCK = "if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])) else return '0' end";
    private static final RedisScript<String> SCRIPT_UNLOCK = new DefaultRedisScript<String>(LUA_SCRIPT_UNLOCK, String.class);


    public RedisDistributedLock(RedisTemplate<Object, Object> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}

	@Override
	public RedisLockInfo lock(String key, long expire, int retryTimes, long sleepMillis) {
        RedisLockInfo result = setRedis(key, expire);
		// 如果获取锁失败，按照传入的重试次数进行重试
		while((result==null) && retryTimes-- > 0){
			try {
				logger.debug("lock failed, retrying..." + retryTimes);
				Thread.sleep(sleepMillis);
			} catch (InterruptedException e) {
				return null;
			}
			result = setRedis(key, expire);
		}
		return result;
	}

	private RedisLockInfo setRedis(String key, long expire) {
        try {
            String lockId = UUID.randomUUID().toString();
            Object lockResult = redisTemplate.execute(SCRIPT_LOCK,
                redisTemplate.getStringSerializer(),
                redisTemplate.getStringSerializer(),
                Collections.singletonList(key),
                lockId, String.valueOf(expire));
            if (null != lockResult && "OK".equals(lockResult)) {
                return new RedisLockInfo(lockId, key, expire);
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
		return null;
	}
	
	@Override
	public boolean releaseLock(RedisLockInfo redisLockInfo) {
        Object releaseResult = null;
        try {
            releaseResult = redisTemplate.execute(SCRIPT_UNLOCK,
                redisTemplate.getStringSerializer(),
                redisTemplate.getStringSerializer(),
                Collections.singletonList(redisLockInfo.getRedisKey()),
                redisLockInfo.getLockId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null != releaseResult && releaseResult.equals(1);
	}
	
}
