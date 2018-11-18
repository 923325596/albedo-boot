package com.albedo.java.spring.boot.autoconfigure.lock.redis.lock;

/**
 * @author lijie
 * @date 2017年6月14日 下午3:11:05
 * @version 1.0.0
 */
public interface DistributedLock {
	
	long TIMEOUT_MILLIS = 30000;
	
	int RETRY_TIMES = Integer.MAX_VALUE;
	
	long SLEEP_MILLIS = 500;

    RedisLockInfo lock(String key);

    RedisLockInfo lock(String key, int retryTimes);

    RedisLockInfo lock(String key, int retryTimes, long sleepMillis);

    RedisLockInfo lock(String key, long expire);

    RedisLockInfo lock(String key, long expire, int retryTimes);

    RedisLockInfo lock(String key, long expire, int retryTimes, long sleepMillis);
	
	boolean releaseLock(RedisLockInfo redisLockInfo);
}
