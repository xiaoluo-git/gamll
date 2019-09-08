package com.xiaoluo.gmall.service;

import com.xiaoluo.gmall.bean.*;

import java.util.List;

public interface ManagerService {

	List<BaseCatalog1> ListAllBaseCatalog1();

	List<BaseCatalog2> ListBaseCatalog2(String catalog1Id);

	List<BaseCatalog3> ListBaseCatalog3(String catalog2Id);

	List<BaseAttrInfo> ListBaseAttrInfo(String catalog3Id);

	List<BaseAttrValue> ListBaseAttrValue(String attrId);

	void delAttrValueByAttrId(String attrId);

	void saveAttrValue(BaseAttrValue baseAttrValue);


	void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
