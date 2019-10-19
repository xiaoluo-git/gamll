package com.xiaoluo.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.xiaoluo.gmall.bean.CartInfo;
import com.xiaoluo.gmall.bean.SkuInfo;
import com.xiaoluo.gmall.cart.mapper.CartInfoMapper;
import com.xiaoluo.gmall.service.CartService;
import com.xiaoluo.gmall.service.ManagerService;
import com.xiaoluo.gmall.service.util.RedisUtil;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.*;

import static org.apache.zookeeper.ZooDefs.OpCode.exists;

@Service
public class CartServiceImpl implements CartService {

	public static final String CART_KEY_PREFIXX = "cart:";

	public static final String CART_KEY_SUFFIX = ":info";

	@Autowired
	private CartInfoMapper cartInfoMapper;

	@Reference
	private ManagerService managerService;

	@Autowired
	private RedisUtil redisUtil;


	@Override
	public CartInfo addCart(String userId, String skuId, Integer num) {

		//更新数据库
		CartInfo cartInfo = new CartInfo();
		cartInfo.setUserId(userId);
		cartInfo.setSkuId(skuId);
		CartInfo cartInfoExists = cartInfoMapper.selectOne(cartInfo);
		SkuInfo skuInfoPage = managerService.getSkuInfoPage(skuId);
		if(cartInfoExists != null){
			//存在数据，进行更新
			cartInfoExists.setSkuNum(cartInfoExists.getSkuNum() + num);
			cartInfoExists.setSkuName(skuInfoPage.getSkuName());
			cartInfoExists.setImgUrl(skuInfoPage.getSkuDefaultImg());
			cartInfoExists.setSkuPrice(skuInfoPage.getPrice());
			cartInfoMapper.updateByPrimaryKeySelective(cartInfoExists);
		}else{
			//不存在数据，进行插入
			cartInfoExists = new CartInfo();
			cartInfoExists.setUserId(userId);
			cartInfoExists.setSkuId(skuId);
			cartInfoExists.setSkuPrice(skuInfoPage.getPrice());
			cartInfoExists.setImgUrl(skuInfoPage.getSkuDefaultImg());
			cartInfoExists.setCartPrice(skuInfoPage.getPrice());
			cartInfoExists.setSkuName(skuInfoPage.getSkuName());
			cartInfoExists.setSkuNum(num);
			cartInfoMapper.insert(cartInfoExists);

		}

		//放入缓存
		loadCache(userId);
		return cartInfoExists;
	}

	@Override
	public List<CartInfo> listCart(String userId) {
		//先查找缓存
		String cartKey = CART_KEY_PREFIXX + userId + CART_KEY_SUFFIX;
		Jedis jedis = redisUtil.getJedis();
		List<String> cartInfoListStr = jedis.hvals(cartKey);

		ArrayList<CartInfo> cartInfoList = new ArrayList<>();
		if(cartInfoListStr != null && cartInfoListStr.size() > 0){
			for (String cartInfoStr : cartInfoListStr) {
				CartInfo cartInfo = JSON.parseObject(cartInfoStr, CartInfo.class);
				cartInfoList.add(cartInfo);
			}
			cartInfoList.sort(new Comparator<CartInfo>() {
				@Override
				public int compare(CartInfo o1, CartInfo o2) {
					return o2.getId().compareTo(o1.getId());
				}
			});

			return cartInfoList;
		}

		//查询数据库,放缓存

		return loadCache(userId);
	}

	private List<CartInfo> loadCache(String userId) {
		//查询数据库，并带上最新的值
		List<CartInfo> cartInfoList = cartInfoMapper.listCartWithLatestPrice(userId);

		if(cartInfoList != null && cartInfoList.size() > 0){
			HashMap<String, String> map = new HashMap<>();
			for (CartInfo cartInfo : cartInfoList) {
				map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
			}
			String cartKey = CART_KEY_PREFIXX + userId + CART_KEY_SUFFIX;
			Jedis jedis = redisUtil.getJedis();
			jedis.del(cartKey);
			jedis.hmset(cartKey,map);
			jedis.expire(cartKey,60*60*24*7);
			jedis.close();
		}

		return  cartInfoList;
	}


	@Override
	public List<CartInfo> mergeCart(String userId, String user_temp_id) {
		//合并购物车
		 cartInfoMapper.mergeCart(userId,user_temp_id);

		//删除零时购物车
		CartInfo cartInfo = new CartInfo();
		cartInfo.setUserId(user_temp_id);
		cartInfoMapper.delete(cartInfo);


		//加载缓存
		return loadCache(userId);
	}

	private void loadCacheExperid(String userId){
		String cartKey = CART_KEY_PREFIXX + userId + CART_KEY_SUFFIX;
		Jedis jedis = redisUtil.getJedis();
		Long ttl = jedis.ttl(cartKey);
		Boolean exists = jedis.exists(cartKey);
		jedis.expire(cartKey,ttl.intValue() + 10);
		if(!exists){
			loadCache(userId);
		}
		jedis.close();
	}

	@Override
	public void checkCart(String userId, String skuId, String isChecked) {

		loadCacheExperid(userId);
		String cartKey = CART_KEY_PREFIXX + userId + CART_KEY_SUFFIX;
		Jedis jedis = redisUtil.getJedis();
		String cartInfoJson = jedis.hget(cartKey, skuId);
		CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
		cartInfo.setIsChecked(isChecked);
		jedis.hset(cartKey,skuId,JSON.toJSONString(cartInfo));

		//维护一个选择中的购物车
		String checkedCartkey = "cart:" + userId + ":checked";
		if(isChecked.equals("1")){
			//添加列表
			jedis.hset(checkedCartkey,skuId,cartInfoJson);
			jedis.expire(checkedCartkey,60 * 60);
		}else{
			//删除列表
			jedis.hdel(checkedCartkey,skuId);

		}
		jedis.close();
	}

	@Override
	public BigDecimal checkCartTotalAmount(String userId) {
		String checkedCartkey = "cart:" + userId + ":checked";
		Jedis jedis = redisUtil.getJedis();
		List<String> cartInfoListJson = jedis.hvals(checkedCartkey);
		BigDecimal totalAmount = new BigDecimal("0");
		for (String cartInfoJson : cartInfoListJson) {
			CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
			Integer skuNum = cartInfo.getSkuNum();
			BigDecimal skuPrice = cartInfo.getSkuPrice();
			BigDecimal multiply = skuPrice.multiply(new BigDecimal(String.valueOf(skuNum)));
			totalAmount = totalAmount.add(multiply);

		}

		jedis.close();
		return totalAmount;
	}

	@Override
	public List<CartInfo> ListCartChecked(String userId) {
		String checkedCartkey = "cart:" + userId + ":checked";
		Jedis jedis = redisUtil.getJedis();
		List<String> cartInfoListJson = jedis.hvals(checkedCartkey);
		ArrayList<CartInfo> cartInfoArrayList = new ArrayList<>();
		for (String cartInfoJson : cartInfoListJson) {
			cartInfoArrayList.add(JSON.parseObject(cartInfoJson,CartInfo.class));
		}

		jedis.close();
		return cartInfoArrayList;
	}


}
