package com.xiaoluo.gmall.managerservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xiaoluo.gmall.bean.*;
import com.xiaoluo.gmall.managerservice.mapper.*;
import com.xiaoluo.gmall.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ManagerServiceImpl implements ManagerService {
	@Autowired
	private BaseCatalog1Mapper catalog1Mapper;

	@Autowired
	private BaseCatalog2Mapper catalog2Mapper;

	@Autowired
	private BaseCatalog3Mapper catalog3Mapper;

	@Autowired
	private BaseAttrInfoMapper attrInfoMapper;

	@Autowired
	private BaseAttrValueMapper attrValueMapper;

	@Override
	public List<BaseCatalog1> ListAllBaseCatalog1() {

		return catalog1Mapper.selectAll();
	}

	@Override
	public List<BaseCatalog2> ListBaseCatalog2(String catalog1Id) {
		BaseCatalog2 baseCatalog2 = new BaseCatalog2();
		baseCatalog2.setCatalog1Id(catalog1Id);
		return catalog2Mapper.select(baseCatalog2);
	}

	@Override
	public List<BaseCatalog3> ListBaseCatalog3(String catalog2Id) {
		BaseCatalog3 baseCatalog3 = new BaseCatalog3();
		baseCatalog3.setCatalog2Id(catalog2Id);
		return catalog3Mapper.select(baseCatalog3);
	}

	@Override
	public List<BaseAttrInfo> ListBaseAttrInfo(String catalog3Id) {
		BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
		baseAttrInfo.setCatalog3Id(catalog3Id);
		return attrInfoMapper.select(baseAttrInfo);
	}

	@Override
	public List<BaseAttrValue> ListBaseAttrValue(String attrId) {
		BaseAttrValue baseAttrValue = new BaseAttrValue();
		baseAttrValue.setAttrId(attrId);
		return attrValueMapper.select(baseAttrValue);
	}

	@Override
	public void delAttrValueByAttrId(String attrId) {
		BaseAttrValue baseAttrValue = new BaseAttrValue();
		baseAttrValue.setAttrId(attrId);
		attrValueMapper.delete(baseAttrValue);
	}

	@Override
	public void saveAttrValue(BaseAttrValue baseAttrValue) {
		attrValueMapper.insertSelective(baseAttrValue);
	}

	@Override
	public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
		String attrId = baseAttrInfo.getId();
		if(StringUtils.isEmpty(attrId)){
			//添加平台属性名
			baseAttrInfo.setId(null);
			attrInfoMapper.insertSelective(baseAttrInfo);
		}else{
			//更新平台属性名
			attrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);

			//删除旧的平台属性值
			BaseAttrValue baseAttrValue = new BaseAttrValue();
			baseAttrValue.setAttrId(attrId);
			attrValueMapper.delete(baseAttrValue);
		}

		//更新平台属性值
		List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
		if(attrValueList != null && attrValueList.size() >0){
			for (BaseAttrValue baseAttrValue : attrValueList) {
				baseAttrValue.setId(null);
				baseAttrValue.setAttrId(baseAttrInfo.getId());
				attrValueMapper.insertSelective(baseAttrValue);
			}
		}

	}
}
