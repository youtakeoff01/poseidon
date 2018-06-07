package com.hand.bdss.web.common.util;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.entity.UserEntity;

/**
 * 获取登录的用户信息
 *
 * @author Administrator
 */
public class GetUserUtils {

    public static UserEntity getUser(HttpServletRequest request) {
        UserEntity user = (UserEntity) request.getSession().getAttribute("userMsg");
        if (user == null) {
            user = new UserEntity();
        }
        return user;
    }

    /**
     * 校验登录用户是否是root角色
     *
     * @param request
     * @return
     */
    public static boolean isRootUser(HttpServletRequest request) {
        UserEntity user = getUser(request);
        if (user.getRoleId() == 1) {
            return true;
        }
        return false;
    }
}
