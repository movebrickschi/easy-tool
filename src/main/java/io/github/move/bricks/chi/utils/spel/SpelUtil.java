package io.github.move.bricks.chi.utils.spel;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * SPEL表达式解析参数
 *
 * @author Liu Chunchi
 * @version 1.0
 */
@Slf4j
public class SpelUtil {

    /**
     * 用于SPEL表达式解析.
     */
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 解析参数
     * @param spelString spel字符串
     * @param param  the arguments at this join point
     * @param parameterNames MethodSignature的参数名称
     * @return 解析结果
     * 案例:
     * MethodSignature signature = (MethodSignature) joinPoint.getSignature();
     *         Method method = signature.getMethod();
     *         AccessTokenAuthentication annotation = method.getAnnotation(AccessTokenAuthentication.class);
     *         Object[] args = joinPoint.getArgs();
     *         String[] parameterNames = signature.getParameterNames();
     *         String unless = annotation.unless();
     *         unless = SpelUtil.parseSpEL(unless, args, parameterNames);
     */
    public static String parseSPEL(String spelString, Object[] param, String[] parameterNames) {
        try {
            if (CharSequenceUtil.isBlank(spelString)) {
                return spelString;
            }
            if (param == null || param.length == 0) {
                return spelString;
            }
            // 解析过后的Spring表达式对象
            Expression expression = PARSER.parseExpression(spelString);
            // spring的表达式上下文对象
            EvaluationContext context = new StandardEvaluationContext();
            // 给上下文赋值
            for (int i = 0; i < param.length; i++) {
                context.setVariable(parameterNames[i], param[i]);
            }
            return expression.getValue(context).toString();
        } catch (Exception e) {
            log.info("解析异常:{},spEL:{},", e.getMessage(), spelString);
            return spelString;
        }
    }
}
