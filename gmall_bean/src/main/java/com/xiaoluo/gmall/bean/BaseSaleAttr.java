package com.xiaoluo.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class BaseSaleAttr implements Serializable {

	@Id
	@Column
	private String id;

	@Column
	private String name;
}
