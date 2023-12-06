package com.sherlockgy;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.junit.Test;

public class GeneralMaskUtilTest {
    @Test
    public void testMask1() {
        MessageReq req = new MessageReq();
        req.setMobileNo("13115681111");
        req.setMsgCode("XFD_2022");
        req.setParamJson("{\"periodAmount\":\"37.72\",\"name\":\"曹小明\"}");
        System.out.println(GeneralMaskUtil.mask(req));
    }

    @Test
    public void testMask2() {
        MessageReq req = new MessageReq();
        req.setMobileNo("13115681111");
        req.setMsgCode("XFD_2022");
        req.setParamJson("{\"periodAmount\":\"37.72\",\"name\":\"曹小明\"}");

        String jsonString = JSON.toJSONString(req);
        System.out.println("原始字符串：" + jsonString);
        System.out.println(GeneralMaskUtil.maskJsonString(jsonString));
    }
}

@Data
class MessageReq implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String mobileNo;

    private String msgCode;

    private String paramJson;

    private String remark;

    private String msgSender;
}