package io.github.liuchunchiuse.enums;

import io.github.liuchunchiuse.vo.DomainStatusVO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 域名Whois枚举类
 *
 * @author Liu Chunchi
 * @version 1.0
 */
public final class DomainWhoisEnum {


    /**
     * 域名状态
     */
    public enum DomainStatus {
        /**
         * 新注册的域名，可能存在以下状态。
         */
        OK(0, "ok", "正常状态", "表示域名可正常使用，没有需要立即进行的操作，也没有设置任何保护措施。"),
        ADD_PERIOD(0, "addPeriod", "注册局设置的域名新注册期", "域名新注册5天内会出现此状态，但不影响域名的正常使用，5天后自动解除该状态。"),
        /**
         * 出于对域名注册信息的保护，域名在设置某些安全锁后会出现以下状态。
         */
        CLIENT_DELETE_PROHIBITED(0, "clientDeleteProhibited", "注册商设置禁止删除", "表示限制域名的一种状态，域名不能被删除。"),
        SERVER_DELETE_PROHIBITED(0, "serverDeleteProhibited", "注册局设置禁止删除", "表示限制域名的一种状态，域名不能被删除。"),
        CLIENT_UPDATE_PROHIBITED(0, "clientUpdateProhibited", "注册商设置禁止更新", "包含注册人/管理联系人/技术联系人/付费联系人/DNS等域名信息不能被修改，但可以设置或修改解析记录。"),
        SERVER_UPDATE_PROHIBITED(0, "serverUpdateProhibited", "注册局设置禁止更新", "包含注册人/管理联系人/技术联系人/付费联系人/DNS等域名信息不能被修改，但可以设置或修改解析记录。"),
        CLIENT_TRANSFER_PROHIBITED(0, "clientTransferProhibited", "注册商设置禁止转移", "表示限制域名的一种状态，域名不能转移注册商。"),
        SERVER_TRANSFER_PROHIBITED(0, "serverTransferProhibited", "注册局设置禁止转移", "表示限制域名的一种状态，域名不能转移注册商。部分新注册的域名或域名转移注册商60天内会被注册局设置禁止转移，60天后会自动解除该状态；部分域名涉及仲裁或诉讼案被注册局设置禁止转移，仲裁或诉讼案结束会解除该状态。"),
        /**
         * 其他禁止解析、禁止续费的状态。
         */
        PENDING_VERIFICATION(1, "pendingVerification", "注册信息审核期", "名注册后未进行实名审核，您需在域名注册成功后5天内提交实名资料进行审核，如果5" +
                "天后仍未提交资料进行实名审核，域名会被serverHold（暂停解析）。"),
        CLIENT_HOLD(1, "clientHold", "注册商设置暂停解析", "处于clientHold状态的域名会被暂停解析，您需联系注册商解除该状态。"),
        SERVER_HOLD(1, "serverHold", "注册局设置暂停解析", "处于serverHold状态的域名会被解析暂停，“.cn”中英文域名注册成功后，如果未通过实名审核一般会出现该状态，您需在域名有效期内完成实名审核，审核通过后会解除该状态。"),
        INACTIVE(1, "inactive", "非激活状态", "注册域名时未填写域名DNS，导致域名注册成功后无法进行解析，您需在注册商处设置域名DNS。"),
        CLIENT_RENEW_PROHIBITED(1, "clientRenewProhibited", "注册商或注册局设置禁止续费", "处于该状态的域名无法进行域名续费，通常是域名处于仲裁或法院争议期，您需联系注册商确认原因。"),
        SERVER_RENEW_PROHIBITED(1, "serverRenewProhibited", "注册商或注册局设置禁止续费", "处于该状态的域名无法进行域名续费，通常是域名处于仲裁或法院争议期，您需联系注册商确认原因。"),
        PENDING_TRANSFER(1, "pendingTransfer", "注册局设置转移过程中", "表示域名正处于转移注册商的过程中。"),
        REDEMPTION_PERIOD(1, "redemptionPeriod", "注册局设置赎回期", "表示域名处于赎回期，您可联系注册商高价赎回域名。"),
        PENDING_DELETE(1, "pendingDelete", "注册局设置待删除/赎回期", "国内和国际域名处于该状态时，有不同的含义："
                + "国际域名：该状态表示域名已过赎回期等待被删除，删除后可对外重新开放注册。"
                + "国内域名：该状态表示域名处于赎回期，您可联系注册商高价赎回域名。"),
        NOT_AVAILABLE(1, "未知", "未知", "未知"),
        ;

        private final int code;
        private final String codeStr;
        private final String name;
        private final String desc;

        DomainStatus(int code, String codeStr, String name, String desc) {
            this.code = code;
            this.codeStr = codeStr;
            this.name = name;
            this.desc = desc;
        }

        public String getCodeStr() {
            return codeStr;
        }

        public String getDesc() {
            return desc;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }


        public static DomainStatus getOneByCode(String codeStr) {
            return Arrays.stream(DomainStatus.values()).filter(it -> codeStr.contains(it.getCodeStr())).findFirst().orElse(NOT_AVAILABLE);
        }

        public static List<DomainStatusVO> getListByCode(String codeStr) {
            return Arrays.stream(DomainStatus.values()).filter(it -> codeStr.contains(it.getCodeStr()))
                    .map(it -> DomainStatusVO.builder().code(it.getCode())
                            .codeStr(it.getCodeStr())
                            .name(it.getName())
                            .desc(it.getDesc())
                            .build()
                    ).collect(Collectors.toList());
        }
    }


}
