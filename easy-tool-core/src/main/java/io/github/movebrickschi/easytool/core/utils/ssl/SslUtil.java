package io.github.movebrickschi.easytool.core.utils.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * SslUtil
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
@Slf4j
public final class SslUtil {
    private SslUtil() {
    }

    /**
     * 忽略SSL证书验证
     */
    public static void disableSSLCertificateValidation() {
        try {
            // 创建一个信任所有证书的TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 安装信任所有证书的SSLContext
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 创建所有主机名都有效的HostnameVerifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // 安装HostnameVerifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            log.warn("禁用SSL证书验证失败: {}", e.getMessage());
        }
    }

}
