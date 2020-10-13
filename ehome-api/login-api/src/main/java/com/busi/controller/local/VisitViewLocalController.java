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

    /***
     * 随机新增访问量，每日给用户新增一些访问量（用于服务间调用）
     * @param visitView
     * @return
     */
    @PutMapping("updateLocalRandomVisit")
    ReturnData updateLocalRandomVisit(@RequestBody VisitView visitView);

}
