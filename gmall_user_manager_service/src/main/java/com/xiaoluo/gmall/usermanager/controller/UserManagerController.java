package com.xiaoluo.gmall.usermanager.controller;


import com.xiaoluo.gmall.bean.UserInfo;
import com.xiaoluo.gmall.service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserManagerController {
	@Autowired
	private UserManagerService userManagerService;

	@PostMapping("addUser")
	public String addUserInfo(UserInfo userInfo){
		userManagerService.addUser(userInfo);
		return "success";
	}

	@GetMapping("listAllUserInfo")
	public List<UserInfo> listAllUserInfo(){
		return userManagerService.getUserInfoListAll();

	}

	@GetMapping("getUserInfoById")
	public UserInfo getUserInfo(String id){
		return userManagerService.getUserInfo(id);
	}

	@DeleteMapping("deleteUserInfo")
	public String deleteUserInfo(String id){
		userManagerService.delUser(id);
		return "success";
	}


	@PutMapping("updateUserInfoById")
	public String updateUserInfoById(UserInfo userInfo){
		userManagerService.updateUser(userInfo);
		return "success";
	}

	@PutMapping("updateUserInfoByName")
	public String updateUserInfoByName(UserInfo userInfo){
		userManagerService.updateUserByName(userInfo.getName(),userInfo);
		return "success";
	}

}
