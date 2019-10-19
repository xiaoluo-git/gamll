package com.xiaoluo.gmall.managerservice.mapper;

import com.xiaoluo.gmall.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {
	List<Map> getSaleAttrValuesBySpu(String spuId);
}
