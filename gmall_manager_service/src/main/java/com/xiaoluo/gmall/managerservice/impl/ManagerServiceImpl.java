package com.xiaoluo.gmall.managerservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.xiaoluo.gmall.bean.*;
import com.xiaoluo.gmall.managerservice.mapper.*;
import com.xiaoluo.gmall.service.ManagerService;
import com.xiaoluo.gmall.service.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ManagerServiceImpl implements ManagerService {

	public static final String SKUKEY_PREFIX="sku:";

	public static final String SKUKEY_SUFFIX=":info";

	public static final String SKUKEY_LOCK_SUFFIX=":lock";

	public static final int SKUKEY_TIMEOUT=24*60*60;

	@Autowired
	private RedisUtil redisUtil;

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

	@Autowired
	private SpuInfoMapper spuInfoMapper;

	@Autowired
	private  BaseSaleAttrMapper baseSaleAttrMapper;

	@Autowired
	private SpuImageMapper spuImageMapper;

	@Autowired
	private SpuSaleAttrMapper spuSaleAttrMapper;

	@Autowired
	private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

	@Autowired
	private SkuInfoMapper skuInfoMapper;

	@Autowired
	private SkuImageMapper skuImageMapper;

	@Autowired
	private SkuAttrValueMapper skuAttrValueMapper;

	@Autowired
	private SkuSaleAttrValueMapper skuSaleAttrValueMapper;



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
//		BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
//		baseAttrInfo.setCatalog3Id(catalog3Id);
//		return attrInfoMapper.select(baseAttrInfo);

		return attrInfoMapper.ListBaseAttrInfo(catalog3Id);
	}

	@Override
	public List<BaseAttrInfo> ListBaseAttr(List<String> attrValueIdList) {
		String valueIds = org.apache.commons.lang3.StringUtils.join(attrValueIdList.toArray(), ",");
		return attrInfoMapper.ListBaseAttr(valueIds);
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

	@Override
	public List<SpuInfo> listSpuInfo(String catalog3Id) {
		SpuInfo spuInfo = new SpuInfo();
		spuInfo.setCatalog3Id(catalog3Id);
		List<SpuInfo> spuInfoList = spuInfoMapper.select(spuInfo);
		return spuInfoList;
	}

	@Override
	public List<BaseSaleAttr> ListBaseSaleAttr() {
		return baseSaleAttrMapper.selectAll();
	}

	@Override
	@Transactional
	public void saveSpuInfo(SpuInfo spuInfo) {
		//保存spu基本信息
		spuInfoMapper.insertSelective(spuInfo);

		//保存图片信息
		List<SpuImage> spuImageList = spuInfo.getSpuImageList();
		for (SpuImage spuImage : spuImageList) {
			spuImage.setSpuId(spuInfo.getId());
			spuImageMapper.insertSelective(spuImage);
		}


		//保存商品属性
		List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
		for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
			spuSaleAttr.setSpuId(spuInfo.getId());
			spuSaleAttrMapper.insertSelective(spuSaleAttr);

			//保存商品属性值
			List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
			for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
				spuSaleAttrValue.setSpuId(spuInfo.getId());
				spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
			}
		}

	}

	@Override
	public List<SpuImage> listSpuImageList(String spuId) {
		SpuImage spuImage = new SpuImage();
		spuImage.setSpuId(spuId);
		return spuImageMapper.select(spuImage);
	}

	@Override
	public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {

		return spuSaleAttrMapper.getSpuSaleAttrList(spuId);
	}

	@Override
	public void saveSkuInfo(SkuInfo skuInfo) {
		//保存sku
		if(skuInfo == null){
			throw new RuntimeException("参数不能为空");
		}

		String skuId = skuInfo.getId();
		if(StringUtils.isEmpty(skuId)){
			//新增
			skuInfoMapper.insertSelective(skuInfo);
		}else{
			//更新
			skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
			//清空图片
			SkuImage skuImage = new SkuImage();
			skuImage.setSkuId(skuId);
			List<SkuImage> skuImageList4Del = skuImageMapper.select(skuImage);
			if(skuImageList4Del != null && skuImageList4Del.size()>0){
				skuImageMapper.delete(skuImage);
			}

			//清空平台属性值
			SkuAttrValue skuAttrValue = new SkuAttrValue();
			skuAttrValue.setSkuId(skuId);
			List<SkuAttrValue> skuAttrValueList4Del = skuAttrValueMapper.select(skuAttrValue);
			if(skuAttrValueList4Del != null && skuAttrValueList4Del.size() >0){
				skuAttrValueMapper.delete(skuAttrValue);
			}

			//清空销售属性值
			SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
			skuSaleAttrValue.setSkuId(skuId);
			List<SkuSaleAttrValue> skuSaleAttrValueList4Del = skuSaleAttrValueMapper.select(skuSaleAttrValue);
			if(skuSaleAttrValueList4Del != null && skuSaleAttrValueList4Del.size() > 0){
				skuSaleAttrValueMapper.delete(skuSaleAttrValue);
			}

		}
		if(skuInfo.getSkuImageList() != null && skuInfo.getSkuImageList().size()>0){
			for (SkuImage skuImage : skuInfo.getSkuImageList()) {
				skuImage.setSkuId(skuInfo.getId());
				skuImageMapper.insertSelective(skuImage);
			}
		}

		if(skuInfo.getSkuAttrValueList() != null && skuInfo.getSkuAttrValueList().size()>0){
			for (SkuAttrValue skuAttrValue : skuInfo.getSkuAttrValueList()) {
				skuAttrValue.setSkuId(skuInfo.getId());
				skuAttrValueMapper.insertSelective(skuAttrValue);

			}
		}

		if(skuInfo.getSkuSaleAttrValueList() != null && skuInfo.getSkuSaleAttrValueList().size() >0){
			for (SkuSaleAttrValue skuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
				skuSaleAttrValue.setSkuId(skuInfo.getId());
				skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
			}
		}


	}


	public SkuInfo getSkuInfoDB(String skuId) {

		SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);

		if(skuInfo == null){
			return null;
		}

		SkuAttrValue skuAttrValue = new SkuAttrValue();
		skuAttrValue.setSkuId(skuId);
		List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);
		skuInfo.setSkuAttrValueList(skuAttrValues);

		SkuImage skuImage = new SkuImage();
		skuImage.setSkuId(skuId);
		List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
		skuInfo.setSkuImageList(skuImageList);
		return skuInfo;
	}


	@Override
	public SkuInfo getSkuInfoPage(String skuId) {
		//从缓存中获取
		Jedis jedis = redisUtil.getJedis();
		String skuInfoKey = SKUKEY_PREFIX + skuId + SKUKEY_SUFFIX;
		String skuInfoStr = jedis.get(skuInfoKey);
		SkuInfo skuInfo = null;

		if(!StringUtils.isEmpty(skuInfoStr)){
			if(!"EMPTY".equals(skuInfoStr)){

				skuInfo = JSON.parseObject(skuInfoStr, SkuInfo.class);
			}

		}else{
			//解决穿透，利用Redis自带的分布锁Set
			String lockKey = SKUKEY_PREFIX + skuId + SKUKEY_LOCK_SUFFIX;
			String lockValue = UUID.randomUUID().toString().replaceAll("-", "");
			String result = jedis.set(lockKey, lockValue, "NX", "EX", 10);
			if("OK".equals(result)){
				//得到锁
				//缓存不存在从数据库中查询并放入缓存
				skuInfo = getSkuInfoDB(skuId);
				String skuInfoResult = null;
				if(skuInfo != null){
					jedis.set(skuInfoKey,JSON.toJSONString(skuInfo));
				}else{
					skuInfoResult = "EMPTY";
					jedis.setex(skuInfoKey,SKUKEY_TIMEOUT,skuInfoResult);
				}

				//释放锁
				if(jedis.exists(lockKey)&& lockValue.equals(jedis.get(lockKey))){
					//不完美，判断的时候没过期，再删除之前过期了，使用lua脚本优化，合并成一步
					jedis.del(lockKey);
				}

			}else{
				//未得到锁
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return this.getSkuInfoPage(skuId);
			}


		}

		jedis.close();
		return skuInfo;
	}



	@Override
	public List<SpuSaleAttr> getSpuSaleAttrListandChecked(String spuId, String skuId) {
		return spuSaleAttrMapper.getSpuSaleAttrListandChecked(spuId,skuId);
	}

	@Override
	public Map getSkuValueIdsMap(String spuId) {

		List<Map> list = skuSaleAttrValueMapper.getSaleAttrValuesBySpu(spuId);
		HashMap hashMap = new HashMap();
		for (Map map : list) {
			String skuId =(Long) map.get("sku_id") +"";
			String valueIds =(String ) map.get("value_ids");
			hashMap.put(valueIds,skuId);
		}

		return hashMap;
	}
}
