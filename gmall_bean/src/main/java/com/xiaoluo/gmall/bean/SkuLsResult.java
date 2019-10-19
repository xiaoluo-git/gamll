package com.xiaoluo.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SkuLsResult implements Serializable {

	List<SkuLsInfo> skuLsInfoList;

	long total;

	long totalPages;

	List<String> attrValueIdList;
}