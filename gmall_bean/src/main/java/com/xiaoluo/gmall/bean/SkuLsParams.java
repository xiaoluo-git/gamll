package com.xiaoluo.gmall.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuLsParams implements Serializable {

	String  keyword;

	String catalog3Id;

	String[] valueId;

	int pageNo=1;

	int pageSize=2;
}