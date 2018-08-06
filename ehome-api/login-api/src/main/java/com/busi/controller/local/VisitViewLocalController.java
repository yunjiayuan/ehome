package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.VisitView;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 访问量信息相关接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface VisitViewLocalController {

    /***
     * 更新访问量信息到数据库
     * @param visitView
     * @return
     */
    @PutMapping("updateLocalVisit")
    ReturnData updateLocalVisit(@RequestBody VisitView visitView);

}
