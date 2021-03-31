package com.busi.controller.api;

import controller.api.PageApiController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;

/**
 * 网页相关访问
 * author：SunTianJie
 * create time：2019/1/2 15:41
 */
@Controller
public class PageController implements PageApiController {

    @Override
    public String downLoadNew(HashMap<String, Object> map,@PathVariable String shareCode) {
        map.put("shareCode",shareCode);
        return "downLoad/index.html";
    }
}
