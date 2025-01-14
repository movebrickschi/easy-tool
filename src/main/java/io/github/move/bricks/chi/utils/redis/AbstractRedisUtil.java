package io.github.move.bricks.chi.utils.redis;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import io.github.move.bricks.chi.constants.LuaScript;
import jakarta.annotation.Resource;
import lombok.*;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * reidis抽象基础操作类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
public abstract class AbstractRedisUtil {

    @Resource(name = "redisTemplateByJacksonSerializer")
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private Redisson redisson;

    @Resource
    private RBloomFilter<String> agentBloomFilter;

    private static final String NOT_FIND_RESOURCE = "未查询出资源~~";

    /**
     * 根据前缀移除
     * @param pre 前缀
     * @param count 每次扫描数量
     */
    public void removeByPre(String pre, Integer... count) {
        Cursor<String> scan = redisTemplate.scan(ScanOptions.scanOptions()
                .count(ArrayUtil.isEmpty(count) ? 1000 : count[0])
                .match(pre).build());

        Set<String> keys = new HashSet<>();
        while (scan.hasNext()) {
            keys.add(scan.next());
        }
        redisTemplate.delete(keys);
    }

    /**
     * 根据key获取对象
     * @param key key
     * @param beanClass 返回对象类型
     * @return 对象
     * @param <T> 泛型
     */
    public <T> T get(String key, Class<T> beanClass) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            if (String.class.equals(beanClass)) {
                // If tClass is String, directly return the data as String
                return (T) redisTemplate.opsForValue().get(key).toString();
            }
            if (beanClass == byte.class || beanClass == Byte.class ||
                    beanClass == short.class || beanClass == Short.class ||
                    beanClass == int.class || beanClass == Integer.class ||
                    beanClass == long.class || beanClass == Long.class ||
                    beanClass == float.class || beanClass == Float.class ||
                    beanClass == double.class || beanClass == Double.class ||
                    beanClass == BigDecimal.class || beanClass == BigInteger.class) {
                return (T) redisTemplate.opsForValue().get(key);
            }
            return JSONUtil.toBean(JSONUtil.toJsonStr(redisTemplate.opsForValue().get(key)), beanClass);
        }
        return null;
    }

    /**
     * 根据key获取对象
     * @param key key
     * @return true or false
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 多个key同时递减
     * @param value 扣减值
     * @param keys 多个key
     */
    public void decrement(Integer value, String... keys) {
        execute(LuaScript.DECREMENT_MULTIPLE, value, keys);
    }

    /**
     * 单个key扣减自定义数量
     * @param key key
     * @param delta 扣减数量,默认1
     * @return 扣减后剩余数量
     */
    public Long decrement(String key, Long... delta) {
        if (ArrayUtil.isEmpty(delta)) {
            delta = new Long[]{1L};
        }
        return redisTemplate.opsForValue().decrement(key, delta[0]);
    }

    /**
     * 单个key递增自定义数量
     * @param key key
     * @param delta 递增数量
     * @return 递增后剩余数量
     */
    public Long increment(String key, Long... delta) {
        if (delta == null) {
            delta = new Long[]{1L};
        }
        return redisTemplate.opsForValue().increment(key, delta[0]);
    }

    /**
     * 单个key递增自定义数量并设置过期时间
     * @param key key
     * @param timeout 时间数
     * @param unit 时间单位
     * @param delta 递增数量
     * @return 递增后剩余数量
     */
    public Long increment(String key, long timeout, TimeUnit unit, Long... delta) {
        if (delta == null) {
            delta = new Long[]{1L};
        }
        return execute(LuaScript.INCREMENT_SET_EXPIRE, key, timeout, unit, delta[0]);
    }

    /**
     * 多个key同时递增自定义数值
     * @param value 递增值
     * @param keys 多个key
     */
    public void increment(Integer value, String... keys) {
        execute(LuaScript.INCREMENT_MULTIPLE, value, keys);
    }

    /**
     * 设置key-value，并且同时设置过期时间
     * @param key key
     * @param value value
     * @param timeout 时间数
     * @param unit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 单个或多个key同时删除
     * @param key 单个或多个key
     */
    public void delete(String... key) {
        redisTemplate.delete(Arrays.asList(key));
    }

    /**
     * 哈希存储单个key
     * @param key key
     * @param hashKey hashKey
     * @param value value
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void HSet(String key, String hashKey, Object value, long timeout, TimeUnit unit) {
        executeLuaScript(key, hashKey, timeout, unit, JSONUtil.toJsonStr(value));
    }


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
    @SneakyThrows
    public <R> R executeForValue(String key, Class<R> beanClass, long timeout, TimeUnit unit,
                                 SupplierThrow<R> supplier) {
        if (agentBloomFilter.contains(key)) {
            throw new RuntimeException(NOT_FIND_RESOURCE);
        }
        Boolean b = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(b)) {
            return JSONUtil.toBean(JSONUtil.toJsonStr(redisTemplate.opsForValue().get(key)), beanClass);
        } else {
            R r = supplier.get();
            if (Objects.isNull(r)) {
                agentBloomFilter.add(key);
                throw new RuntimeException(NOT_FIND_RESOURCE);
            }
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(r), timeout, unit);
            return r;
        }
    }


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
    @SneakyThrows
    public <T, R> R executeForValue(String key, Class<R> beanClass, long timeout, TimeUnit unit, T t, FunctionThrow<T
            , R> function) {
        if (agentBloomFilter.contains(key)) {
            throw new RuntimeException(NOT_FIND_RESOURCE);
        }
        Boolean b = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(b)) {
            return JSONUtil.toBean(JSONUtil.toJsonStr(redisTemplate.opsForValue().get(key)), beanClass);
        } else {
            R r = function.apply(t);
            if (Objects.isNull(r)) {
                agentBloomFilter.add(key);
                throw new RuntimeException(NOT_FIND_RESOURCE);
            }
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(r), timeout, unit);
            return r;
        }
    }

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
    @SneakyThrows
    public <R> R executeForValueContainNull(String key, Class<R> beanClass, long timeout, TimeUnit unit,
                                            SupplierThrow<R> supplier) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Object result = redisTemplate.opsForValue().get(key);
            if (Objects.isNull(result)) {
                return null;
            }
            return JSONUtil.toBean(JSONUtil.toJsonStr(redisTemplate.opsForValue().get(key)), beanClass);
        } else {
            R r = supplier.get();
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(r), timeout, unit);
            return r;
        }
    }

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
    @SneakyThrows
    public <R extends Number> R executeForNumberValue(String key, long timeout, TimeUnit unit,
                                                      SupplierThrow<R> supplier) {
        if (agentBloomFilter.contains(key)) {
            throw new RuntimeException(NOT_FIND_RESOURCE);
        }
        Boolean b = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(b)) {
            Object value = redisTemplate.opsForValue().get(key);
            return (R) value;
        } else {
            R r = supplier.get();
            if (Objects.isNull(r)) {
                agentBloomFilter.add(key);
                throw new RuntimeException(NOT_FIND_RESOURCE);
            }
            redisTemplate.opsForValue().set(key, r, timeout, unit);
            return r;
        }
    }

    /**
     * 执行保存数字方法,一个key,多个值
     * @param key key
     * @param list 参数
     * @param supplier 获取数据的方法
     * @return 返回值
     * @param <R> 返回类型
     */
    @SneakyThrows
    public <R extends Number> R executeForNumberValue(String key, List<RedisDo> list, SupplierThrow<R> supplier) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Object value = redisTemplate.opsForValue().get(key);
            return (R) value;
        } else {
            R r = supplier.get();
            executeLuaScript(list, r);
            return r;
        }
    }


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
    @SneakyThrows
    public <R> R executeForHash(String key, String hashKey, Class<R> beanClass, long timeout, TimeUnit unit,
                                SupplierThrow<R> supplier) {
        if (agentBloomFilter.contains(key)) {
            throw new RuntimeException(NOT_FIND_RESOURCE);
        }
        if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, hashKey))) {
            return JSONUtil.toBean(JSONUtil.toJsonStr(redisTemplate.opsForHash().get(key, hashKey)), beanClass);
        } else {
            R r = supplier.get();
            if (Objects.isNull(r)) {
                agentBloomFilter.add(key);
                throw new RuntimeException(NOT_FIND_RESOURCE);
            }
            executeLuaScript(key, hashKey, timeout, unit, JSONUtil.toJsonStr(r));
            return r;
        }
    }


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
    @SneakyThrows
    public <R> R executeForHashContainNull(String key, String hashKey, Class<R> beanClass, long timeout, TimeUnit unit,
                                           SupplierThrow<R> supplier) {
        if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, hashKey))) {
            Object result = redisTemplate.opsForHash().get(key, hashKey);
            if (Objects.isNull(result)) {
                return null;
            }
            return JSONUtil.toBean(JSONUtil.toJsonStr(result), beanClass);
        } else {
            R r = supplier.get();
            executeLuaScript(key, hashKey, timeout, unit, JSONUtil.toJsonStr(r));
            return r;
        }
    }

    /**
     * 使用返回里面的过期时间
     * @param key key
     * @param hashKey hashKey
     * @param beanClass 返回类型
     * @param supplier 获取数据的方法
     * @return 返回R类型的值
     * @param <R> 返回类型
     */
    @SneakyThrows
    public <R extends RedisResult> R executeForHashContainNull(String key, String hashKey, Class<R> beanClass,
                                                               SupplierThrow<R> supplier) {
        if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, hashKey))) {
            Object result = redisTemplate.opsForHash().get(key, hashKey);
            if (Objects.isNull(result)) {
                return null;
            }
            return JSONUtil.toBean(JSONUtil.toJsonStr(result), beanClass);
        } else {
            R r = supplier.get();
            executeLuaScript(key, hashKey, r != null ? r.getCommonExpirationDate().getSecond() : 30, TimeUnit.SECONDS,
                    JSONUtil.toJsonStr(r));
            return r;
        }
    }

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
    @SneakyThrows
    public <R> R executeForHashContainNull(String key, String hashKey, TypeReference<R> typeR, long timeout,
                                           TimeUnit unit,
                                           SupplierThrow<R> supplier) {
        if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, hashKey))) {
            Object result = redisTemplate.opsForHash().get(key, hashKey);
            if (Objects.isNull(result)) {
                return null;
            }
            return JSONUtil.toBean(JSONUtil.toJsonStr(result), typeR, false);
        } else {
            R r = supplier.get();
            executeLuaScript(key, hashKey, timeout, unit, JSONUtil.toJsonStr(r));
            return r;
        }
    }


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
    @SneakyThrows
    public <R> List<R> executeForListContainNull(String key, Class<R> beanClass, long timeout, TimeUnit unit,
                                                 SupplierThrow<List<R>> supplier) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long size = redisTemplate.opsForList().size(key);
            if (Objects.isNull(size) || size == 0) {
                return Collections.emptyList();
            }
            List<Object> result = redisTemplate.opsForList().range(key, 0, -1);
            return JSONUtil.toList(JSONUtil.toJsonStr(result), beanClass);
        } else {
            List<R> r = supplier.get();
            executeLuaScript(key, timeout, unit, r.toArray());
            return r;
        }
    }

    /**
     * 执行hash
     * @param key key
     * @param hashKey hashKey
     * @param timeout 超时时长
     * @param unit 超时单位
     * @param value 值
     * @return True成功, false失败
     */
    private Boolean executeLuaScript(String key, String hashKey, long timeout, TimeUnit unit, String... value) {
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
    private Boolean executeLuaScript(String key, long timeout, TimeUnit unit, Object... value) {
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
    private void executeLuaScript(List<RedisDo> list, Object value) {
        execute(LuaScript.SETEX_MULTIPLE, list, value);
    }

    /**
     * 执行多个key,同一个值
     * @param luaScript lua脚本
     * @param value 值
     * @param keys 多个key
     */
    private void execute(String luaScript, Object value, String... keys) {
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
    private void execute(String luaScript, List<RedisDo> list, Object value) {
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
    private Boolean execute(String luaScript, List<String> keys, long timeout, TimeUnit unit, Object... value) {
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
    private Long execute(String luaScript, String key, long timeout, TimeUnit unit, Long value) {
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
    public static class RedisResult {

        private LocalDateTime commonExpirationDate;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RedisDo {
        private String key;

        private Object value;

        private long timeout;

        private TimeUnit unit;
    }

}
