package com.busi.controller.local;

import com.busi.entity.ImageDeleteLog;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * 图片相关接口
 * author：SunTianJie
 * create time：2018/8/2 8:09
 */
public interface ImageDeleteLogLocalController {

    /***
     * 新增将要删除的图片记录
     * @param imageDeleteLog
     * @return
     */
    @PostMapping("addImageDeleteLog")
    ReturnData addImageDeleteLog(@Valid @RequestBody ImageDeleteLog imageDeleteLog);

}
