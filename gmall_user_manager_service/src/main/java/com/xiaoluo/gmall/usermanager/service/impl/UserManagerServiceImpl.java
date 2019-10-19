package com.xiaoluo.gmall.usermanager.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.xiaoluo.gmall.bean.UserAddress;
import com.xiaoluo.gmall.bean.UserInfo;
import com.xiaoluo.gmall.service.UserManagerService;
import com.xiaoluo.gmall.service.util.RedisUtil;
import com.xiaoluo.gmall.usermanager.mapper.UserAddressMapper;
import com.xiaoluo.gmall.usermanager.mapper.UserInfoMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserManagerServiceImpl implements UserManagerService {
	public static final String USERINFO_PREFIX = "userInfo:";
	public static final String USERINFO_SUFFIX = ":info";
	public static final int USERINFO_TIMEOUT = 24 * 60 * 60;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private UserAddressMapper userAddressMapper;

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

	@Override
	public UserInfo login(UserInfo userInfo) {
		String passwd = org.springframework.util.DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
		userInfo.setPasswd(passwd);
		UserInfo userInfoExists = userInfoMapper.selectOne(userInfo);
		if(userInfoExists != null){
			//放入缓存
			Jedis jedis = redisUtil.getJedis();
			String userInfoKey = USERINFO_PREFIX + userInfoExists.getId() + USERINFO_SUFFIX;
			jedis.setex(userInfoKey,USERINFO_TIMEOUT, JSON.toJSONString(userInfoExists));

			jedis.close();
			return userInfoExists;
		}
		return null;
	}

	@Override
	public UserInfo verify(String id) {
		Jedis jedis = redisUtil.getJedis();
		String userInfoStr = jedis.get(USERINFO_PREFIX + id + USERINFO_SUFFIX);
		if(!StringUtils.isEmpty(userInfoStr)){
			jedis.expire(USERINFO_PREFIX + id + USERINFO_SUFFIX,USERINFO_TIMEOUT);
			UserInfo userInfo = JSON.parseObject(userInfoStr, UserInfo.class);
			return userInfo;

		}

		jedis.close();
		return null;
	}

	@Override
	public List<UserAddress> getUserAddress(String userId) {
		UserAddress userAddress = new UserAddress();
		userAddress.setUserId(Integer.parseInt(userId));


		return userAddressMapper.select(userAddress);
	}
}
