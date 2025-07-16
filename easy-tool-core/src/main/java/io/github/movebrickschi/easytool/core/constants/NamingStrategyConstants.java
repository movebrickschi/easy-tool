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
     * 定义:直接使用字段名，不进行格式转换。作为读取数据时，将小写开头的驼峰式数据转换为大写开头的驼峰式对象
     * <p>适用场景：JSON 字段名与 Java 属性名完全一致（如 userName）。
     * <ul>序列化规则：</ul>
     * <ol>Java 属性名 userName → JSON 字段名 userName。</ol>
     * <ul>反序列化规则：</ul>
     * <ol>JSON 字段名 userName → Java 属性名 userName。</ol>
     */
    public static final String LOWER_CAMEL_CASE = "LOWER_CAMEL_CASE";
    /**
     * 定义：强制字段名首字母大写（PascalCase）。
     * <p>适用场景：JSON 字段名是 PascalCase（如 UserName），Java 属性名是驼峰（如 userName）。
     * <ul>序列化规则：</ul>
     * <ol>Java 属性名 userName → JSON 字段名 UserName。</ol>
     * <ul>反序列化规则：</ul>
     * <ol>JSON 字段名 UserName → Java 属性名 userName。</ol>
     */
    public static final String UPPER_CAMEL_CASE = "UPPER_CAMEL_CASE";
    /**
     * 定义：将驼峰命名（CamelCase）转换为蛇形命名（snake_case）。
     * <p>适用场景：JSON 字段名是蛇形（如 user_name），Java 属性名是驼峰（如 userName）。
     * <ul>序列化规则：</ul>
     * <ol>Java 属性名 userName → JSON 字段名 user_name。</ol>
     * <ul>反序列化规则：</ul>
     * <ol>JSON 字段名 user_name → Java 属性名 userName。</ol>
     */
    public static final String SNAKE_CASE = "SNAKE_CASE";

    /**
     * 定义：将驼峰命名转换为全大写蛇形（SNAKE_CASE）。
     * <p>适用场景：JSON 字段名是全大写蛇形（如 USER_NAME），Java 属性名是驼峰（如 userName）。
     * <ul>序列化规则：</ul>
     * <ol>Java 属性名 userName → JSON 字段名 USER_NAME。</ol>
     * <ul>反序列化规则：</ul>
     * <ol>JSON 字段名 USER_NAME → Java 属性名 userName。</ol>
     */
    public static final String UPPER_SNAKE_CASE = "UPPER_SNAKE_CASE";
    /**
     * 定义：将字段名转换为 全小写字母，不使用任何分隔符。
     * <p>适用场景：需要极简格式的字段（如 username），或与某些特定接口兼容。
     * <ul>序列化规则：</ul>
     * <ol>Java 属性名 userName → JSON 字段名 username。</ol>
     * <ul>反序列化规则：</ul>
     * <ol>JSON 字段名 username → Java 属性名 userName。</ol>
     */
    public static final String LOWER_CASE = "LOWER_CASE";
    /**
     * 定义：将驼峰命名转换为短横线命名（kebab-case）。
     * <p>适用场景：JSON 字段名是短横线（如 user-name），Java 属性名是驼峰（如 userName）。
     * <ul>序列化规则：</ul>
     * <ol>Java 属性名 userName → JSON 字段名 user-name。</ol>
     * <ul>反序列化规则：</ul>
     * <ol>JSON 字段名 user-name → Java 属性名 userName。</ol>
     */
    public static final String KEBAB_CASE = "KEBAB_CASE";
    /**
     * 定义：将字段名转换为小写字母，单词间用 点号（.） 分隔。
     * <p>适用场景：适用于配置属性或需要层级结构的字段（如 user.address.city）。
     * <ul>序列化规则：</ul>
     * <ol>Java 属性名 userName → JSON 字段名 user.name。</ol>
     * <ul>反序列化规则：</ul>
     * <ol>JSON 字段名 user.name → Java 属性名 userName。</ol>
     */
    public static final String LOWER_DOT_CASE = "LOWER_DOT_CASE";


    @Serial
    private static final long serialVersionUID = 7870836474119420271L;
}
