package com.busi.controller.api;


import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 疫情相关接口
 * author：zhaojiajie
 * create time：2020-02-15 10:40:23
 */
public interface EpidemicSituationApiController {

    /***
     * 查询疫情
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findEpidemicSituation/{page}/{count}")
    ReturnData findEpidemicSituation(@PathVariable int page, @PathVariable int count);

}
