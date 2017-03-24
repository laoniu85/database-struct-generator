package com.leyoss;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * Created by liyongliu on 2017/3/24.
 */
public class TestFastJson
{
    @Test
    public void test(){
        JSONObject jsonObject=new JSONObject();
        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("a","b");
        jsonObject.put("test",jsonObject1);
        System.out.println(jsonObject.toJSONString());
    }
}
