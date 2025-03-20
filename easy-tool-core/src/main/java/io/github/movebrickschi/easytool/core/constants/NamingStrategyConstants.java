package io.github.movebrickschi.easytool.core.constants;

import java.io.Serial;
import java.io.Serializable;

/**
 * 命名策略常量
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
public final class NamingStrategyConstants implements Serializable {

    /**
     * 作为读取数据时，将小写开头的驼峰式数据转换为大写开头的驼峰式对象
     * <p>例如：numberValue -> NumberValue
     * <p>作为写入数据时则相反：例如：NumberValue -> numberValue
     */
    public static final String LOWER_CAMEL_CASE = "LOWER_CAMEL_CASE";
    /**
     * 作为读取数据时，将大写开头的正常驼峰式数据转换为小写开头的驼峰式对象
     * <p>例如：NumberValue -> numberValue
     * <p>作为写入数据时则相反：例如：numberValue -> NumberValue
     */
    public static final String UPPER_CAMEL_CASE = "UPPER_CAMEL_CASE";
    /**
     * 作为读取数据时，将下划线的数据转为驼峰式对象
     * <p>例如：number_value -> numberValue
     * <p>作为写入数据时则相反：例如：numberValue -> number_value
     */
    public static final String SNAKE_CASE = "SNAKE_CASE";

    /**
     * 作为读取数据时，将大写的带有下划线的数据（包括没有下划线的字段）转换为小写的驼峰式对象
     * <p>例如：NUMBER_VALUE -> numberValue
     * <p>作为写入数据时则相反：例如：numberValue -> NUMBER_VALUE
     */
    public static final String UPPER_SNAKE_CASE = "UPPER_SNAKE_CASE";
    /**
     * 作为读取数据时，将小写的数据转为驼峰式对象
     * <p>例如：numbervalue -> numberValue
     * <p>作为写入数据时则相反：例如：numberValue -> numbervalue
     */
    public static final String LOWER_CASE = "LOWER_CASE";
    /**
     *  作为读取数据时，将小写的中间用破折号的数据转换为驼峰式对象
     *  <p>例如：number-value -> numberValue
     *  <p>作为写入数据时则相反：例如：numberValue -> number-value
     */
    public static final String KEBAB_CASE = "KEBAB_CASE";
    /**
     * 作为读取数据时，将小写的中间用点号的数据转换为驼峰式对象
     * <p>例如：number.value -> numberValue
     * <p>作为写入数据时则相反：例如：numberValue -> number.value
     */
    public static final String LOWER_DOT_CASE = "LOWER_DOT_CASE";


    @Serial
    private static final long serialVersionUID = 7870836474119420271L;
}
