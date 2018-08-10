package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface IPS_HomeApiController{
    /***
     * home推荐分页查询
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findHomeList/{page}/{count}")
    ReturnData findHomeList(@PathVariable int page, @PathVariable int count);

}

