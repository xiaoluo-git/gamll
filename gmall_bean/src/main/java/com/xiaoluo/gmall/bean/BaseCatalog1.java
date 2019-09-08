package com.xiaoluo.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class BaseCatalog1 implements Serializable {

	private static final long serialVersionUID = 684974237710L;

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	@Column
	private String name;
}
