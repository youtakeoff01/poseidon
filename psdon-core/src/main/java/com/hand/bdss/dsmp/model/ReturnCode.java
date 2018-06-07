package com.hand.bdss.dsmp.model;

import java.util.HashMap;
import java.util.Map;

public class ReturnCode {

    public static Map<String, String> MESSAGES = new HashMap<>();

    /**
     * 操作成功
     */
    public static final String RETURN_CODE_SUCCESS = "000000";

    /**
     * 用户未登录
     */
    public static final String RETURN_CODE_USER_NOT_CHECKED = "000001";
    /**
     * 用户登陆失败
     */
    public static final String RETURN_CODE_USER_CHECKED_FAIL = "000002";

    /**
     * 没有账户信息
     */
    public static final String RETURN_CODE_ACCOUNT_NOT_FOUND = "000003";

    /**
     * 用户密码错
     */
    public static final String RETURN_CODE_USER_PASSWD_ERROR = "000004";

    /**
     * 账户停用
     */
    public static final String RETURN_CODE_USER_DISABLE = "000005";

    /**
     * 资源路径不存在
     */
    public static final String RETURN_CODE_SRCPATH_NOT_FOUND = "000006";

    /**
     * 目标文件已存在
     */
    public static final String RETURN_CODE_DOCUMENT_NOT_ONLY = "000007";
    
    /**
     * 上传的csv内容不满足要求
     */
    public static final String CSV_CONTENT_ERROR = "000008";
    /**
     * 上传的文件类型错误
     */
    public static final String UPLOAD_TYPE_ERROR = "000009";
    
    /**
     * 系统错误
     */
    public static final String RETURN_CODE_ERROR = "999999";

    /**
     * 参数为空异常
     */
    public static final String RETURN_CODE_PARAM_NULL = "999998";

    /**
     * token过期
     */
    public static final String RETURN_CODE_USR_TOKEN_EXPIRED = "999997";

    /**
     * 上传hdfs文件超出大小限制
     */
    public static final String RETURN_CODE_FILE_SIZE_EXEC = "999996";

    static {
        MESSAGES.put("", "没有返回码");
        MESSAGES.put(RETURN_CODE_ERROR, "系统错误");
        MESSAGES.put(RETURN_CODE_SUCCESS, "操作成功");
        MESSAGES.put(RETURN_CODE_USR_TOKEN_EXPIRED, "token过期");
        MESSAGES.put(RETURN_CODE_PARAM_NULL, "参数异常");
        MESSAGES.put(RETURN_CODE_FILE_SIZE_EXEC, "文件超出限制");
    }
}
