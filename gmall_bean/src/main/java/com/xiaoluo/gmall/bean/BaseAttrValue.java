package com.xiaoluo.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
public class BaseAttrValue implements Serializable {
	private static final long serialVersionUID = 684643463424237710L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	@Column
	private String valueName;

	@Column
	private String attrId;

	@Transient
	private String urlParam;


}
