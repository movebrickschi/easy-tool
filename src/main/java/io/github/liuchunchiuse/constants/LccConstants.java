package io.github.liuchunchiuse.constants;

public final class LccConstants {


    /**
     * 成功
     */
    public static final Integer SUCCESS = 0;

    /**
     * 失败
     */
    public static final Integer FAIL = 1;

    public enum SuccessEnum {
        SUCCESS_ZERO(0),
        SUCCESS_2_HUNDRED(200);
        private final int code;

        SuccessEnum(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }


}
