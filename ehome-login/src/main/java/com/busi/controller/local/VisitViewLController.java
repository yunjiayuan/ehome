package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.VisitView;
import com.busi.service.VisitViewService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 更新访问量信息到数据 为MQ提供调用接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController
public class VisitViewLController extends BaseController implements VisitViewLocalController {

    @Autowired
    VisitViewService visitViewService;

    /***
     * 更新访问量信息到数据库
     * @param visitView
     * @return
     */
    @Override
    public ReturnData updateLocalVisit(@RequestBody VisitView visitView) {
        VisitView v = visitViewService.findVisitView(visitView.getUserId());
        int count = 0;
        if(v==null){
            count = visitViewService.add(visitView);
        }else{
            count = visitViewService.update(visitView);
        }
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"更新用户["+visitView.getUserId()+"]访问量记录到数据库失败",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
