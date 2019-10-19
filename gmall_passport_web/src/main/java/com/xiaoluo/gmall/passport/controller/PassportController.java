package com.xiaoluo.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xiaoluo.gmall.bean.UserInfo;
import com.xiaoluo.gmall.service.UserManagerService;
import com.xiaoluo.gmall.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin
public class PassportController {
	public static final String KEY = "xiaoluo";

	@Reference
	private UserManagerService userManagerService;

	@GetMapping("index")
	public String index(HttpServletRequest request){
		String originUrl = request.getParameter("originUrl");
		request.setAttribute("originUrl",originUrl);
		return "index";
	}

	@PostMapping("login")
	@ResponseBody
	public String login(UserInfo userInfo, HttpServletRequest request){

		UserInfo userInfoExists = userManagerService.login(userInfo);
		if(userInfoExists != null){
			HashMap<String, Object> map = new HashMap<>();
			map.put("userId",userInfoExists.getId());
			map.put("nickName",userInfoExists.getNickName());
			String currentIp = request.getHeader("X-forwarded-for");
			return JwtUtil.encode(KEY,map,currentIp);

		}

		return "fail";
	}

	@GetMapping("verify")
	@ResponseBody
	public String verify(HttpServletRequest request){
		String token = request.getParameter("token");
		String currentIp = request.getParameter("currentIp");

		Map<String, Object> map = JwtUtil.decode(token, KEY, currentIp);
		if(map != null){
			String id = (String) map.get("userId");
			UserInfo userInfo = userManagerService.verify(id);

			if(userInfo != null){
				return "success";
			}
		}

		return "fail";
	}


}
