package com.xiaoluo.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class SkuSaleAttrValue implements Serializable {

	@Id
	@Column
	String id;

	@Column
	String skuId;

	@Column
	String saleAttrId;

	@Column
	String saleAttrValueId;

	@Column
	String saleAttrName;

	@Column
	String saleAttrValueName;
}
