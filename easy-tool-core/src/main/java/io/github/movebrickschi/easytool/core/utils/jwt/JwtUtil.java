package io.github.movebrickschi.easytool.core.utils.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JwtUtil
 *
 * @author MoveBricks Chi
 */
public final class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // 默认配置
    private static final long DEFAULT_EXPIRATION_HOURS = 24;
    private static final String DEFAULT_ISSUER = "jwt-util";

    // 密钥（生产环境必须从配置中心或环境变量获取，长度至少32字符）
    private final SecretKey secretKey;
    // 过期时间（小时）
    private final long expirationHours;
    // 签发者
    private final String issuer;

    /**
     * 构造函数
     * @param secret 密钥字符串（必须至少32字符，建议使用随机生成的复杂字符串）
     */
    public JwtUtil(String secret) {
        this(secret, DEFAULT_EXPIRATION_HOURS, DEFAULT_ISSUER);
    }

    /**
     * 构造函数
     * @param secret 密钥字符串
     * @param expirationHours token过期时间（小时）
     * @param issuer 签发者
     */
    public JwtUtil(String secret, long expirationHours, String issuer) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("密钥长度必须至少32个字符");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationHours = expirationHours;
        this.issuer = issuer;
    }

    /**
     * 生成JWT令牌（无额外声明）
     * @param subject 主题（通常是用户ID）
     * @return JWT字符串
     */
    public String generateToken(String subject) {
        return generateToken(subject, null);
    }

    /**
     * 生成JWT令牌（带额外声明）
     * @param subject 主题（通常是用户ID）
     * @param claims 额外声明信息
     * @return JWT字符串
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + TimeUnit.HOURS.toMillis(expirationHours));

        JwtBuilder builder = Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256);

        // 添加额外声明
        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }

        return builder.compact();
    }

    /**
     * 解析JWT令牌
     * @param token JWT字符串
     * @return Claims对象
     * @throws JwtException 解析失败时抛出异常
     */
    public Claims parseToken(String token) throws JwtException {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT令牌已过期: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            logger.warn("JWT签名验证失败: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.warn("JWT解析失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 验证JWT令牌是否有效
     * @param token JWT字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 从令牌中获取主题（用户ID）
     * @param token JWT字符串
     * @return 主题
     */
    public String getSubject(String token) throws JwtException {
        return parseToken(token).getSubject();
    }

    /**
     * 获取令牌过期时间
     * @param token JWT字符串
     * @return 过期时间
     */
    public Date getExpiration(String token) throws JwtException {
        return parseToken(token).getExpiration();
    }

    /**
     * 判断令牌是否即将过期（剩余时间少于指定小时数）
     * @param token JWT字符串
     * @param thresholdHours 阈值（小时）
     * @return 是否即将过期
     */
    public boolean isTokenExpiringSoon(String token, long thresholdHours) throws JwtException {
        Date expiration = getExpiration(token);
        long diffMillis = expiration.getTime() - System.currentTimeMillis();
        return diffMillis < TimeUnit.HOURS.toMillis(thresholdHours);
    }

    /**
     * 刷新JWT令牌（生成新的令牌）
     * @param oldToken 旧的JWT字符串
     * @return 新的JWT字符串
     * @throws JwtException 旧令牌无效时抛出异常
     */
    public String refreshToken(String oldToken) throws JwtException {
        Claims claims = parseToken(oldToken);
        Map<String, Object> originalClaims = claims.entrySet().stream()
                .filter(entry -> !"iss".equals(entry.getKey())
                        && !"sub".equals(entry.getKey())
                        && !"iat".equals(entry.getKey())
                        && !"exp".equals(entry.getKey()))
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        return generateToken(claims.getSubject(), originalClaims);
    }

    /**
     * 获取Token剩余有效时间（毫秒）
     * @param token JWT字符串
     * @return 剩余毫秒数，如果已过期返回0
     */
    public long getRemainingTimeMillis(String token) {
        try {
            Date expiration = getExpiration(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(remaining, 0);
        } catch (JwtException e) {
            return 0;
        }
    }


    public static void main(String[] args) {
        String secret = "YourSuperSecretKeyThatIsAtLeast32CharactersLong!123";
        long expirationHours = 24;
        String issuer = "liuchunchi";
        JwtUtil jwtUtil = new JwtUtil(secret, expirationHours, issuer);
        // 1. 生成Token
        String userId = "123456";
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "zhangsan");
        claims.put("role", "ADMIN");

        String token = jwtUtil.generateToken(userId, claims);
        System.out.println("生成的Token: " + token);

        // 2. 验证Token
        boolean isValid = jwtUtil.validateToken(token);
        System.out.println("Token是否有效: " + isValid);

        // 3. 解析Token
        try {
            String subject = jwtUtil.getSubject(token);
            System.out.println("用户ID: " + subject);

            // 获取所有声明
            Claims claimsObj = jwtUtil.parseToken(token);
            System.out.println("用户名: " + claimsObj.get("username"));
            System.out.println("角色: " + claimsObj.get("role"));

            // 4. 检查是否即将过期
            boolean expiringSoon = jwtUtil.isTokenExpiringSoon(token, 2);
            System.out.println("是否即将过期（2小时内）: " + expiringSoon);

            // 5. 刷新Token
            String newToken = jwtUtil.refreshToken(token);
            System.out.println("刷新后的Token: " + newToken);

        } catch (JwtException e) {
            System.err.println("Token处理失败: " + e.getMessage());
        }
    }


}
