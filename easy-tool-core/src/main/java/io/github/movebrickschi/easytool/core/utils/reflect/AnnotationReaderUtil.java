package io.github.movebrickschi.easytool.core.utils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * AnnotationReaderUtil
 * 使用示例：
 * {@code
 *
 *  String packageName = "com.xxxx.Demo"; // 替换为你的包名
 *         List<Field> fields = AnnotationReaderUtil.getAnnotationFields(packageName, AuditLogTarget.class);
 * <p>
 *         for (Field field : fields) {
 *             AuditLogTarget label = field.getAnnotation(AuditLogTarget.class);
 *             System.out.println("字段名: " + field.getName() + ", 标签值: " + label.value());
 *         }
 * <p>
 * }
 * @author MoveBricks Chi
 */
public final class AnnotationReaderUtil {

    private AnnotationReaderUtil() {
    }

    public static List<Class<?>> getClassesInPackage(String packageName) throws Exception {
        // 注意：实际项目中需要动态扫描包下的类，可借助工具如Reflections库
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Class.forName(packageName));
        return classes;
    }

    /**
     * 获取所有带有制定注解的字段
     * @param packageName 实体类包名
     * @param tClass 注解类
     * @return 带有指定注解的字段
     */
    public static <T extends Annotation> List<Field> getAnnotationFields(String packageName, Class<T> tClass) throws Exception {
        List<Class<?>> classes = getClassesInPackage(packageName);
        List<Field> labeledFields = new ArrayList<>();

        for (Class<?> clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(tClass)) {
                    labeledFields.add(field);
                }
            }
        }
        return labeledFields;
    }

}
