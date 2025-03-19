package io.github.movebrickschi.easytool.core.utils.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.lang.reflect.Field;

/**
 * 实体wrapper工具类
 *
 * @author MoveBricks Chi
 * @version 1.0
 */
public class QueryWrapperUtils {

    public static <T> QueryWrapper<T> getByNotNullField(T t, Class<?> clazz) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(t) != null && !"serialVersionUID".equals(field.getName())) {
                    queryWrapper.eq(fieldToColumn(field.getName()), field.get(t));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return queryWrapper;
    }

    private static String fieldToColumn(String field) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            //判断首字母是否为大写字母
            if ('A' <= field.charAt(i) && field.charAt(i) <= 'Z') {
                sb.append("_").append(field.charAt(i));
            } else {
                sb.append(field.charAt(i));
            }
        }
        return sb.toString();
    }
}
