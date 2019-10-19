package com.xiaoluo.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartInfo implements Serializable {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column
	String id;
	@Column
	String userId;
	@Column
	String skuId;
	@Column
	BigDecimal cartPrice;
	@Column
	Integer skuNum;
	@Column
	String imgUrl;
	@Column
	String skuName;

	// 实时价格
	@Transient
	BigDecimal skuPrice;
	// 下订单的时候，商品是否勾选
	@Transient
	String isChecked="0";
}
