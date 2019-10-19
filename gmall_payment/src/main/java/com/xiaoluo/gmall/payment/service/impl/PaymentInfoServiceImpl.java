package com.xiaoluo.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xiaoluo.gmall.bean.PaymentInfo;
import com.xiaoluo.gmall.payment.mapper.PaymentInfoMapper;
import com.xiaoluo.gmall.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
public class PaymentInfoServiceImpl implements PaymentInfoService {
	@Autowired
	private PaymentInfoMapper paymentInfoMapper;

	@Override
	public void savePaymentInfo(PaymentInfo paymentInfo) {
		paymentInfoMapper.insertSelective(paymentInfo);
	}

	@Override
	public PaymentInfo getPaymentInfo(PaymentInfo paymentInfoQuery) {

		return paymentInfoMapper.selectOne(paymentInfoQuery);
	}

	@Override
	public void updatePaymentInfoByOutTradeNo(String out_trade_no, PaymentInfo paymentInfoForUpdate) {
		Example example = new Example(PaymentInfo.class);
		example.createCriteria().andEqualTo("outTradeNo",out_trade_no);
		paymentInfoMapper.updateByExampleSelective(paymentInfoForUpdate,example);
	}
}
