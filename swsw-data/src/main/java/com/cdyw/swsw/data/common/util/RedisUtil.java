package com.cdyw.swsw.data.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author jovi
 */
@Slf4j
public class RedisUtil {

    private static final RedisTemplate<String, Object> REDIS_TEMPLATE = SpringContextUtil.getBean("redisTemplate", RedisTemplate.class);

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return boolean
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                REDIS_TEMPLATE.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis：指定缓存失效时间-异常】", e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效;如果该key已经过期,将返回"-2";
     */
    public static long getExpire(String key) {
        return REDIS_TEMPLATE.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean exists(String key) {
        try {
            return REDIS_TEMPLATE.hasKey(key);
        } catch (Exception e) {
            log.error("【redis：判断{}是否存在-异常】", key, e);
            return false;
        }
    }


    /**********************************************************************************
     * redis-String类型的操作
     **********************************************************************************/

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key, Object value) {
        try {
            REDIS_TEMPLATE.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("【redis：普通缓存放入-异常】", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                REDIS_TEMPLATE.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis：普通缓存放入并设置时间-异常】", e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    @SuppressWarnings("unchecked")
    public static long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return REDIS_TEMPLATE.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    @SuppressWarnings("unchecked")
    public static long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return REDIS_TEMPLATE.opsForValue().increment(key, -delta);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                REDIS_TEMPLATE.delete(key[0]);
            } else {
                REDIS_TEMPLATE.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    /**
     * 获取缓存
     *
     * @param key   redis的key
     * @param clazz value的class类型
     * @param <T>
     * @return value的实际对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> clazz) {
        Object obj = key == null ? null : REDIS_TEMPLATE.opsForValue().get(key);
        assert obj != null;
        if (!obj.getClass().isAssignableFrom(clazz)) {
            throw new ClassCastException("类转化异常");
        }
        return (T) obj;
    }

    /**
     * 获取泛型
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return key == null ? null : REDIS_TEMPLATE.opsForValue().get(key);
    }

}
