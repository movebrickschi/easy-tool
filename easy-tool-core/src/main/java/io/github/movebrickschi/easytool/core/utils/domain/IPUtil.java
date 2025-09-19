package io.github.movebrickschi.easytool.core.utils.domain;


import javax.servlet.http.HttpServletRequest;

/**
 * ip获取工具类
 *
 * @author MoveBricks Chi
 */
public class IPUtil {

    private IPUtil() {
    }

    private static final String UNKNOWN = "unknown";

    public static String getClientIp(HttpServletRequest request) {
        // 检查 X-Forwarded-For 头，这可能包含多个 IP 地址，用逗号分隔
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || UNKNOWN.equalsIgnoreCase(clientIp)) {
            // 如果 X-Forwarded-For 不存在或无效，尝试检查 X-Real-IP
            clientIp = request.getHeader("X-Real-IP");
            if (clientIp == null || clientIp.isEmpty() || UNKNOWN.equalsIgnoreCase(clientIp)) {
                // 如果 X-Real-IP 也不存在或无效，使用 getRemoteAddr()
                clientIp = request.getRemoteAddr();
            }
        } else {
            // 取 X-Forwarded-For 中的第一个有效 IP 地址
            String[] ipArray = clientIp.split(",");
            for (String ip : ipArray) {
                if (ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip.trim())) {
                    clientIp = ip.trim();
                    break;
                }
            }
        }
        return clientIp;
    }
}