package com.xiaoluo.gmall.gmall_order_web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xiaoluo.gmall.bean.UserInfo;
import com.xiaoluo.gmall.service.UserManagerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class orderController {
	@Reference
	private UserManagerService userManagerService;

	@GetMapping("trade")
	public UserInfo trade(String userId){
		return userManagerService.getUserInfo(userId);
	}

}
