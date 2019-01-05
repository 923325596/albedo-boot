package com.albedo.java.common;

/**
 * Globals for Spring Security authorities.
 */
public final class AuthoritiesConstants {
    /**超级管理员*/
    public static final String ADMIN = "ROLE_ADMIN";
    /**UA操作员*/
    public static final String NORMAL = "ROLE_NORMAL";
    /**登录权限*/
    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";/**
     * 默认生成图形验证码宽度
     */
    public static final String DEFAULT_IMAGE_WIDTH = "100";

    /**
     * 默认生成图像验证码高度
     */
    public static final String DEFAULT_IMAGE_HEIGHT = "40";

    /**
     * 默认生成图形验证码长度
     */
    public static final String DEFAULT_IMAGE_LENGTH = "4";

    /**
     * 默认生成图形验证码过期时间
     */
    public static final int DEFAULT_IMAGE_EXPIRE = 60;
    /**
     * 边框颜色，合法值： r,g,b (and optional alpha) 或者 white,black,blue.
     */
    public static final String DEFAULT_COLOR_FONT = "black";

    /**
     * 图片边框
     */
    public static final String DEFAULT_IMAGE_BORDER = "no";
    /**
     * 默认图片间隔
     */
    public static final String DEFAULT_CHAR_SPACE = "5";

    /**
     * 默认保存code的前缀
     */
    public static final String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY";
    /**
     * 默认保存登录的前缀
     */
    public static final String DEFAULT_LOGIN_KEY = "DEFAULT_LOGIN_KEY";
    /**
     * 默认保存登录的前缀
     */
    public static final String DEFAULT_LOGIN_JWT_KEY = "DEFAULT_LOGIN_JWT_KEY";
    public static final String DEFAULT_LOGIN_JWT_MAP_KEY = "DEFAULT_LOGIN_JWT_MAP_KEY";
    /**
     * 默认保存24小时后登录的前缀
     */
    public static final String DEFAULT_LOGIN_AFTER_24_KEY = "DEFAULT_LOGIN_AFTER_24_KEY";
    /**
     * 验证码文字大小
     */
    public static final String DEFAULT_IMAGE_FONT_SIZE = "30";

    /**
     * token-uservo
     */
    public static final String TOKEN_USER_DETAIL = "token-user-detail";

    private AuthoritiesConstants() {
    }
}
