package com.xiaoluo.gmall.managerservice.mapper;

import com.xiaoluo.gmall.bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
	List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

	List<SpuSaleAttr> getSpuSaleAttrListandChecked(@Param("spuId") String spuId,@Param("skuId") String skuId);
}
