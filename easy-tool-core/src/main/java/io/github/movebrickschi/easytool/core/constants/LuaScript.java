package io.github.movebrickschi.easytool.core.constants;

/**
 * redis脚本类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
public final class LuaScript {


    /**
     * 多个key value expire
     * 语法:SETEX key seconds value
     */
    public static final String SETEX_MULTIPLE = "for i=1, #KEYS do\n" +
            "   redis.call('setex', KEYS[i], ARGV[i+2], ARGV[i])\n" +
            "end";


    /**
     * 多个key decrement 一个value
     */
    public static final String DECREMENT_MULTIPLE = "for i, key in ipairs(KEYS) do\n" +
            "    redis.call('decrby', key, ARGV[1])\n" +
            "end";


    /**
     * 多个key increment 一个value
     */
    public static final String INCREMENT_MULTIPLE = "for i, key in ipairs(KEYS) do\n" +
            "    redis.call('incrby', key, ARGV[1])\n" +
            "end";

    /**
     * increment and set expire
     */
    public static final String INCREMENT_SET_EXPIRE = "local result = redis.call('incrby', KEYS[1], ARGV[1])\n" +
            "redis.call('expire', KEYS[1], ARGV[2])\n" +
            "return result";


    /**
     * 删除多个
     */
    public static final String DELETE_MULTIPLE = "for i, key in ipairs(KEYS) do\n" +
            "    redis.call('del', key)\n" +
            "end";


}
