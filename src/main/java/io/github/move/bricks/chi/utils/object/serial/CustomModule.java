package io.github.move.bricks.chi.utils.object.serial;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;

import java.io.Serial;

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
    }

}
