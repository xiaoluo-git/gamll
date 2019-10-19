package com.xiaoluo.gmall.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xiaoluo.gmall.bean.SkuInfo;
import com.xiaoluo.gmall.bean.SpuSaleAttr;
import com.xiaoluo.gmall.myannotation.RequireLogin;
import com.xiaoluo.gmall.service.ListService;
import com.xiaoluo.gmall.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class ItemController {
	@Reference
	private ManagerService managerService;

//	@Reference
//	private ListService listService;

	@GetMapping("{skuId}.html")
//	@RequireLogin
	public ModelAndView getSkuInfoPage(@PathVariable String skuId){
//		listService.incrHotScore(skuId);

		ModelAndView mv = new ModelAndView();
		SkuInfo skuInfo = managerService.getSkuInfoPage(skuId);

		List<SpuSaleAttr> spuSaleAttrList = managerService.getSpuSaleAttrListandChecked(skuInfo.getSpuId(),skuId);
		//String skuInfoJson = JSON.toJSONString(skuInfo,true);

		Map skuValueIdsMap = managerService.getSkuValueIdsMap(skuInfo.getSpuId());
		String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
		mv.setViewName("item");
		mv.addObject("skuInfo",skuInfo);
		mv.addObject("spuSaleAttrList",spuSaleAttrList);
		mv.addObject("valuesSkuJson",valuesSkuJson);
		return mv;
	}
}
