package com.xiaoluo.gmall.service;

import com.xiaoluo.gmall.bean.OrderInfo;

public interface OrderService {

	String generateTradeNo(String userId);

	Boolean verifyTradeNo(String userId, String tradeNo);

	String saveOrder(OrderInfo orderInfo);

	OrderInfo getOrderInfoById(String orderId);
}
