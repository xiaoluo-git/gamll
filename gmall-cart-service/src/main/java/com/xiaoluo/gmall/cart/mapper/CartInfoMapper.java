package com.xiaoluo.gmall.cart.mapper;

import com.xiaoluo.gmall.bean.CartInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CartInfoMapper extends Mapper<CartInfo> {
	List<CartInfo> listCartWithLatestPrice(String userId);

	void mergeCart(@Param("userId") String userId, @Param("tempId") String user_temp_id);
}
