package com.qiazilai.qc_code.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
public class WXConfig {
    @Value("${wxpay.appid}")
    private String appid;

    @Value("${wxpay.mch_id}")
    private String mch_id;

    @Value("${wxpay.notify_url}")
    private String notify_url;

    @Value("${wxpay.trade_type}")
    private String trade_type;

    @Value("${wxpay.key}")
    private String key;

    @Value("${wxpay.pay_url}")
    private String pay_url;
}
