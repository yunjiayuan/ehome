package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Look;
import com.busi.entity.ReturnData;
import com.busi.service.LookService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

/**
 * @program: 浏览记录
 * @author: ZHaoJiaJie
 * @create: 2018-08-24 16:50
 */
@RestController
public class LookLController extends BaseController implements LookLocalController {

    @Autowired
    LookService lookService;

    /***
     * 新增
     * @param look
     * @return
     */
    @Override
    public ReturnData addLook(@RequestBody Look look) {

        look.setTime(new Date());
        lookService.add(look);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
