package com.xiaoluo.gmall.service;

import com.xiaoluo.gmall.bean.PaymentInfo;

public interface PaymentInfoService {
	void savePaymentInfo(PaymentInfo paymentInfo);

	PaymentInfo getPaymentInfo(PaymentInfo paymentInfoQuery);

	void updatePaymentInfoByOutTradeNo(String out_trade_no, PaymentInfo paymentInfoForUpdate);
}
