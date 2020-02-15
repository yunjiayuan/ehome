package com.busi.controller.api;


import com.busi.entity.EpidemicSituationAbout;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    /***
     * 新增我和疫情
     * @param epidemicSituationAbout
     * @return
     */
    @PostMapping("addESabout")
    ReturnData addESabout(@Valid @RequestBody EpidemicSituationAbout epidemicSituationAbout, BindingResult bindingResult);

    /***
     * 编辑我和疫情
     * @param epidemicSituationAbout
     * @return
     */
    @PutMapping("changeESabout")
    ReturnData changeESabout(@Valid @RequestBody EpidemicSituationAbout epidemicSituationAbout, BindingResult bindingResult);

    /***
     * 查询我和疫情信息
     * @param userId
     * @return
     */
    @GetMapping("findESabout/{userId}")
    ReturnData findESabout(@PathVariable long userId);

}
