package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityEventReportingService;
import com.busi.service.CommunityService;
import com.busi.service.RealNameInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.RealNameUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 社区新冠状病毒事件报备相关接口
 * author：suntj
 * create time：2020-03-17 15:53:35
 */
@RestController
public class CommunityEventReportingController extends BaseController implements CommunityEventReportingApiController {

    @Autowired
    CommunityEventReportingService communityEventReportingService;

    @Autowired
    CommunityService communityService;

    @Autowired
    RealNameInfoService realNameInfoService;

    /***
     * 新增新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @Override
    public ReturnData addCommunityEventReporting(@Valid @RequestBody CommunityEventReporting communityEventReporting, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证身份证与姓名是否相符
//        List<RealNameInfo> list = null;
//        //查本地库中是否存在该实名信息
//        list = realNameInfoService.findRealNameInfo(communityEventReporting.getRealName(), communityEventReporting.getIdCard());
//        RealNameInfo rni = null;
//        if (list == null ||list.size() <= 0) {//本地不存在
//            //本地中不存在 远程调用第三方平台认证
//            rni = RealNameUtils.checkRealName(CommonUtils.getMyId(),communityEventReporting.getRealName(), communityEventReporting.getIdCard());
//            if (rni == null) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的身份证与姓名不符，请重新输入", new JSONObject());
//            }
//        }
        communityEventReporting.setReview(0);
        communityEventReporting.setTime(new Date());
        communityEventReportingService.addCommunityEventReporting(communityEventReporting);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @Override
    public ReturnData changeCommunityEventReporting(@Valid @RequestBody CommunityEventReporting communityEventReporting, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证身份证与姓名是否相符
//        List<RealNameInfo> list = null;
//        //查本地库中是否存在该实名信息
//        list = realNameInfoService.findRealNameInfo(communityEventReporting.getRealName(), communityEventReporting.getIdCard());
//        RealNameInfo rni = null;
//        if (list == null ||list.size() <= 0) {//本地不存在
//            //本地中不存在 远程调用第三方平台认证
//            rni = RealNameUtils.checkRealName(CommonUtils.getMyId(),communityEventReporting.getRealName(), communityEventReporting.getIdCard());
//            if (rni == null) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的身份证与姓名不符，请重新输入", new JSONObject());
//            }
//        }
        communityEventReporting.setReview(0);//改为未报备
        communityEventReportingService.changeCommunityEventReporting(communityEventReporting);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 审核新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @Override
    public ReturnData toExamineCommunityEventReporting(@Valid @RequestBody CommunityEventReporting communityEventReporting, BindingResult bindingResult) {
        //验证当前登录者是否有修改权限
        CommunityResident communityResident = communityService.findResident(communityEventReporting.getCommunityId(),CommonUtils.getMyId());
        if(communityResident==null||communityResident.getIdentity()==0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您无权限进行此操作", new JSONObject());
        }
        communityEventReportingService.toExamineCommunityEventReporting(communityEventReporting);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 根据ID查询新冠状病毒报备详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findCommunityEventReportin(@PathVariable long id) {
        CommunityEventReporting communityEventReporting = communityEventReportingService.findCommunityEventReporting(id);
        if (communityEventReporting == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", communityEventReporting);
    }

    /**
     * @Description: 删除新冠状病毒报备
     * @return:
     */
    @Override
    public ReturnData delCommunityEventReportin(@PathVariable String ids) {
        if(CommonUtils.checkFull(ids)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "删除参数ids不能为空", new JSONObject());
        }
        communityEventReportingService.delCommunityEventReporting(ids.split(","),CommonUtils.getMyId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询新冠状病毒报备列表
     * @param communityId    居委会ID
     * @param userId   业主的用户ID  上一个界面中房屋数据中userId   管理员查询传0
     * @param communityHouseId  房屋ID 大于0时 为查询指定房屋的报备信息 管理员查询传0
     * @param type     -1表示查询所有 0表示查询未审核 1表示查询已审核 2表示查询审核失败
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHouseList(@PathVariable long communityId,@PathVariable  long userId,@PathVariable  long communityHouseId,@PathVariable  int type,@PathVariable  int page,@PathVariable  int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        long houseUserId = 0;// 0表示为业主或者管理员  具体的值表示住户查询
        if(CommonUtils.getMyId()==userId){//查询者是业主
            houseUserId = 0;
        }else{
            houseUserId = userId;
        }
        PageBean<CommunityEventReporting> pageBean = null;
        pageBean = communityEventReportingService.findCommunityEventReportingList(communityId,houseUserId,communityHouseId,type, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
