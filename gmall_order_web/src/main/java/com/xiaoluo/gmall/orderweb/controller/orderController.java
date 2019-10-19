package com.xiaoluo.gmall.orderweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sun.xml.internal.ws.util.CompletedFuture;
import com.xiaoluo.gmall.bean.*;
import com.xiaoluo.gmall.enums.OrderStatus;
import com.xiaoluo.gmall.enums.ProcessStatus;
import com.xiaoluo.gmall.myannotation.RequireLogin;
import com.xiaoluo.gmall.service.CartService;
import com.xiaoluo.gmall.service.ManagerService;
import com.xiaoluo.gmall.service.OrderService;
import com.xiaoluo.gmall.service.UserManagerService;
import com.xiaoluo.gmall.util.HttpClientUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Controller
public class orderController {

	@Reference
	private UserManagerService userManagerService;

	@Reference
	private CartService cartService;

	@Reference
	private OrderService orderService;

	@Reference
	private ManagerService managerService;

	@GetMapping("trade")
	@RequireLogin
	public String trade(HttpServletRequest request){
		String userId = (String) request.getAttribute("userId");

		//地址信息
		List<UserAddress> userAddressList = userManagerService.getUserAddress(userId);

		//商品列表，缓存中获取
		List<CartInfo> cartInfoList = cartService.ListCartChecked(userId);

		BigDecimal totalAmount = cartService.checkCartTotalAmount(userId);


		//生成tradeNo，防止重复下单
		String tradeNo = orderService.generateTradeNo(userId);

		request.setAttribute("userAddressList",userAddressList);
		request.setAttribute("cartInfoList",cartInfoList);
		request.setAttribute("totalAmount",totalAmount);
		request.setAttribute("tradeNo",tradeNo);
		return "trade";
	}

	@PostMapping("submitOrder")
	@RequireLogin
	public String submitOrder(OrderInfo orderInfo,HttpServletRequest request){
		//验证token
		String tradeNo = request.getParameter("tradeNo");
		String userId = (String) request.getAttribute("userId");
		Boolean verify = orderService.verifyTradeNo(userId,tradeNo);
		if(!verify){

			request.setAttribute("errMsg","页面已失效，请重新结算！");
			return "tradeFail";

		}
		UserInfo userInfo = userManagerService.getUserInfo(userId);
		orderInfo.setCreateTime(new Date());
		orderInfo.setExpireTime(DateUtils.addMinutes(orderInfo.getCreateTime(),15));
		orderInfo.setOrderStatus(OrderStatus.UNPAID);
		orderInfo.setProcessStatus(ProcessStatus.UNPAID);
		orderInfo.setConsigneeTel(userInfo.getPhoneNum());
		orderInfo.sumTotalAmount();
		orderInfo.setUserId(userId);

		List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
		for (OrderDetail orderDetail : orderDetailList) {
			SkuInfo skuInfoPage = managerService.getSkuInfoPage(orderDetail.getSkuId());

			orderDetail.setSkuName(skuInfoPage.getSkuName());
			orderDetail.setImgUrl(skuInfoPage.getSkuDefaultImg());

			if(!orderDetail.getOrderPrice().divide(new BigDecimal(String.valueOf(orderDetail.getSkuNum()))).equals(skuInfoPage.getPrice())){
				request.setAttribute("errMsg","商品价格已发送变动请重新下单！");
				return  "tradeFail";
			}
		}

		//验证库存
		List<OrderDetail> orderDetails = Collections.synchronizedList(new ArrayList<OrderDetail>());
		Stream<CompletableFuture<String>> completableFutureStream = orderDetailList.stream().map(orderDetail ->
				CompletableFuture.supplyAsync(() -> checkSkuNum(orderDetail)).whenComplete((result, ex) -> {
					if ("0".equals(result)) {
						orderDetails.add(orderDetail);
					}
				})
		);
		CompletableFuture[] completableFutures = completableFutureStream.toArray(CompletableFuture[]::new);
		CompletableFuture.allOf(completableFutures).join();
		StringBuffer stringBuffer = new StringBuffer();
		if(orderDetails.size() > 0){
			for (OrderDetail orderDetail : orderDetails) {
				stringBuffer.append("订单:" + orderDetail.getSkuName() + "库存不足！<br>");
			}
			request.setAttribute("errMsg",stringBuffer.toString());
			return  "tradeFail";
		}


		//保存订单
		String orderId = orderService.saveOrder(orderInfo);

		//删除购物车


		return "redirect://payment.gmall.com/index?orderId="+orderId;

	}

	private String checkSkuNum(OrderDetail orderDetail){
		String result = HttpClientUtil.doGet("http://www.ware.com/hasStock?skuId=" + orderDetail.getSkuId() + "&num=" + orderDetail.getSkuNum());
		return result;
	}

}
