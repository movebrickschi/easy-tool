package io.github.movebrickschi.easytool.core.utils.ding;

import cn.hutool.core.collection.CollUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 钉钉消息发送
 *
 * @author MoveBricks Chi
 * @since 1.0
 */
public class DingDingMessageProvider {

    private static final Logger logger = LoggerFactory.getLogger(DingDingMessageProvider.class);

    /**
     * 机器人应用的 access_token 的值
     */
    public final String customRobotToken;

    /**
     * 钉钉用户手机号
     */
    public final List<String> mobiles;

    /**
     * 安全设置，加签密钥
     */
    public final String secret;

    public DingDingMessageProvider(String customRobotToken, String secret, List<String> mobiles) {
        this.customRobotToken = customRobotToken;
        this.secret = secret;
        this.mobiles = mobiles;
    }

    /**
     * 自定义机器人发送群聊文本消息
     * 不设置指定mobile,则是@所有人，设置手机则@指定手机号的用户
     * @param content 消息内容
     */
    public void sendDingTalkMsg(String content) {
        try {
            Long timestamp = System.currentTimeMillis();
            logger.info("timestamp: {}", timestamp);
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8.name());
            logger.info("sign: {}", sign);
            //sign字段和timestamp字段必须拼接到请求URL上，否则会出现 310000 的错误信息
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?sign=" + sign +
                    "&timestamp=" + timestamp);
            OapiRobotSendRequest req = new OapiRobotSendRequest();
            //定义文本内容
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(content);
            //定义 @ 对象
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            if (CollUtil.isEmpty(mobiles)) {
                at.setIsAtAll(true);
            } else {
                at.setIsAtAll(false);
                at.setAtMobiles(mobiles);
            }
            //设置消息类型
            req.setMsgtype("text");
            req.setText(text);
            req.setAt(at);
            OapiRobotSendResponse rsp = client.execute(req, this.customRobotToken);
            logger.info("钉钉发送消息结果：{}", rsp.getBody());
        } catch (ApiException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String customRobotToken;

        private List<String> mobiles;

        private String secret;


        public Builder customRobotToken(String customRobotToken) {
            this.customRobotToken = customRobotToken;
            return this;
        }

        public Builder mobiles(List<String> mobiles) {
            this.mobiles = mobiles;
            return this;
        }

        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public DingDingMessageProvider build() {
            return new DingDingMessageProvider(customRobotToken, secret, mobiles);
        }

    }

}
