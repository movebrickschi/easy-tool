package io.github.movebrickschi.easytool.core.utils.date;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * EasyDateUtil
 *
 * @author MoveBricks Chi
 */
public final class EasyDateUtil {

    private EasyDateUtil() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== 转换方法 ====================

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date parseDate(LocalDateTime localDateTime) {
        return DateUtil.date(localDateTime);
    }

    /**
     * 字符串转 Date (ISO格式)
     *
     * @param localDateTime 日期时间字符串
     * @return Date对象
     */
    public static Date parseDate(String localDateTime) {
        return parseDate(LocalDateTime.parse(localDateTime));
    }

    /**
     * LocalDateTime 转字符串
     *
     * @param localDateTime LocalDateTime对象
     * @param datePattern   日期格式（可选）
     * @return 格式化后的日期字符串
     */
    public static String parseDate(LocalDateTime localDateTime, String... datePattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        if (ArrayUtil.isNotEmpty(datePattern)) {
            formatter = DateTimeFormatter.ofPattern(datePattern[0]);
        }
        return localDateTime.format(formatter);
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseLocalDateTime(Date date) {
        return DateUtil.toLocalDateTime(date);
    }

    /**
     * 字符串转 LocalDateTime
     *
     * @param dateStr     日期时间字符串
     * @param datePattern 日期格式（可选，默认：yyyy-MM-dd HH:mm:ss）
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String... datePattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        if (ArrayUtil.isNotEmpty(datePattern)) {
            formatter = DateTimeFormatter.ofPattern(datePattern[0]);
        }
        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * Date 转 LocalDate
     *
     * @param date Date对象
     * @return LocalDate对象
     */
    public static LocalDate parseLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 字符串转 LocalDate
     *
     * @param dateStr     日期字符串
     * @param datePattern 日期格式（可选，默认：yyyy-MM-dd）
     * @return LocalDate对象
     */
    public static LocalDate parseLocalDate(String dateStr, String... datePattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        if (ArrayUtil.isNotEmpty(datePattern)) {
            formatter = DateTimeFormatter.ofPattern(datePattern[0]);
        }
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * LocalDate 转 Date
     *
     * @param localDate LocalDate对象
     * @return Date对象
     */
    public static Date parseDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // ==================== 格式化方法 ====================

    /**
     * 格式化 Date 为字符串
     *
     * @param date        Date对象
     * @param datePattern 日期格式（可选，默认：yyyy-MM-dd HH:mm:ss）
     * @return 格式化后的日期字符串
     */
    public static String format(Date date, String... datePattern) {
        if (date == null) {
            return null;
        }
        String pattern = DatePattern.NORM_DATETIME_PATTERN;
        if (ArrayUtil.isNotEmpty(datePattern)) {
            pattern = datePattern[0];
        }
        return DateUtil.format(date, pattern);
    }

    /**
     * 格式化 LocalDate 为字符串
     *
     * @param localDate   LocalDate对象
     * @param datePattern 日期格式（可选，默认：yyyy-MM-dd）
     * @return 格式化后的日期字符串
     */
    public static String format(LocalDate localDate, String... datePattern) {
        if (localDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        if (ArrayUtil.isNotEmpty(datePattern)) {
            formatter = DateTimeFormatter.ofPattern(datePattern[0]);
        }
        return localDate.format(formatter);
    }

    // ==================== 获取当前时间方法 ====================

    /**
     * 获取当前时间字符串
     *
     * @param datePattern 日期格式（可选，默认：yyyy-MM-dd HH:mm:ss）
     * @return 当前时间字符串
     */
    public static String now(String... datePattern) {
        return format(new Date(), datePattern);
    }

    /**
     * 获取当前日期字符串
     *
     * @return 当前日期字符串（yyyy-MM-dd）
     */
    public static String today() {
        return format(LocalDate.now());
    }

    /**
     * 获取当前 LocalDateTime
     *
     * @return 当前LocalDateTime对象
     */
    public static LocalDateTime nowLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前 LocalDate
     *
     * @return 当前LocalDate对象
     */
    public static LocalDate nowLocalDate() {
        return LocalDate.now();
    }

    // ==================== 日期计算方法 ====================

    /**
     * 增加天数
     *
     * @param date Date对象
     * @param days 天数
     * @return 计算后的Date对象
     */
    public static Date plusDays(Date date, long days) {
        return parseDate(parseLocalDateTime(date).plusDays(days));
    }

    /**
     * 减少天数
     *
     * @param date Date对象
     * @param days 天数
     * @return 计算后的Date对象
     */
    public static Date minusDays(Date date, long days) {
        return parseDate(parseLocalDateTime(date).minusDays(days));
    }

    /**
     * 增加小时
     *
     * @param date  Date对象
     * @param hours 小时数
     * @return 计算后的Date对象
     */
    public static Date plusHours(Date date, long hours) {
        return parseDate(parseLocalDateTime(date).plusHours(hours));
    }

    /**
     * 减少小时
     *
     * @param date  Date对象
     * @param hours 小时数
     * @return 计算后的Date对象
     */
    public static Date minusHours(Date date, long hours) {
        return parseDate(parseLocalDateTime(date).minusHours(hours));
    }

    /**
     * 增加分钟
     *
     * @param date    Date对象
     * @param minutes 分钟数
     * @return 计算后的Date对象
     */
    public static Date plusMinutes(Date date, long minutes) {
        return parseDate(parseLocalDateTime(date).plusMinutes(minutes));
    }

    /**
     * 减少分钟
     *
     * @param date    Date对象
     * @param minutes 分钟数
     * @return 计算后的Date对象
     */
    public static Date minusMinutes(Date date, long minutes) {
        return parseDate(parseLocalDateTime(date).minusMinutes(minutes));
    }

    /**
     * 增加月份
     *
     * @param date   Date对象
     * @param months 月数
     * @return 计算后的Date对象
     */
    public static Date plusMonths(Date date, long months) {
        return parseDate(parseLocalDateTime(date).plusMonths(months));
    }

    /**
     * 减少月份
     *
     * @param date   Date对象
     * @param months 月数
     * @return 计算后的Date对象
     */
    public static Date minusMonths(Date date, long months) {
        return parseDate(parseLocalDateTime(date).minusMonths(months));
    }

    /**
     * 增加年份
     *
     * @param date  Date对象
     * @param years 年数
     * @return 计算后的Date对象
     */
    public static Date plusYears(Date date, long years) {
        return parseDate(parseLocalDateTime(date).plusYears(years));
    }

    /**
     * 减少年份
     *
     * @param date  Date对象
     * @param years 年数
     * @return 计算后的Date对象
     */
    public static Date minusYears(Date date, long years) {
        return parseDate(parseLocalDateTime(date).minusYears(years));
    }

    // ==================== 日期比较方法 ====================

    /**
     * 判断是否在指定日期之前
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return date1是否在date2之前
     */
    public static boolean isBefore(Date date1, Date date2) {
        return parseLocalDateTime(date1).isBefore(parseLocalDateTime(date2));
    }

    /**
     * 判断是否在指定日期之后
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return date1是否在date2之后
     */
    public static boolean isAfter(Date date1, Date date2) {
        return parseLocalDateTime(date1).isAfter(parseLocalDateTime(date2));
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 天数差
     */
    public static long daysBetween(Date startDate, Date endDate) {
        LocalDateTime start = parseLocalDateTime(startDate);
        LocalDateTime end = parseLocalDateTime(endDate);
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期之间的小时差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 小时差
     */
    public static long hoursBetween(Date startDate, Date endDate) {
        LocalDateTime start = parseLocalDateTime(startDate);
        LocalDateTime end = parseLocalDateTime(endDate);
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 计算两个日期之间的分钟差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分钟差
     */
    public static long minutesBetween(Date startDate, Date endDate) {
        LocalDateTime start = parseLocalDateTime(startDate);
        LocalDateTime end = parseLocalDateTime(endDate);
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 计算两个日期之间的秒差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 秒差
     */
    public static long secondsBetween(Date startDate, Date endDate) {
        LocalDateTime start = parseLocalDateTime(startDate);
        LocalDateTime end = parseLocalDateTime(endDate);
        return ChronoUnit.SECONDS.between(start, end);
    }

    // ==================== 特殊日期方法 ====================

    /**
     * 获取一天的开始时间（00:00:00）
     *
     * @param date Date对象
     * @return 当天开始时间
     */
    public static Date startOfDay(Date date) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return parseDate(localDateTime.with(LocalTime.MIN));
    }

    /**
     * 获取一天的结束时间（23:59:59）
     *
     * @param date Date对象
     * @return 当天结束时间
     */
    public static Date endOfDay(Date date) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return parseDate(localDateTime.with(LocalTime.MAX));
    }

    /**
     * 获取本月第一天
     *
     * @return 本月第一天的Date对象
     */
    public static Date firstDayOfMonth() {
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        return parseDate(firstDay);
    }

    /**
     * 获取本月最后一天
     *
     * @return 本月最后一天的Date对象
     */
    public static Date lastDayOfMonth() {
        LocalDate lastDay = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return parseDate(lastDay);
    }

    /**
     * 获取指定日期所在月的第一天
     *
     * @param date Date对象
     * @return 该月第一天的Date对象
     */
    public static Date firstDayOfMonth(Date date) {
        LocalDate localDate = parseLocalDate(date);
        return parseDate(localDate.withDayOfMonth(1));
    }

    /**
     * 获取指定日期所在月的最后一天
     *
     * @param date Date对象
     * @return 该月最后一天的Date对象
     */
    public static Date lastDayOfMonth(Date date) {
        LocalDate localDate = parseLocalDate(date);
        return parseDate(localDate.withDayOfMonth(localDate.lengthOfMonth()));
    }

    /**
     * 判断是否为同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否为同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = parseLocalDate(date1);
        LocalDate localDate2 = parseLocalDate(date2);
        return localDate1.equals(localDate2);
    }

    /**
     * 判断是否为今天
     *
     * @param date Date对象
     * @return 是否为今天
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }

    /**
     * 判断日期是否在指定范围内（包含边界）
     *
     * @param date      要判断的日期
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 是否在范围内
     */
    public static boolean isBetween(Date date, Date startDate, Date endDate) {
        return !isBefore(date, startDate) && !isAfter(date, endDate);
    }

    // ==================== 周相关方法 ====================

    /**
     * 获取本周第一天（周一）
     *
     * @return 本周第一天的Date对象
     */
    public static Date firstDayOfWeek() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        return parseDate(monday);
    }

    /**
     * 获取本周最后一天（周日）
     *
     * @return 本周最后一天的Date对象
     */
    public static Date lastDayOfWeek() {
        LocalDate today = LocalDate.now();
        LocalDate sunday = today.plusDays(7 - today.getDayOfWeek().getValue());
        return parseDate(sunday);
    }

    /**
     * 获取指定日期所在周的第一天（周一）
     *
     * @param date Date对象
     * @return 该周第一天的Date对象
     */
    public static Date firstDayOfWeek(Date date) {
        LocalDate localDate = parseLocalDate(date);
        LocalDate monday = localDate.minusDays(localDate.getDayOfWeek().getValue() - 1);
        return parseDate(monday);
    }

    /**
     * 获取指定日期所在周的最后一天（周日）
     *
     * @param date Date对象
     * @return 该周最后一天的Date对象
     */
    public static Date lastDayOfWeek(Date date) {
        LocalDate localDate = parseLocalDate(date);
        LocalDate sunday = localDate.plusDays(7 - localDate.getDayOfWeek().getValue());
        return parseDate(sunday);
    }

    /**
     * 判断是否为周末
     *
     * @param date Date对象
     * @return 是否为周末（周六或周日）
     */
    public static boolean isWeekend(Date date) {
        LocalDate localDate = parseLocalDate(date);
        int dayOfWeek = localDate.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7;
    }

    /**
     * 判断是否为工作日
     *
     * @param date Date对象
     * @return 是否为工作日（周一到周五）
     */
    public static boolean isWeekday(Date date) {
        return !isWeekend(date);
    }

    // ==================== 时间戳相关方法 ====================

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 当前时间戳
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     *
     * @return 当前时间戳（秒）
     */
    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 时间戳转Date（毫秒）
     *
     * @param timestamp 时间戳（毫秒）
     * @return Date对象
     */
    public static Date timestampToDate(long timestamp) {
        return new Date(timestamp);
    }

    /**
     * 时间戳转Date（秒）
     *
     * @param timestampSeconds 时间戳（秒）
     * @return Date对象
     */
    public static Date timestampSecondsToDate(long timestampSeconds) {
        return new Date(timestampSeconds * 1000);
    }

    /**
     * Date转时间戳（毫秒）
     *
     * @param date Date对象
     * @return 时间戳（毫秒）
     */
    public static long dateToTimestamp(Date date) {
        return date.getTime();
    }

    /**
     * Date转时间戳（秒）
     *
     * @param date Date对象
     * @return 时间戳（秒）
     */
    public static long dateToTimestampSeconds(Date date) {
        return date.getTime() / 1000;
    }

    // ==================== 年龄相关方法 ====================

    /**
     * 根据生日计算年龄
     *
     * @param birthDate 生日
     * @return 年龄
     */
    public static int getAge(Date birthDate) {
        return getAge(birthDate, new Date());
    }

    /**
     * 根据生日计算指定日期的年龄
     *
     * @param birthDate 生日
     * @param targetDate 目标日期
     * @return 年龄
     */
    public static int getAge(Date birthDate, Date targetDate) {
        LocalDate birth = parseLocalDate(birthDate);
        LocalDate target = parseLocalDate(targetDate);
        return (int) ChronoUnit.YEARS.between(birth, target);
    }

    // ==================== 季度相关方法 ====================

    /**
     * 获取指定日期所在的季度（1-4）
     *
     * @param date Date对象
     * @return 季度（1-4）
     */
    public static int getQuarter(Date date) {
        LocalDate localDate = parseLocalDate(date);
        return (localDate.getMonthValue() - 1) / 3 + 1;
    }

    /**
     * 获取指定季度的第一天
     *
     * @param year    年份
     * @param quarter 季度（1-4）
     * @return 该季度第一天的Date对象
     */
    public static Date firstDayOfQuarter(int year, int quarter) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter must be between 1 and 4");
        }
        int month = (quarter - 1) * 3 + 1;
        LocalDate firstDay = LocalDate.of(year, month, 1);
        return parseDate(firstDay);
    }

    /**
     * 获取指定季度的最后一天
     *
     * @param year    年份
     * @param quarter 季度（1-4）
     * @return 该季度最后一天的Date对象
     */
    public static Date lastDayOfQuarter(int year, int quarter) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter must be between 1 and 4");
        }
        int month = quarter * 3;
        LocalDate lastDay = LocalDate.of(year, month, 1);
        lastDay = lastDay.withDayOfMonth(lastDay.lengthOfMonth());
        return parseDate(lastDay);
    }

    // ==================== 年份相关方法 ====================

    /**
     * 获取本年第一天
     *
     * @return 本年第一天的Date对象
     */
    public static Date firstDayOfYear() {
        LocalDate firstDay = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        return parseDate(firstDay);
    }

    /**
     * 获取本年最后一天
     *
     * @return 本年最后一天的Date对象
     */
    public static Date lastDayOfYear() {
        LocalDate lastDay = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        return parseDate(lastDay);
    }

    /**
     * 获取指定日期所在年的第一天
     *
     * @param date Date对象
     * @return 该年第一天的Date对象
     */
    public static Date firstDayOfYear(Date date) {
        LocalDate localDate = parseLocalDate(date);
        LocalDate firstDay = LocalDate.of(localDate.getYear(), 1, 1);
        return parseDate(firstDay);
    }

    /**
     * 获取指定日期所在年的最后一天
     *
     * @param date Date对象
     * @return 该年最后一天的Date对象
     */
    public static Date lastDayOfYear(Date date) {
        LocalDate localDate = parseLocalDate(date);
        LocalDate lastDay = LocalDate.of(localDate.getYear(), 12, 31);
        return parseDate(lastDay);
    }

    /**
     * 判断是否为闰年
     *
     * @param date Date对象
     * @return 是否为闰年
     */
    public static boolean isLeapYear(Date date) {
        LocalDate localDate = parseLocalDate(date);
        return localDate.isLeapYear();
    }

    // ==================== 增强的秒计算方法 ====================

    /**
     * 增加秒数
     *
     * @param date    Date对象
     * @param seconds 秒数
     * @return 计算后的Date对象
     */
    public static Date plusSeconds(Date date, long seconds) {
        return parseDate(parseLocalDateTime(date).plusSeconds(seconds));
    }

    /**
     * 减少秒数
     *
     * @param date    Date对象
     * @param seconds 秒数
     * @return 计算后的Date对象
     */
    public static Date minusSeconds(Date date, long seconds) {
        return parseDate(parseLocalDateTime(date).minusSeconds(seconds));
    }

    /**
     * 增加周数
     *
     * @param date  Date对象
     * @param weeks 周数
     * @return 计算后的Date对象
     */
    public static Date plusWeeks(Date date, long weeks) {
        return parseDate(parseLocalDateTime(date).plusWeeks(weeks));
    }

    /**
     * 减少周数
     *
     * @param date  Date对象
     * @param weeks 周数
     * @return 计算后的Date对象
     */
    public static Date minusWeeks(Date date, long weeks) {
        return parseDate(parseLocalDateTime(date).minusWeeks(weeks));
    }

    // ==================== 月份和年份的差值计算 ====================

    /**
     * 计算两个日期之间的月份差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 月份差
     */
    public static long monthsBetween(Date startDate, Date endDate) {
        LocalDate start = parseLocalDate(startDate);
        LocalDate end = parseLocalDate(endDate);
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * 计算两个日期之间的年份差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 年份差
     */
    public static long yearsBetween(Date startDate, Date endDate) {
        LocalDate start = parseLocalDate(startDate);
        LocalDate end = parseLocalDate(endDate);
        return ChronoUnit.YEARS.between(start, end);
    }

    // ==================== 其他实用方法 ====================

    /**
     * 获取日期中的年份
     *
     * @param date Date对象
     * @return 年份
     */
    public static int getYear(Date date) {
        return parseLocalDate(date).getYear();
    }

    /**
     * 获取日期中的月份（1-12）
     *
     * @param date Date对象
     * @return 月份
     */
    public static int getMonth(Date date) {
        return parseLocalDate(date).getMonthValue();
    }

    /**
     * 获取日期中的日（1-31）
     *
     * @param date Date对象
     * @return 日
     */
    public static int getDayOfMonth(Date date) {
        return parseLocalDate(date).getDayOfMonth();
    }

    /**
     * 获取日期是星期几（1-7，周一到周日）
     *
     * @param date Date对象
     * @return 星期几
     */
    public static int getDayOfWeek(Date date) {
        return parseLocalDate(date).getDayOfWeek().getValue();
    }

    /**
     * 获取日期是一年中的第几天
     *
     * @param date Date对象
     * @return 一年中的第几天
     */
    public static int getDayOfYear(Date date) {
        return parseLocalDate(date).getDayOfYear();
    }

    /**
     * 获取日期中的小时（0-23）
     *
     * @param date Date对象
     * @return 小时
     */
    public static int getHour(Date date) {
        return parseLocalDateTime(date).getHour();
    }

    /**
     * 获取日期中的分钟（0-59）
     *
     * @param date Date对象
     * @return 分钟
     */
    public static int getMinute(Date date) {
        return parseLocalDateTime(date).getMinute();
    }

    /**
     * 获取日期中的秒（0-59）
     *
     * @param date Date对象
     * @return 秒
     */
    public static int getSecond(Date date) {
        return parseLocalDateTime(date).getSecond();
    }

    /**
     * 修改日期的年份
     *
     * @param date Date对象
     * @param year 年份
     * @return 修改后的Date对象
     */
    public static Date withYear(Date date, int year) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return parseDate(localDateTime.withYear(year));
    }

    /**
     * 修改日期的月份
     *
     * @param date  Date对象
     * @param month 月份（1-12）
     * @return 修改后的Date对象
     */
    public static Date withMonth(Date date, int month) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return parseDate(localDateTime.withMonth(month));
    }

    /**
     * 修改日期的日
     *
     * @param date       Date对象
     * @param dayOfMonth 日（1-31）
     * @return 修改后的Date对象
     */
    public static Date withDayOfMonth(Date date, int dayOfMonth) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return parseDate(localDateTime.withDayOfMonth(dayOfMonth));
    }

    /**
     * 修改日期的小时
     *
     * @param date Date对象
     * @param hour 小时（0-23）
     * @return 修改后的Date对象
     */
    public static Date withHour(Date date, int hour) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return parseDate(localDateTime.withHour(hour));
    }

    /**
     * 修改日期的分钟
     *
     * @param date   Date对象
     * @param minute 分钟（0-59）
     * @return 修改后的Date对象
     */
    public static Date withMinute(Date date, int minute) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return parseDate(localDateTime.withMinute(minute));
    }

    /**
     * 修改日期的秒
     *
     * @param date   Date对象
     * @param second 秒（0-59）
     * @return 修改后的Date对象
     */
    public static Date withSecond(Date date, int second) {
        LocalDateTime localDateTime = parseLocalDateTime(date);
        return parseDate(localDateTime.withSecond(second));
    }

    /**
     * 获取两个日期中较早的日期
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 较早的日期
     */
    public static Date min(Date date1, Date date2) {
        return isBefore(date1, date2) ? date1 : date2;
    }

    /**
     * 获取两个日期中较晚的日期
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 较晚的日期
     */
    public static Date max(Date date1, Date date2) {
        return isAfter(date1, date2) ? date1 : date2;
    }

}
