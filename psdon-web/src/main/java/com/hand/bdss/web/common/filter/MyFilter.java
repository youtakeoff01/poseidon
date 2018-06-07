package com.hand.bdss.web.common.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.vo.BaseResponse;

/**
 * 拦截所有的用户请求，查看用户是否登录成功，如果没有登录，则重定向到登录界面
 * 
 * @author Administrator
 *
 */
@Component
public class MyFilter implements Filter {

	@Override
	public void destroy() {

	}

	/**
	 * 拦截所有的请求，处理用户的登录请求，和用户进入登录界面
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		StringBuffer url = httpRequest.getRequestURL();
		String str = handlerUrl(url.toString());
		// 无需拦截的url
		String[] excludeUrl = new String[] { "loginController/login", "" };

		// 如果是用户登录请求，则直接放过
		if (isContains(str, excludeUrl)) {
			chain.doFilter(request, response);
		} else {
			// 获取当前登录的用户信息
			UserEntity user = GetUserUtils.getUser(httpRequest);
			if (user.getId() == 0) {// 表明没有登录过，或者登录过期
				BaseResponse base = new BaseResponse();
				base.setReturnCode("2");
				base.setReturnMessage("登录超时。");
				PrintWriter out= response.getWriter(); //获取流
	            out.print(base.toString());
	            out.flush();
	            out.close();
			} else {
				chain.doFilter(request, response);
			}
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	public String handlerUrl(String url) {
		return url.substring(getCharacterPosition(url, "/", 4) + 1, url.length());
	}

	/**
	 * 获取字符串中第N次出现的字符位置
	 * 
	 * @param string
	 * @return
	 */
	public int getCharacterPosition(String string, String sign, int times) {
		// 这里是获取"/"符号的位置
		Matcher slashMatcher = Pattern.compile(sign).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			// 当"/"符号第四次出现的位置
			if (mIdx == times) {
				break;
			}
		}
		return slashMatcher.start();
	}

	public boolean isContains(String url, String[] excludeUrl) {
		if (excludeUrl != null && excludeUrl.length > 0) {
			for (String string : excludeUrl) {
				// 避开对静态资源访问的拦截
				if (!"".equals(url) && "resources/".equals(url.substring(0, getCharacterPosition(url, "/", 1) + 1))) {
					return true;
				}
				if (string.equals(url)) {
					return true;
				}
			}
		}
		return false;
	}

}
