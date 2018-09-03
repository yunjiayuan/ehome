package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface IPS_HomeApiController {

    /***
     * home推荐分页查询
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findHomeList/{userId}")
    ReturnData findHomeList(@PathVariable long userId);

    /**
     * 刷新公告时间
     * @param infoId
     * @param userId
     * @param afficheType
     * @return
     */
    @GetMapping("refreshTime/{infoId}/{userId}/{afficheType}")
    ReturnData refreshTime(@PathVariable long infoId, @PathVariable long userId, @PathVariable int afficheType);

    /**
     * 置顶公告
     * @param infoId
     * @param userId
     * @param frontPlaceType
     * @return
     */
    @GetMapping("setTop/{infoId}/{userId}/{frontPlaceType}/{afficheType}")
    ReturnData setTop(@PathVariable long infoId, @PathVariable long userId, @PathVariable int frontPlaceType, @PathVariable int afficheType);

}

