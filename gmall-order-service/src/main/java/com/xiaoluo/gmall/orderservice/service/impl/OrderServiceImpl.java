package com.xiaoluo.gmall.orderservice.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xiaoluo.gmall.bean.OrderDetail;
import com.xiaoluo.gmall.bean.OrderInfo;
import com.xiaoluo.gmall.enums.OrderStatus;
import com.xiaoluo.gmall.enums.ProcessStatus;
import com.xiaoluo.gmall.orderservice.mapper.OrderDetailMapper;
import com.xiaoluo.gmall.orderservice.mapper.OrderInfoMapper;
import com.xiaoluo.gmall.service.OrderService;
import com.xiaoluo.gmall.service.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Autowired
	private OrderDetailMapper orderDetailMapper;

	@Override
	public String generateTradeNo(String userId) {
		Jedis jedis = redisUtil.getJedis();
		String tradeNoKey = "trade:" + userId + ":token";
		String token = UUID.randomUUID().toString().replaceAll("-", "");
		jedis.setex(tradeNoKey,60*60*24,token);
		jedis.close();
		return token;
	}

	@Override
	public Boolean verifyTradeNo(String userId, String tradeNo) {
		Jedis jedis = redisUtil.getJedis();
		String tradeNoKey = "trade:" + userId + ":token";
		String token = jedis.get(tradeNoKey);
		String watch = jedis.watch(tradeNoKey);
		Transaction multi = jedis.multi();
		if(StringUtils.isEmpty(token)){
			return false;
		}
		if(tradeNo.equals(token)){
			multi.del(tradeNoKey);
		}
		List<Object> list = multi.exec();
		jedis.close();
		if(list!=null&&list.size()>0&&(Long)list.get(0)==1L){
			return true;
		}else{
			return false;
		}


	}

	@Override
	@Transactional
	public String saveOrder(OrderInfo orderInfo) {


		orderInfoMapper.insertSelective(orderInfo);

		List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
		for (OrderDetail orderDetail : orderDetailList) {
			orderDetail.setOrderId(orderInfo.getId());
			orderDetailMapper.insertSelective(orderDetail);
		}

		return orderInfo.getId();
	}

	@Override
	public OrderInfo getOrderInfoById(String orderId) {
		OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderId);

		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrderId(orderInfo.getId());
		List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetail);
		orderInfo.setOrderDetailList(orderDetailList);

		return orderInfo;
	}
}
