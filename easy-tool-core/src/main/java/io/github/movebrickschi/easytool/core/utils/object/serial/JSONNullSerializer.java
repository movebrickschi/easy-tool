package io.github.movebrickschi.easytool.core.utils.object.serial;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 解决JSONNull序列化问题
 *
 * @author MoveBricks Chi
 * @version 1.0
 * @since 3.0.0
 */
public class JSONNullSerializer extends JsonSerializer<JSONNull> {

    @Override
    public void serialize(JSONNull value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNull();
    }
}
