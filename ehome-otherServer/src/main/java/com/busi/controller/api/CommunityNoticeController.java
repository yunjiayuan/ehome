package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.CommunityNotice;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.CommunityNoticeService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * @program: ehome
 * @description: 公告
 * @author: ZHaoJiaJie
 * @create: 2020-03-23 16:33:18
 */
@RestController
public class CommunityNoticeController extends BaseController implements CommunityNoticeApiController {
    @Autowired
    CommunityNoticeService todayNoticeService;

    /***
     * 新增
     * @param todayNotice
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addNotice(@Valid @RequestBody CommunityNotice todayNotice, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        todayNotice.setAddTime(new Date());
        todayNotice.setRefreshTime(new Date());
        todayNoticeService.add(todayNotice);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新
     * @Param: todayNotice
     * @return:
     */
    @Override
    public ReturnData editNotice(@Valid @RequestBody CommunityNotice todayNotice, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        todayNotice.setRefreshTime(new Date());
        todayNoticeService.editNotice(todayNotice);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delNotice(@PathVariable String ids) {
        todayNoticeService.del(ids);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询列表
     * @param communityId newsType=0时为居委会ID  newsType=1时为物业ID
     * @param type 类型： 0居委会  1物业
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findNoticeList(@PathVariable long communityId, @PathVariable int type, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (type < 0 || type > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数type有误", new JSONObject());
        }
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunityNotice> pageBean;
        pageBean = todayNoticeService.findList(communityId, type, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

}
