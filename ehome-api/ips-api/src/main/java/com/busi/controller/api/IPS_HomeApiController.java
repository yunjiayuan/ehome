package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface IPS_HomeApiController{
    /***
     * home推荐分页查询
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findHomeList/{userId}")
    ReturnData findHomeList(@PathVariable int userId);

}

