package com.xiaoluo.gmall.interceptor;

import com.alibaba.fastjson.JSON;
import com.xiaoluo.gmall.myannotation.RequireLogin;
import com.xiaoluo.gmall.util.CookieUtil;
import com.xiaoluo.gmall.util.HttpClientUtil;
import com.xiaoluo.gmall.webconst.WebConst;
import io.jsonwebtoken.impl.Base64UrlCodec;
import io.jsonwebtoken.impl.JwtMap;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		//请求路径中获取token
		String token = request.getParameter("newToken");

		if(!StringUtils.isEmpty(token)){
			CookieUtil.setCookie(request,response,"token",token, WebConst.COOKIE_MAXAGE,false);
		}else{
			//从cookie中获取
			token = CookieUtil.getCookieValue(request, "token", false);
		}

		if(token != null){
			//读取token
			Map map = getUserMapByToken(token);
			String nickName = (String) map.get("nickName");
			request.setAttribute("nickName", nickName);
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
		if(requireLogin != null && requireLogin.autoRedirect()){
			//需要认证
			if(StringUtils.isEmpty(token)){
				//重定向
				redirect(  request,   response);
				return false;
			}else{
				//验证token
				return verifyToken(request, response, token);
			}
		}

		if(requireLogin != null && !requireLogin.autoRedirect()){
			if(!StringUtils.isEmpty(token)){
				String currentIp = request.getHeader("X-forwarded-for");
				String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + currentIp);
				if("success".equals(result)){
					Map map = getUserMapByToken(token);
					String userId = (String) map.get("userId");
					request.setAttribute("userId",userId);
				}

			}
		}




		return true;
	}

	private boolean verifyToken(HttpServletRequest request, HttpServletResponse response, String token) throws IOException {
		String currentIp = request.getHeader("X-forwarded-for");
		String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + currentIp);
		if("fail".equals(result)){
			redirect(  request,   response);
			return false;
		}
		Map map = getUserMapByToken(token);
		String userId = (String) map.get("userId");
		request.setAttribute("userId",userId);
		return true;
	}

	private void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String  requestURL = request.getRequestURL().toString();//取得用户的当前登录请求
		System.out.println(requestURL);
		String encodeURL = URLEncoder.encode(requestURL, "UTF-8");
		response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);
	}

	private Map getUserMapByToken(String token) {
		String userBase64 = org.apache.commons.lang3.StringUtils.substringBetween(token, ".");
		Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
		String mapStr = new String(base64UrlCodec.decode(userBase64));
		return JSON.parseObject(mapStr);
	}
}
