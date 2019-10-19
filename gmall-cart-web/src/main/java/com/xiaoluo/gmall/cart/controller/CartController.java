package com.xiaoluo.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xiaoluo.gmall.bean.CartInfo;
import com.xiaoluo.gmall.myannotation.RequireLogin;
import com.xiaoluo.gmall.service.CartService;
import com.xiaoluo.gmall.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
public class CartController {

	@Reference
	private CartService cartService;

	@PostMapping("addToCart")
	@RequireLogin(autoRedirect = false)
	public String addCart(String skuId, Integer num, HttpServletRequest request, HttpServletResponse response){
		String userId = request.getParameter("userId");

		if(StringUtils.isEmpty(userId)){
			userId = CookieUtil.getCookieValue(request, "user_temp_id", false);
			if(StringUtils.isEmpty(userId)){
				userId = UUID.randomUUID().toString().replaceAll("-", "");
				CookieUtil.setCookie(request,response,"user_temp_id",userId,60 * 60 * 24 * 7,false);
			}
		}

		CartInfo cartInfo = cartService.addCart(userId, skuId, num);

		request.setAttribute("cartInfo",cartInfo);
		request.setAttribute("num",num);

		return "success";
	}

	@GetMapping("cartList")
	@RequireLogin(autoRedirect = false)
	public String  listCart (HttpServletRequest request){
		String userId = (String) request.getAttribute("userId");
		List<CartInfo> cartList=null;
		if(!StringUtils.isEmpty(userId)){
			cartList = cartService.listCart(userId);
		}

		String user_temp_id = CookieUtil.getCookieValue(request, "user_temp_id", false);
		List<CartInfo> cartTempList=null;
		if(!StringUtils.isEmpty("user_temp_id") ){
			cartTempList = cartService.listCart(user_temp_id);
			if(StringUtils.isEmpty(userId)){
				cartList = cartTempList;
			}

		}

		if(!StringUtils.isEmpty(userId) && cartTempList != null && cartTempList.size() > 0){
			cartList = cartService.mergeCart(userId,user_temp_id);
		}

		BigDecimal totalAmount = cartService.checkCartTotalAmount(userId);

		request.setAttribute("cartList",cartList);
		request.setAttribute("totalAmount",totalAmount);
		return  "cartList";

	}

	@PostMapping("checkCart")
	@RequireLogin(autoRedirect = false)
	@ResponseBody
	public String checkCart(@RequestParam String isChecked,@RequestParam String skuId,HttpServletRequest request){
		String userId = (String) request.getAttribute("userId");

		if(StringUtils.isEmpty(userId)){
			userId = CookieUtil.getCookieValue(request,"user_temp_id",false);
		}

		cartService.checkCart(userId,skuId,isChecked);

		return "success";
	}
}
