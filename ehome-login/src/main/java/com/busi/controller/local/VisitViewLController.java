package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Footprint;
import com.busi.entity.ReturnData;
import com.busi.entity.VisitView;
import com.busi.service.FootprintService;
import com.busi.service.VisitViewService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

/**
 * 更新访问量信息到数据 为MQ提供调用接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController
public class VisitViewLController extends BaseController implements VisitViewLocalController {

    @Autowired
    VisitViewService visitViewService;

    @Autowired
    FootprintService footprintService;

    /***
     * 更新访问量信息和脚印记录
     * @return
     */
    @Override
    public ReturnData updateLocalVisit(@RequestBody VisitView visitView) {
        //更新访问量
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
        //新增脚印记录
        Footprint footprint = new Footprint();
        footprint.setMyId(visitView.getMyId());
        footprint.setUserId(visitView.getUserId());
        footprint.setTime(new Date());
        footprintService.add(footprint);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
