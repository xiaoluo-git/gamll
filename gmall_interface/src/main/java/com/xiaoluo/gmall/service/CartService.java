package com.xiaoluo.gmall.service;

import com.xiaoluo.gmall.bean.CartInfo;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

	CartInfo addCart(String userId,String skuId,Integer num);

	List<CartInfo> listCart(String userId);

	List<CartInfo> mergeCart(String userId, String user_temp_id);

	void checkCart(String userId, String skuId, String isChecked);

	BigDecimal checkCartTotalAmount(String userId);

	List<CartInfo> ListCartChecked(String userId);


}
