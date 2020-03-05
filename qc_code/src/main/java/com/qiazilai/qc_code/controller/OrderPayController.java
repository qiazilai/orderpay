package com.qiazilai.qc_code.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.qiazilai.qc_code.config.WXConfig;
import com.qiazilai.qc_code.utils.XMLUtil4jdom;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OrderPayController {

    @Autowired
    WXConfig wxConfig;

    @RequestMapping("/orderpay")
    public void OrderPay(HttpServletResponse response, @RequestParam("ordernumber") String ordernumber, @RequestParam("totalfee") String totalfee, @RequestParam("body") String body) throws Exception {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", wxConfig.getAppid());
        paramMap.put("mch_id", wxConfig.getMch_id());
        String nonce_str = WXPayUtil.generateNonceStr();
        paramMap.put("nonce_str", nonce_str);
        paramMap.put("body", body);
        paramMap.put("out_trade_no", ordernumber);
        paramMap.put("total_fee", totalfee);
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        paramMap.put("spbill_create_ip", hostAddress);
        paramMap.put("notify_url", wxConfig.getNotify_url());
        paramMap.put("trade_type", wxConfig.getTrade_type());
        paramMap.put("product_id", ordernumber);
        String signture = WXPayUtil.generateSignature(paramMap, wxConfig.getKey());
        paramMap.put("sign", signture);
        String mapToXml = WXPayUtil.mapToXml(paramMap);
        String resposeFromWX = this.doPostByXml(wxConfig.getPay_url(), mapToXml);
        Map map = XMLUtil4jdom.doXMLParse(resposeFromWX);
        if (map != null && "SUCCESS".equals(map.get("return_code"))) {
            String code_url = (String) map.get("code_url");
            get_CodeImage(code_url, response);
        }
    }

    public String doPostByXml(String url, String requestDataXml) throws IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse httpResponse = null;
        httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(15000)
                .setConnectionRequestTimeout(60000)
                .setSocketTimeout(60000)
                .build();
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(new StringEntity(requestDataXml, "UTF-8"));
        httpPost.addHeader("Content-Type", "text/xml");
        String result = "";
        httpResponse = httpclient.execute(httpPost);
        HttpEntity entity = httpResponse.getEntity();
        result = EntityUtils.toString(entity, "UTF-8");
        return result;
    }

    public void get_CodeImage(String content, HttpServletResponse response) throws Exception {
        HashMap<EncodeHintType, Object> encodeHintTypeObjectHashMap = new HashMap<>();
        encodeHintTypeObjectHashMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, encodeHintTypeObjectHashMap);
        ByteArrayOutputStream imageout = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "jpg", imageout);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageout.toByteArray());
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        OutputStream outputStream = response.getOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        bufferedImage.flush();
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping("/wxPayNotity")
    public void notityFromWX(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuffer sb = new StringBuffer();
        InputStream inputStream = request.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        Map<String, String> map = XMLUtil4jdom.doXMLParse(sb.toString());
    }
}
