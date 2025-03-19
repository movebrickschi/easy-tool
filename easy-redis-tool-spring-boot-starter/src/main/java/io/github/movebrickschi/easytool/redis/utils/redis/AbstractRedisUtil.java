package io.github.movebrickschi.easytool.redis.utils.redis;

import cn.hutool.core.lang.TypeReference;
import com.google.common.collect.Lists;
import io.github.movebrickschi.easytool.redis.constants.LuaScript;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis抽象基础操作类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
public abstract class AbstractRedisUtil {

    @Resource(name = "redisTemplateByJacksonSerializer")
    protected RedisTemplate<String, Object> redisTemplate;

    @Resource
    protected RBloomFilter<String> agentBloomFilter;

    protected static final String NOT_FIND_RESOURCE = "未查询出资源~~";

    /**
     * 根据前缀移除
     * @param pre 前缀
     * @param count 每次扫描数量
     */
    protected abstract void removeByPre(String pre, Integer... count);

    /**
     * 根据key获取对象
     * @param key key
     * @param beanClass 返回对象类型
     * @return 对象
     * @param <T> 泛型
     */
    protected abstract <T> T get(String key, Class<T> beanClass);

    /**
     * 根据key获取对象
     * @param key key
     * @return true or false
     */
    protected abstract Boolean hasKey(String key);

    /**
     * 多个key同时递减
     * @param value 扣减值
     * @param keys 多个key
     */
    protected abstract void decrement(Integer value, String... keys);

    /**
     * 单个key扣减自定义数量
     * @param key key
     * @param delta 扣减数量,默认1
     * @return 扣减后剩余数量
     */
    protected abstract Long decrement(String key, Long... delta);

    /**
     * 单个key递增自定义数量
     * @param key key
     * @param delta 递增数量
     * @return 递增后剩余数量
     */
    protected abstract Long increment(String key, Long... delta);

    /**
     * 单个key递增自定义数量并设置过期时间
     * @param key key
     * @param timeout 时间数
     * @param unit 时间单位
     * @param delta 递增数量
     * @return 递增后剩余数量
     */
    protected abstract Long increment(String key, long timeout, TimeUnit unit, Long... delta);

    /**
     * 多个key同时递增自定义数值
     * @param value 递增值
     * @param keys 多个key
     */
    protected abstract void increment(Integer value, String... keys);

    /**
     * 设置key-value，并且同时设置过期时间
     * @param key key
     * @param value value
     * @param timeout 时间数
     * @param unit 时间单位
     */
    protected abstract void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 单个或多个key同时删除
     * @param key 单个或多个key
     */
    protected abstract void delete(String... key);

    /**
     * 哈希存储单个key
     * @param key key
     * @param hashKey hashKey
     * @param value value
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    protected abstract void HSet(String key, String hashKey, Object value, long timeout, TimeUnit unit);


    /**
     * 适用于查询一定存在的数据,不确定的数据不能用此方法,比如根据智能体NO查询智能体信息
     * 使用布隆过滤器过滤结果为空的key
     * @param key key
     * @param beanClass 返回类型
     * @param timeout 时间
     * @param unit 时间单位
     * @param supplier 获取数据的方法
     * @return 返回值
     * @param <R> 返回的类型
     */
    protected abstract <R> R executeForValue(String key, Class<R> beanClass, long timeout, TimeUnit unit,
                                             SupplierThrow<R> supplier);


    /**
     * function形式
     * 适用场景同上
     * @param key key
     * @param beanClass 返回类型class
     * @param timeout 超时时间
     * @param unit 时间单位
     * @param t 参数类型
     * @param function 函数
     * @return 返回值
     * @param <T> 入参
     * @param <R> 出参
     */
    protected abstract <T, R> R executeForValue(String key, Class<R> beanClass, long timeout, TimeUnit unit, T t,
                                                FunctionThrow<T
                                                        , R> function);

    /**
     * value包含null
     * @param key key
     * @param beanClass 返回类型class
     * @param timeout 超时时间
     * @param unit 时间单位
     * @param supplier 获取数据的方法
     * @return 返回值
     * @param <R> 返回类型
     */
    protected abstract <R> R executeForValueContainNull(String key, Class<R> beanClass, long timeout, TimeUnit unit,
                                                        SupplierThrow<R> supplier);

    /**
     * 存放数字类型
     * 需要注意的是R要和redis读取的数据类型一致,如果不一致会抛异常
     * @param key key
     * @param timeout 超时时间
     * @param unit 时间单位
     * @param supplier 获取数据的方法
     * @return 返回值
     * @param <R> 返回类型
     */
    protected abstract <R extends Number> R executeForNumberValue(String key, long timeout, TimeUnit unit,
                                                                  SupplierThrow<R> supplier);

    /**
     * 执行保存数字方法,一个key,多个值
     * @param key key
     * @param list 参数
     * @param supplier 获取数据的方法
     * @return 返回值
     * @param <R> 返回类型
     */
    protected abstract <R extends Number> R executeForNumberValue(String key, List<RedisDo> list,
                                                                  SupplierThrow<R> supplier);


    /**
     * 适用于查询一定存在的数据,不确定的数据不能用此方法,比如根据智能体NO查询智能体信息
     * 使用布隆过滤器过滤结果为空的key
     * @param key key
     * @param hashKey hashKey
     * @param beanClass 返回类型
     * @param timeout 超时时长
     * @param unit 超时单位
     * @param supplier 获取数据的方法
     * @return 返回R类型的值
     * @param <R> 返回类型
     */
    protected abstract <R> R executeForHash(String key, String hashKey, Class<R> beanClass, long timeout, TimeUnit unit,
                                            SupplierThrow<R> supplier);


    /**
     * 允许为空的放入缓存
     * @param key key
     * @param hashKey hashKey
     * @param beanClass 返回类型
     * @param timeout 超时时长
     * @param unit 超时单位
     * @param supplier 获取数据的方法
     * @return 返回R类型的值
     * @param <R> 返回类型
     */
    protected abstract <R> R executeForHashContainNull(String key, String hashKey, Class<R> beanClass, long timeout,
                                                       TimeUnit unit,
                                                       SupplierThrow<R> supplier);

    /**
     * 使用返回里面的过期时间
     * @param key key
     * @param hashKey hashKey
     * @param beanClass 返回类型
     * @param supplier 获取数据的方法
     * @return 返回R类型的值
     * @param <R> 返回类型
     */
    protected abstract <R extends RedisResult> R executeForHashContainNull(String key, String hashKey,
                                                                           Class<R> beanClass,
                                                                           SupplierThrow<R> supplier);

    /**
     * 此方式可用于带有泛型的实体,一般的情况也可以使用只是多写几个代码
     * @param key key
     * @param hashKey hashKey
     * @param typeR 返回类型
     * @param timeout 超时时长
     * @param unit 超时单位
     * @param supplier 返回数据的方法
     * @return 返回R类型的值
     * @param <R> 返回类型
     */
    protected abstract <R> R executeForHashContainNull(String key, String hashKey, TypeReference<R> typeR, long timeout,
                                           TimeUnit unit,
                                                       SupplierThrow<R> supplier);


    /**
     * 执行获取List
     * @param key key
     * @param beanClass 返回类型
     * @param timeout 超时时间
     * @param unit 超时单位
     * @param supplier 获取数据的方法
     * @return 返回R类型的值
     * @param <R> 返回类型
     */
    protected abstract <R> List<R> executeForListContainNull(String key, Class<R> beanClass, long timeout,
                                                             TimeUnit unit,
                                                             SupplierThrow<List<R>> supplier);

    /**
     * 执行hash
     * @param key key
     * @param hashKey hashKey
     * @param timeout 超时时长
     * @param unit 超时单位
     * @param value 值
     * @return True成功, false失败
     */
    protected Boolean executeLuaScript(String key, String hashKey, long timeout, TimeUnit unit, String... value) {
        // Lua脚本内容
        String luaScript = "redis.call('hset',KEYS[1],KEYS[2],ARGV[2])\n" +
                "redis.call('expire', KEYS[1], ARGV[1])\n" +
                "return true";

        // 执行Lua脚本
        return execute(luaScript, Lists.newArrayList(key, hashKey), timeout, unit, value);
    }

    /**
     * 执行list脚本
     * @param key key
     * @param timeout 超时时长
     * @param unit 超时单位
     * @param value 值
     * @return True成功, false失败
     */
    protected Boolean executeLuaScript(String key, long timeout, TimeUnit unit, Object... value) {
        // Lua脚本内容
        String luaScript = "local key = KEYS[1];\n" +
                "local timeout = tonumber(ARGV[1]);\n" +
                "table.remove(ARGV, 1);\n" +
//                "redis.call(\"DEL\", key)\n" +  // 删除原有的数据 不需要可以删除这一行
                "for i=1, #(ARGV) do\n" +
                "\tredis.call(\"RPUSH\", key, ARGV[i]);\n" +
                "end\n" +
                "redis.call(\"EXPIRE\", key, timeout)\n" +
                "return true";

        return execute(luaScript, Lists.newArrayList(key), timeout, unit, value);
    }

    /**
     * 执行list脚本
     * @param list list参数
     * @param value 值
     */
    protected void executeLuaScript(List<RedisDo> list, Object value) {
        execute(LuaScript.SETEX_MULTIPLE, list, value);
    }

    /**
     * 执行多个key,同一个值
     * @param luaScript lua脚本
     * @param value 值
     * @param keys 多个key
     */
    protected void execute(String luaScript, Object value, String... keys) {
        // 创建RedisScript对象
        RedisScript<String> script = new DefaultRedisScript<>(luaScript);
        // 执行Lua脚本
        redisTemplate.execute(script, Arrays.asList(keys), value);
    }

    /**
     *一个key,多个值
     * @param luaScript lua脚本
     * @param list 值
     * @param value 值
     */
    protected void execute(String luaScript, List<RedisDo> list, Object value) {
        List<String> keys = Lists.newArrayList();
        List<Integer> valuesAndExpire = Lists.newArrayList();
        List<Integer> expire = Lists.newArrayList();
        list.forEach(it -> {
            it.setTimeout(TimeoutUtils.toSeconds(it.getTimeout(), it.getUnit()));
            keys.add(it.getKey());
            valuesAndExpire.add(Integer.valueOf(value.toString()));
            expire.add(Integer.valueOf(String.valueOf(it.getTimeout())));
        });

        valuesAndExpire.addAll(expire);

        // 创建RedisScript对象
        RedisScript<String> script = new DefaultRedisScript<>(luaScript);
        // 执行Lua脚本
        redisTemplate.execute(script, keys, valuesAndExpire.toArray());
    }

    /**
     * 多个key,多个值
     * @param luaScript 脚本
     * @param keys keys
     * @param timeout 超时时长
     * @param unit 超时单位
     * @param value 多个值
     * @return True成功, false失败
     */
    protected Boolean execute(String luaScript, List<String> keys, long timeout, TimeUnit unit, Object... value) {
        Integer timeoutInSeconds = Integer.valueOf(String.valueOf(TimeoutUtils.toSeconds(timeout, unit)));

        // 创建RedisScript对象
        RedisScript<Boolean> script = new DefaultRedisScript<>(luaScript, Boolean.class);
        ArrayList<Object> list = Lists.newArrayList(timeoutInSeconds);
        Collections.addAll(list, value);
        // 执行Lua脚本
        return redisTemplate.execute(script, keys, list.toArray());
    }

    /**
     * 单个key,单个值
     * @param luaScript lua脚本
     * @param key key
     * @param timeout 超时时长
     * @param unit 超时单位
     * @param value 值
     * @return 变更后的值
     */
    protected Long execute(String luaScript, String key, long timeout, TimeUnit unit, Long value) {
        Integer timeoutInSeconds = Integer.valueOf(String.valueOf(TimeoutUtils.toSeconds(timeout, unit)));

        // 创建RedisScript对象
        RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);
        ArrayList<Object> list = Lists.newArrayList(value);
        Collections.addAll(list, timeoutInSeconds);
        // 执行Lua脚本
        return redisTemplate.execute(script, Lists.newArrayList(key), list.toArray());
    }


    @FunctionalInterface
    public interface FunctionThrow<T, R> {
        R apply(T t);
    }


    @FunctionalInterface
    public interface SupplierThrow<R> {
        R get();
    }

    @Data
    protected static class RedisResult {

        protected LocalDateTime commonExpirationDate;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    protected static class RedisDo {
        protected String key;

        protected Object value;

        protected long timeout;

        protected TimeUnit unit;
    }

}
