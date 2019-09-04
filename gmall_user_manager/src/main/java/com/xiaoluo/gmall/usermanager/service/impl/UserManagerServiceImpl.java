package com.xiaoluo.gmall.usermanager.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.xiaoluo.gmall.bean.UserInfo;
import com.xiaoluo.gmall.service.UserManagerService;
import com.xiaoluo.gmall.usermanager.mapper.UserInfoMapper;

import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserManagerServiceImpl implements UserManagerService {
	@Autowired
	private UserInfoMapper userInfoMapper;

	@Override
	public UserInfo getUserInfo(String id) {
		return userInfoMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<UserInfo> getUserInfoListAll() {
		return userInfoMapper.selectAll();
	}

	@Override
	public void addUser(UserInfo userInfo) {
		userInfoMapper.insert(userInfo);
	}

	@Override
	public void updateUser(UserInfo userInfo) {
		userInfoMapper.updateByPrimaryKeySelective(userInfo);
	}

	@Override
	public void updateUserByName(String name, UserInfo userInfo) {
		Example example = new Example(UserInfo.class);
		example.createCriteria().andEqualTo("name",name);
		userInfoMapper.updateByExampleSelective(userInfo,example);
	}

	@Override
	public void delUser(String id) {
		userInfoMapper.deleteByPrimaryKey(id);
	}
}
