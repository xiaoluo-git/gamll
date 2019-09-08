package com.xiaoluo.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class BaseCatalog3 implements Serializable {

	private static final long serialVersionUID = 68253224710L;

	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	@Column
	private String name;

	@Column
	private String catalog2Id;
}
