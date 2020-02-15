package com.busi.controller.local;

import com.busi.entity.EpidemicSituation;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/***
 * 疫情本地新增接口
 * author：zhaojiajie
 * create time：2020-02-15 10:40:23
 */
public interface EpidemicSituationLocalController {

    /***
     * 新增
     * @param epidemicSituation
     * @return
     */
    @PostMapping("addEpidemicSituation")
    ReturnData addEpidemicSituation(@RequestBody EpidemicSituation epidemicSituation);
}
