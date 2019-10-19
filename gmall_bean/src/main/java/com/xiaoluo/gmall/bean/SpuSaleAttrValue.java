package com.xiaoluo.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

@Data
public class SpuSaleAttrValue implements Serializable {

	@Id
	@Column
	private String id;

	@Column
	private String spuId;

	@Column
	private String saleAttrId;

	@Column
	private String saleAttrValueName;

	@Transient
	private String isChecked;
}
