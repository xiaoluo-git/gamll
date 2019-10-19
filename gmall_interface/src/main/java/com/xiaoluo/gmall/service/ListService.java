package com.xiaoluo.gmall.service;

import com.xiaoluo.gmall.bean.SkuLsInfo;
import com.xiaoluo.gmall.bean.SkuLsParams;
import com.xiaoluo.gmall.bean.SkuLsResult;

public interface ListService {
	void saveSkuLsInfo(SkuLsInfo skuLsInfo);

	SkuLsResult searchSkuLsInfo(SkuLsParams skuLsParams);

	public void incrHotScore(String skuId);
}
