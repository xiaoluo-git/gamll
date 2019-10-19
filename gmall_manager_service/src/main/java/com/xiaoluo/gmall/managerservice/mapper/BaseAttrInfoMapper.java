package com.xiaoluo.gmall.managerservice.mapper;

import com.xiaoluo.gmall.bean.BaseAttrInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
	List<BaseAttrInfo> ListBaseAttrInfo(String catalog3Id);


	List<BaseAttrInfo> ListBaseAttr(String valueIds);
}
