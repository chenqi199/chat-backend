package com.taoyiyi.chat.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;


@Getter
@AllArgsConstructor
public enum RespCodeEnum {

    /* ---------系统类编码--------- */

    SUCCESS("1000", "成功"),

    FAILE("1001", "失败"),

    ERROR_TOKEN("1002", "token异常"),

    ERROR_PARAM("1003", "参数异常"),

    ERROR_CONVERT("1004", "转换异常"),

    ERROR_SIGN("1005", "签名异常"),

    ERROR_LIMIT("1006", "限流异常"),

    ERROR_SMS("1007", "短信异常"),

    ERROR_ACCESS("1008", "权限异常"),

    /* ---------配置信息业务类编码--------- */

    BIZ_USER_ERROR("2000", "用户信息异常"),

    BIZ_USER_VER_ERROR("2001", "用户认证异常"),
    BIZ_USER_ADDR_ERROR("2002", "用户位置信息异常"),
    BIZ_USER_REGISTER_ERROR("2004", "用户注册异常"),
    BIZ_USER_PASSWORD_ERROR("2005", "用户修码异常"),
    BIZ_USER_PASS_LOGIN_ERROR("2006", "用户修码异常"),

    BIZ_USER_DB_ERROR("2100", "用户数据库异常"),

    BIZ_USER_VERIFY_CODE_ERROR("2200", "用户验证码异常"),

    BIZ_CUSTOMER_ERROR("3000", "客服信息异常"),
    BIZ_APPLAY_ERROR("3010", "申请信息异常"),

    BIZ_SHOP_ERROR("4001", "门店定位信息异常"),
    BIZ_SHOP_AUTH_ERROR("4002", "门店审核信息异常"),
    BIZ_SHOP_AUTH_DUPLICATE_ERROR("4003", "门店审核记录有待审核记录,重复提交"),
    BIZ_PROJECT_AUTH_DUPLICATE_ERROR("4004", "商品审核记录有待审核记录,重复提交"),
    BIZ_SHOP_TECHNICIAN_DUPLICATE_ERROR("4005", "正在审核中,请勿重复提交"),

    BIZ_ORDER_ERROR("5000", "订单信息异常"),

    BIZ_GRAB_ORDER_ERROR("6000", "抢单异常"),
    BIZ_PAY_ERROR("7000", "支付异常"),
    BIZ_PAY_REFUND_ERROR("7500", "退款异常"),
    BIZ_WALLET_ERROR("8000", "钱包异常"),
    BIZ_SPLITTING_ACCOUNTS_ERROR("8100", "分账异常"),


    ;

    private String code;

    private String memo;

    public static RespCodeEnum instanceOf(String code) {

        for (RespCodeEnum item : RespCodeEnum.values()) {

            if (StringUtils.pathEquals(item.getCode(), code)) {
                return item;
            }

        }

        return null;

    }

}
