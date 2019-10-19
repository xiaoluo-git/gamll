package com.xiaoluo.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xiaoluo.gmall.bean.SkuInfo;
import com.xiaoluo.gmall.service.ManagerService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SkuController {
	@Reference
	private ManagerService managerService;



	@PostMapping("saveSkuInfo")
	public String saveSkuInfo(@RequestBody SkuInfo skuInfo){
		managerService.saveSkuInfo(skuInfo);
		return "success";
	}
}
