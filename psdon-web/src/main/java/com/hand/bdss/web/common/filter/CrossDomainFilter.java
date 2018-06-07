package com.hand.bdss.web.common.filter;



import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * 处理请求跨域过滤器
 */
@Component
public class CrossDomainFilter implements Filter {

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		// 允许请求域
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
		// 只允许指定域
//		response.setHeader("Access-Control-Allow-Origin", "http://10.60.138.106:8080");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, Accept-Language");
		// 是否支持cookie跨域
		response.addHeader("Access-Control-Allow-Credentials", "true");
		chain.doFilter(req, res);
	}
}
