package com.xiaoluo.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuLsInfo implements Serializable {

	private String id;

	private BigDecimal price;

	private String skuName;

	private String catalog3Id;

	private String skuDefaultImg;

	private Long hotScore=0L;

	private List<SkuLsAttrValue> skuAttrValueList;
}
