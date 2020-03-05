package com.qiazilai.qc_code;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QcCodeApplicationTests {

    @Test
    public void qc_Code() throws Exception{
        //JSONObject jsonObject = new JSONObject();
        //jsonObject.put("author","qiazilai");
        String content = "weixin://wxpay/bizpayurl?pr=9BgkfIj";
        HashMap<EncodeHintType, Object> encodeHintTypeObjectHashMap = new HashMap<>();
        encodeHintTypeObjectHashMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, encodeHintTypeObjectHashMap);
        String filePath = "C:\\Users\\Administrator\\Desktop\\";
        String fileName = "wxpay.jpg";
        Path codeImagepath = FileSystems.getDefault().getPath(filePath, fileName);
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",codeImagepath);
    }
}
