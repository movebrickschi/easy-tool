package io.github.move.bricks.chi.utils.object.serial;

import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;

import java.io.Serial;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 自定义序列化规则
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.1.13
 */
public class CustomModule extends SimpleModule {

    @Serial
    private static final long serialVersionUID = -6035535164802345726L;

    /**
     * 指定序列化\反序列化规则
     */
    public CustomModule() {
        super(PackageVersion.VERSION);
        // JSONNull 类型序列化
        this.addSerializer(JSONNull.class, new JSONNullSerializer());
        //----------------------time---------------------------------
        // yyyy-MM-dd HH:mm:ss
        this.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        // yyyy-MM-dd
        this.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        // HH:mm:ss
        this.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));

        // Instant 类型序列化
        this.addSerializer(Instant.class, InstantSerializer.INSTANCE);

        // Duration 类型序列化
        this.addSerializer(Duration.class, DurationSerializer.INSTANCE);

        // JSONNull 类型序列化
        this.addSerializer(JSONNull.class, new JSONNullSerializer());

        // yyyy-MM-dd HH:mm:ss
        this.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        // yyyy-MM-dd
        this.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        // HH:mm:ss
        this.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
        // Instant 反序列化
        this.addDeserializer(Instant.class, InstantDeserializer.INSTANT);

        // Duration 反序列化
        this.addDeserializer(Duration.class, DurationDeserializer.INSTANCE);

        this.addDeserializer(Date.class, new CustomDateDeserializer());

    }

}
