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
        footmark.setAddTime(new Date());
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
    public ReturnData delFootmark(String ids) {
        //查询数据库
        Footmark footmark = footmarkService.findByIds(ids);

        footmark.setFootmarkStatus(1);
        footmarkService.updateDel(footmark);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
