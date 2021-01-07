package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Footmark;
import com.busi.entity.ReturnData;
import com.busi.service.FootmarkService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * 足迹相关接口(服务期间调用)
 * author：zhaojiajie
 * create time：2018-10-9 14:45:32
 */
@RestController
public class FootmarkLController extends BaseController implements FootmarkLocalController {

    @Autowired
    FootmarkService footmarkService;

    /**
     * @program: ehome
     * @description: 添加足迹
     * @author: ZHaoJiaJie
     * @create: 2019-1-25 13:35:52
     */
    @Override
    public ReturnData addFootmark(@RequestBody Footmark footmark) {
        //添加足迹
        if (footmark.getFootmarkType() == 6) {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                footmark.setAddTime(dateformat.parse(footmark.getAudioUrl()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            footmark.setAddTime(new Date());
        }
        footmarkService.add(footmark);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @program: ehome
     * @description: 更新足迹
     * @author: ZHaoJiaJie
     * @create: 2019-3-25 13:35:52
     */
    @Override
    public ReturnData updateFootmark(@RequestBody Footmark footmark) {

        footmarkService.updateFootmark(footmark);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delFootmarkPad(@RequestBody Footmark footmark) {
        footmarkService.delFootmarkPad(footmark);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
