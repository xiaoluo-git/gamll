package com.xiaoluo.gmall.service;


import com.xiaoluo.gmall.bean.UserAddress;
import com.xiaoluo.gmall.bean.UserInfo;

import java.util.List;

public interface UserManagerService {


	UserInfo getUserInfo (String id);

	List<UserInfo> getUserInfoListAll();

	void addUser(UserInfo userInfo);

	void updateUser(UserInfo userInfo);

	void updateUserByName(String name,UserInfo userInfo);

	void delUser(String id);

	UserInfo login(UserInfo userInfo);

	UserInfo verify(String id);

	List<UserAddress> getUserAddress(String userId);
}
