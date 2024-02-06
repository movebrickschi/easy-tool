package com.lcc.tool.utils;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.lcc.tool.constants.LuaScript;
import lombok.*;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * reidis操作类
 *
 * @author Liu Chunchi
 * @date 2023/12/21 13:30:53
 */
@Component
public class RedisUtil {

    @Resource(name = "redisTemplateByJacksonSerializer")
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private Redisson redisson;

    @Resource
    private RBloomFilter<String> agentBloomFilter;

    private static final String NOT_FIND_RESOURCE = "未查询出资源~~";

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

    public <T> T get(String key, Class<T> beanClass) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
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

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void decrement(Integer value, String... keys) {
        execute(LuaScript.DECREMENT_MULTIPLE, value, keys);
    }

    public Long decrement(String key, Long... delta) {
        if (ArrayUtil.isEmpty(delta)) {
            delta = new Long[]{1L};
        }
        return redisTemplate.opsForValue().decrement(key, delta[0]);
    }

    public Long increment(String key, Long... delta) {
        if (delta == null) {
            delta = new Long[]{1L};
        }
        return redisTemplate.opsForValue().increment(key, delta[0]);
    }

    public Long increment(String key, long timeout, TimeUnit unit, Long... delta) {
        if (delta == null) {
            delta = new Long[]{1L};
        }
        return execute(LuaScript.INCREMENT_SET_EXPIRE, key, timeout, unit, delta[0]);
    }

    public void increment(Integer value, String... keys) {
        execute(LuaScript.INCREMENT_MULTIPLE, value, keys);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void delete(String... key) {
        redisTemplate.delete(Arrays.asList(key));
    }

    public void HSet(String key, String hashKey, Object value, long timeout, TimeUnit unit) {
        executeLuaScript(key, hashKey, timeout, unit, JSONUtil.toJsonStr(value));
    }


    /**
     * 适用于查询一定存在的数据,不确定的数据不能用此方法,比如根据智能体NO查询智能体信息
     * 使用布隆过滤器过滤结果为空的key
     */
    @SneakyThrows
    public <R> R executeForValue(String key, Class<R> beanClass, long timeout, TimeUnit unit, SupplierThrow<R> supplier) {
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
     */
    @SneakyThrows
    public <T, R> R executeForValue(String key, Class<R> beanClass, long timeout, TimeUnit unit, T t, FunctionThrow<T, R> function) {
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


    @SneakyThrows
    public <R> R executeForValueContainNull(String key, Class<R> beanClass, long timeout, TimeUnit unit, SupplierThrow<R> supplier) {
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
     */
    @SneakyThrows
    public <R extends Number> R executeForNumberValue(String key, long timeout, TimeUnit unit, SupplierThrow<R> supplier) {
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
     */
    @SneakyThrows
    public <R extends RedisResult> R executeForHashContainNull(String key, String hashKey, Class<R> beanClass, SupplierThrow<R> supplier) {
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
     * 此方式可用于带有泛型的实体比如(Page<T>),一般的情况也可以使用只是多写几个代码
     *
     */
    @SneakyThrows
    public <R> R executeForHashContainNull(String key, String hashKey, TypeReference<R> typeR, long timeout, TimeUnit unit,
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
     * hash
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
     * list
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

    private void executeLuaScript(List<RedisDo> list, Object value) {
        execute(LuaScript.SETEX_MULTIPLE, list, value);
    }

    private void execute(String luaScript, Object value, String... keys) {
        // 创建RedisScript对象
        RedisScript<String> script = new DefaultRedisScript<>(luaScript);
        // 执行Lua脚本
        redisTemplate.execute(script, Arrays.asList(keys), value);
    }

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

    private Boolean execute(String luaScript, List<String> keys, long timeout, TimeUnit unit, Object... value) {
        Integer timeoutInSeconds = Integer.valueOf(String.valueOf(TimeoutUtils.toSeconds(timeout, unit)));

        // 创建RedisScript对象
        RedisScript<Boolean> script = new DefaultRedisScript<>(luaScript, Boolean.class);
        ArrayList<Object> list = Lists.newArrayList(timeoutInSeconds);
        Collections.addAll(list, value);
        // 执行Lua脚本
        return redisTemplate.execute(script, keys, list.toArray());
    }

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
        R apply(T t) throws Throwable;
    }


    @FunctionalInterface
    public interface SupplierThrow<R> {
        R get() throws Throwable;
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

    /*public interface RedisResult {
        LocalDateTime getExpireDateTime();


    }*/


}
