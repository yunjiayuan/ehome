package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityHouseService;
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
import java.util.*;

/**
 * 社区房屋相关接口
 * author：suntj
 * create time：2020-03-17 15:53:35
 */
@RestController
public class CommunityHouseController extends BaseController implements CommunityHouseApiController {

    @Autowired
    CommunityHouseService communityHouseService;

    @Autowired
    RealNameInfoService realNameInfoService;

    /***
     * 新增房屋
     * @param communityHouse
     * @return
     */
    @Override
    public ReturnData addHouse(@Valid @RequestBody CommunityHouse communityHouse, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证身份证与姓名是否相符
//        List<RealNameInfo> list = null;
//        //查本地库中是否存在该实名信息
//        list = realNameInfoService.findRealNameInfo(communityHouse.getRealName(), communityHouse.getIdCard());
//        RealNameInfo rni = null;
//        if (list == null ||list.size() <= 0) {//本地不存在
//            //本地中不存在 远程调用第三方平台认证
//            rni = RealNameUtils.checkRealName(CommonUtils.getMyId(),communityHouse.getRealName(), communityHouse.getIdCard());
//            if (rni == null) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的身份证与姓名不符，请重新输入", new JSONObject());
//            }
//        }
        communityHouse.setReview(1);
        communityHouse.setTime(new Date());
        //特殊处理居住人员信息
        String household = communityHouse.getHousehold();
        if(!CommonUtils.checkFull(household)){
            String[] hhd = household.split(";");
            String householdUserIds = "";
            for(int i = 0;i<hhd.length;i++){
                String userId = "#"+hhd[i].split(",")[0]+"#";
                if(i==hhd.length-1){
                    householdUserIds += userId;
                }
                householdUserIds += userId+",";
            }
            communityHouse.setHouseholdUserIds("#"+communityHouse.getUserId()+"#,"+householdUserIds);
        }
        communityHouseService.addCommunityHouse(communityHouse);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新房屋
     * @param communityHouse
     * @return
     */
    @Override
    public ReturnData changeHouse(@Valid @RequestBody CommunityHouse communityHouse, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证身份证与姓名是否相符
//        List<RealNameInfo> list = null;
//        //查本地库中是否存在该实名信息
//        list = realNameInfoService.findRealNameInfo(communityHouse.getRealName(), communityHouse.getIdCard());
//        RealNameInfo rni = null;
//        if (list == null ||list.size() <= 0) {//本地不存在
//            //本地中不存在 远程调用第三方平台认证
//            rni = RealNameUtils.checkRealName(CommonUtils.getMyId(),communityHouse.getRealName(), communityHouse.getIdCard());
//            if (rni == null) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您输入的身份证与姓名不符，请重新输入", new JSONObject());
//            }
//        }
        //特殊处理居住人员信息
        String household = communityHouse.getHousehold();
        if(!CommonUtils.checkFull(household)){
            String[] hhd = household.split(";");
            String householdUserIds = "";
            for(int i = 0;i<hhd.length;i++){
                String userId = "#"+hhd[i].split(",")[0]+"#";
                if(i==hhd.length-1){
                    householdUserIds += userId;
                }
                householdUserIds += userId+",";
            }
            communityHouse.setHouseholdUserIds(householdUserIds);
        }
        communityHouseService.changeCommunityHouse(communityHouse);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询房屋详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findHouse(@PathVariable long id) {
        CommunityHouse communityHouse = communityHouseService.findCommunityHouse(id);
        if (communityHouse == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", communityHouse);
    }

    /**
     * @Description: 删除房屋
     * @return:
     */
    @Override
    public ReturnData delHouse(@PathVariable String ids) {
        if(CommonUtils.checkFull(ids)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "删除参数ids不能为空", new JSONObject());
        }
        communityHouseService.delResident(ids.split(","),CommonUtils.getMyId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询房屋列表
     * @param communityId    居委会ID
     * @param userId   当前登录者ID userId=0 w为管理员查询
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHouseList(@PathVariable int communityId,@PathVariable long userId,@PathVariable int page,@PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunityHouse> pageBean = null;
        pageBean = communityHouseService.findCommunityHouseList(communityId, "#"+userId+"#", page, count);
        if(userId>0){
            //对结果集进行处理 判断是业主查询 还是住户查询 并设置标识 方便客户端显示
            List list = pageBean.getList();
            if(list!=null&&list.size()>0){
                for (int i = 0; i <list.size() ; i++) {
                    CommunityHouse communityHouse = (CommunityHouse)list.get(i);
                    if(communityHouse==null){
                        continue;
                    }
                    if(communityHouse.getUserId()==CommonUtils.getMyId()){//业主查询直接返回所以数据 无需处理
                        break;
                    }
                    //住户查询 只显示自己的一条数据
                    String household = communityHouse.getHousehold();
                    if(!CommonUtils.checkFull(household)){
                        String[] hhd = household.split(";");
                        String householdUserIds = "";
                        for(int j = 0;j<hhd.length;j++){
                            long householdUserId = Long.parseLong(hhd[j].split(",")[0]);
                            if(CommonUtils.getMyId()==householdUserId){
                                communityHouse.setHouseholdUserIds(householdUserIds);
                                communityHouse.setHouseholdType(1);//住户标识 只能显示自己的信息
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
