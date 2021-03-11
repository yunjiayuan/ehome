package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityHouseService;
import com.busi.service.PassProveService;
import com.busi.utils.CommonUtils;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区出入证、证明相关接口
 * author：ZJJ
 * create time：2021-02-03 14:41:16
 */
@RestController
public class PassProveController extends BaseController implements PassProveApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    PassProveService passProveService;

    @Autowired
    CommunityHouseService communityHouseService;

    /***
     * 新增出入证、证明
     * @param prove
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addPassProve(@Valid @RequestBody PassProve prove, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否重复新增
        PassProve passProve = passProveService.find(prove.getCommunityHouseId(), prove.getVillageName(), prove.getIdCard(), prove.getType(), prove.getCommunityId(), prove.getHouseNumber(), prove.getHouseCompany(), prove.getUnitNumber(), prove.getUnitCompany(), prove.getRoomNumber());
        if (passProve != null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您已申请该房屋的出入证", new JSONObject());
        }
        //判断实名信息是否匹配

        prove.setReview(0);
        prove.setTime(new Date());
        passProveService.addPassProve(prove);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新出入证、证明
     * @param scenicSpot
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changePassProve(@Valid @RequestBody PassProve scenicSpot, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        scenicSpot.setReview(0);
        passProveService.changePassProve(scenicSpot);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 审核出入证、证明
     * @param communityEventReporting
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData toExaminePassProve(@Valid @RequestBody PassProve communityEventReporting, BindingResult bindingResult) {
        //判断是否有权限
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels < 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        passProveService.toExaminePassProve(communityEventReporting);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询用户状态
     * @param communityId    居委会ID
     * @param userId  用户ID
     * @param type    0出入证  1证明
     * @return
     */
    @Override
    public ReturnData findUserPassProve(@PathVariable long communityId, @PathVariable long userId, @PathVariable int type) {
        PageBean<CommunityHouse> pageBean = null;
        String uId = "";
        if (userId > 0) {
            uId = "#" + userId + "#";
        }
        pageBean = communityHouseService.findCommunityHouseList(communityId, uId, 1, 1000);
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "在线办理居委会证明，请先完善住房信息", new JSONObject());
        }
        List list1 = passProveService.findPassProve2(communityId, userId, type);
        if (list1 == null && list1.size() <= 0 && type == 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "未办理出入证", new JSONObject());
        }
        if (list1 == null && list1.size() <= 0 && type == 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "未办理我的证明", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 根据ID查询出入证、证明详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findPassProve(@PathVariable long id) {
        PassProve communityEventReporting = passProveService.findPassProve(id);
        if (communityEventReporting == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", communityEventReporting);
    }

    /***
     * 查询出入证、证明列表
     * @param communityId    居委会ID
     * @param userId    用户ID  查询我的出入证时传当前用户id，查询出入证管理时传0
     * @param type     -1表示查询所有 0出入证 1证明
     * @param auditType     -1表示查询所有 0待审核 1已审核通过 2未审核通过
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findPassProveList(@PathVariable long communityId, @PathVariable long userId,
                                        @PathVariable int type, @PathVariable int auditType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //判断是否有权限
        if (userId <= 0) {
            int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
            if (levels < 0) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
            }
        } else {
            if (userId != CommonUtils.getMyId()) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
            }
        }
        PageBean<PassProve> pageBean = null;
        pageBean = passProveService.findPassProveList(communityId, userId, type, auditType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 统计各种审核状态数量
     * @param communityId    居委会ID
     * @param type    0出入证  1证明
     * @return
     */
    @Override
    public ReturnData countPassProveAuditType(@PathVariable long communityId, @PathVariable int type) {
        //判断是否有审核权限
        int levels = CommonUtils.getAdministrator(CommonUtils.getMyId(), redisUtils);
        if (levels < 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您无权限进行此操作，请联系管理员申请权限!", new JSONObject());
        }
        //开始统计
        List list = null;
        int num = 0;
        int num1 = 0;
        int num2 = 0;
        list = passProveService.countAuditType(type, communityId);
        for (int i = 0; i < list.size(); i++) {
            PassProve hotel = (PassProve) list.get(i);
            if (hotel.getReview() == 0) {
                num++;
            }
            if (hotel.getReview() == 1) {
                num1++;
            }
            if (hotel.getReview() == 2) {
                num2++;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("auditType", num);//0待审核 1已审核通过 2未审核通过
        map.put("auditType1", num1);
        map.put("auditType2", num2);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
