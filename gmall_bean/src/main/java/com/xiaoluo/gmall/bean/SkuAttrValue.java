package com.xiaoluo.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class SkuAttrValue implements Serializable {

	@Id
	@Column
	String id;

	@Column
	String attrId;

	@Column
	String valueId;

	@Column
	String skuId;
}
