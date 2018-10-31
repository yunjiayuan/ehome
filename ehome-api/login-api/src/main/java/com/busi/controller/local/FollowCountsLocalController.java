package com.busi.controller.local;


import com.busi.entity.FollowCounts;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.*;

/**
 * 更新粉丝数（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface FollowCountsLocalController {

    /***
     * 更新粉丝数
     * @param followCounts
     * @return
     */
    @PutMapping("updateFollowCounts")
    ReturnData updateFollowCounts(@RequestBody FollowCounts followCounts);

}
