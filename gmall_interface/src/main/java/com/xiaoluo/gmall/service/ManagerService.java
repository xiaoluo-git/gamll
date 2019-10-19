package com.xiaoluo.gmall.service;

import com.xiaoluo.gmall.bean.*;

import java.util.List;
import java.util.Map;

public interface ManagerService {

	List<BaseCatalog1> ListAllBaseCatalog1();

	List<BaseCatalog2> ListBaseCatalog2(String catalog1Id);

	List<BaseCatalog3> ListBaseCatalog3(String catalog2Id);

	List<BaseAttrInfo> ListBaseAttrInfo(String catalog3Id);

	List<BaseAttrInfo> ListBaseAttr(List<String> attrValueIdList);

	List<BaseAttrValue> ListBaseAttrValue(String attrId);

	void delAttrValueByAttrId(String attrId);

	void saveAttrValue(BaseAttrValue baseAttrValue);


	void saveAttrInfo(BaseAttrInfo baseAttrInfo);

	List<SpuInfo> listSpuInfo(String catalog3Id);

	List<BaseSaleAttr> ListBaseSaleAttr();

	void saveSpuInfo(SpuInfo spuInfo);

	List<SpuImage> listSpuImageList(String spuId);

	List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

	void saveSkuInfo(SkuInfo skuInfo);

	SkuInfo getSkuInfoPage(String skuId);


	List<SpuSaleAttr> getSpuSaleAttrListandChecked(String spuId, String skuId);


	Map getSkuValueIdsMap(String spuId);


}
