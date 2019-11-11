package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.utils.StatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 苹果官方需要的跳转地址
 * author：SunTianJie
 * create time：2019/11/1 14:01
 */
@RestController
public class AppleApplinksController extends BaseController {

    @GetMapping("getApplinks")
    public Object getApplinks(){
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> map1 = new HashMap<>();
        Map<String,Object> map2 = new HashMap<>();
        map2.put("appID","1140776324.ehome.lichengwang.com:8760");
        map2.put("paths","*");

        List list = new ArrayList();
        list.add(map2);

        map1.put("details",list);

        map.put("applinks",map1);

        return returnData(map);
    }
}
