package io.github.move.bricks.chi.utils.object.serial;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * 自定义时间反序列化器
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 2.2.3
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        LocalDateTime localDateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        return Date.from(localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant());
    }
}
   