package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.VisitView;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * 访问量信息相关接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface VisitViewApiController {
    /***
     * 更新访问量信息
     * @param visitView
     * @return
     */
    @PutMapping("updateVisit")
    ReturnData updateVisit(@Valid @RequestBody VisitView visitView, BindingResult bindingResult);

}
