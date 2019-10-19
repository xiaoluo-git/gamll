package com.xiaoluo.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xiaoluo.gmall.bean.*;
import com.xiaoluo.gmall.service.ListService;
import com.xiaoluo.gmall.service.ManagerService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ManagerController {
	@Reference
	private ManagerService managerService;

	@Reference
	private ListService listService;

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

	@GetMapping("spuList")
	public List<SpuInfo> getSpuList(String catalog3Id){
		return managerService.listSpuInfo(catalog3Id);

	}

	@PostMapping("saveSpuInfo")
	public String saveSpuInfo(@RequestBody SpuInfo spuInfo){
		managerService.saveSpuInfo(spuInfo);
		return "success";
	}

	@PostMapping("baseSaleAttrList")
	public List<BaseSaleAttr> ListBaseSaleAttr(){
		return managerService.ListBaseSaleAttr();
	}

	@GetMapping("spuImageList")
	public List<SpuImage> getSpuImageList(String spuId){
		return managerService.listSpuImageList(spuId);
	}

	@GetMapping("spuSaleAttrList")
	public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){
		return managerService.getSpuSaleAttrList(spuId);
	}

	@GetMapping("onSale")
	public void onSale(String skuId){
		SkuLsInfo skuLsInfo = new SkuLsInfo();
		SkuInfo skuInfoPage = managerService.getSkuInfoPage(skuId);
		BeanUtils.copyProperties(skuInfoPage,skuLsInfo);
		listService.saveSkuLsInfo(skuLsInfo);

	}


}
