package com.xiaoluo.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xiaoluo.gmall.bean.*;
import com.xiaoluo.gmall.service.ManagerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ManagerController {
	@Reference
	private ManagerService managerService;

	@GetMapping("getCatalog1")
	public List<BaseCatalog1> getCatalog1(){

		return managerService.ListAllBaseCatalog1();
	}

	@GetMapping("getCatalog2")
	public List<BaseCatalog2> getCatalog2(String catalog1Id){
		return managerService.ListBaseCatalog2(catalog1Id);
	}

	@GetMapping("getCatalog3")
	public List<BaseCatalog3> getCatalog3(String catalog2Id){
		return managerService.ListBaseCatalog3(catalog2Id);
	}

	@GetMapping("attrInfoList")
	public List<BaseAttrInfo> attrInfoList(String catalog3Id){
		return managerService.ListBaseAttrInfo(catalog3Id);
	}

	@GetMapping("getAttrValueList")
	public List<BaseAttrValue> getAttrValueList(String attrId){
		return managerService.ListBaseAttrValue(attrId);
	}

	@PostMapping("saveAttrInfo")
	public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
		managerService.saveAttrInfo(baseAttrInfo);
	}



}
