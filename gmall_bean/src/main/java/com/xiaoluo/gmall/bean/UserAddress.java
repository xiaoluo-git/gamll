package com.xiaoluo.gmall.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserAddress implements Serializable {

	@Id
	@Column
	private Integer id;

	@Column
	private String userAddress;

	@Column
	private Integer userId;

	@Column
	private String consignee;

	@Column
	private String phoneNum;

	@Column
	private String isDefault;
}
