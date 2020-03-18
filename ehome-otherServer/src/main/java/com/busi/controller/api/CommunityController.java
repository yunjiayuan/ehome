package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @program: ehome
 * @description: 居委会相关
 * @author: ZHaoJiaJie
 * @create: 2020-03-17 17:07:43
 */
@RestController
public class CommunityController extends BaseController implements CommunityApiController {

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    CommunityService communityService;

    /***
     * 查询是否已加入居委会
     * @param userId
     * @return
     */
    @Override
    public ReturnData findJoinCommunity(@PathVariable long userId) {
        CommunityResident situationAbout = communityService.findJoin(userId);
        if (situationAbout == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", situationAbout);
    }

    /***
     * 新增居委会
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addCommunity(@Valid @RequestBody Community homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeHospital.setTime(new Date());
        communityService.addCommunity(homeHospital);

        //新增居民
        CommunityResident resident = new CommunityResident();
        resident.setTime(new Date());
        resident.setCommunityId(homeHospital.getId());
        resident.setUserId(homeHospital.getUserId());
        resident.setIdentity(2);
        communityService.addResident(resident);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeHospital.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新居委会
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeCommunity(@Valid @RequestBody Community homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        communityService.changeCommunity(homeHospital);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居委会详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findCommunity(@PathVariable long id) {
        Community sa = communityService.findCommunity(id);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询居委会不存在!", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", sa);
    }

    /***
     * 查询列表
     * @param lon     经度
     * @param lat  纬度
     * @param string    模糊搜索 (居委会名字)
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findCommunityList(@PathVariable double lon, @PathVariable double lat, @PathVariable String string, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<Community> pageBean = null;
        pageBean = communityService.findCommunityList(lon, lat, string, province, city, district, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        for (int i = 0; i < list.size(); i++) {
            Community ik = (Community) list.get(i);
            double userlon = ik.getLon();
            double userlat = ik.getLat();

            int distance = (int) Math.round(CommonUtils.getShortestDistance(userlon, userlat, lon, lat));

            ik.setDistance(distance);//距离/m
        }
        Collections.sort(list, new Comparator<Community>() {
            /*
             * int compare(Person o1, Person o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和p2相等，
             * 返回正数表示：o1大于o2
             */
            @Override
            public int compare(Community o1, Community o2) {
                // 按照距离进行正序排列
                if (o1.getDistance() > o2.getDistance()) {
                    return 1;
                }
                if (o1.getDistance() == o2.getDistance()) {
                    return 0;
                }
                return -1;
            }
        });
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 加入居委会
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (homeHospital.getType() == 0) {//主动加入
            homeHospital.setTime(new Date());
            communityService.addResident(homeHospital);
        } else {
            //判断邀请者权限
            CommunityResident sa = communityService.findResident(homeHospital.getCommunityId(), homeHospital.getMasterId());
            if (sa == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
            }
            //判断是否已经加入了
            List list = communityService.findIsList(homeHospital.getCommunityId(), homeHospital.getUserIds());
            if (list != null && list.size() > 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "已经邀请过了", new JSONArray());
            }
            String[] userId = homeHospital.getUserIds().split(",");
            for (int i = 0; i < userId.length; i++) {
                CommunityResident resident = new CommunityResident();
                resident.setIdentity(0);
                resident.setType(1);
                resident.setTime(new Date());
                resident.setCommunityId(homeHospital.getId());
                resident.setMasterId(homeHospital.getMasterId());
                resident.setUserId(Long.parseLong(userId[i]));
                communityService.addResident(resident);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
    }

    /***
     * 更新居民权限
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断权限
        CommunityResident sa = communityService.findResident(homeHospital.getCommunityId(), CommonUtils.getMyId());
        if (sa == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "没有权限", new JSONArray());
        }
        if (sa.getIdentity() > 1) {
            communityService.changeResident(homeHospital);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param ids
     * @param communityId
     * @Description: 删除居民
     * @return:
     */
    @Override
    public ReturnData delResident(@PathVariable String ids, @PathVariable long communityId) {
        Community sa = communityService.findCommunity(communityId);
        if (sa == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "当前查询居委会不存在!", new JSONObject());
        }
        if (sa.getUserId() != CommonUtils.getMyId()) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "没有权限!", new JSONObject());
        }
        communityService.delResident(ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询居民列表
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findResidentList(@PathVariable long communityId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunityResident> pageBean = null;
        pageBean = communityService.findResidentList(communityId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list == null || list.size() <= 0) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        for (int i = 0; i < list.size(); i++) {
            CommunityResident sa = (CommunityResident) list.get(i);
            if (sa != null) {
                UserInfo userInfo = null;
                userInfo = userInfoUtils.getUserInfo(sa.getUserId());
                if (userInfo != null) {
                    sa.setName(userInfo.getName());
                    sa.setHead(userInfo.getHead());
                    sa.setProTypeId(userInfo.getProType());
                    sa.setHouseNumber(userInfo.getHouseNumber());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 添加留言板
     * @param shopFloorComment
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addMessageBoard(@Valid @RequestBody CommunityMessageBoard shopFloorComment, BindingResult bindingResult) {
        return null;
    }

    /***
     * 删除留言板
     * @param id 评论ID
     * @param communityId 居委会ID
     * @return
     */
    @Override
    public ReturnData delMessageBoard(@PathVariable long id, @PathVariable long communityId) {
        return null;
    }

    /***
     * 查询留言板记录
     * @param communityId     居委会ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findMessageBoardList(@PathVariable long communityId, @PathVariable int page, @PathVariable int count) {
        return null;
    }

    /***
     * 查询留言板指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findMessageBoardReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count) {
        return null;
    }

    /***
     * 新增居委会人员设置
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addSetUp(@Valid @RequestBody CommunitySetUp homeHospital, BindingResult bindingResult) {
        return null;
    }

    /***
     * 更新居委会人员设置
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeSetUp(@Valid @RequestBody CommunitySetUp homeHospital, BindingResult bindingResult) {
        return null;
    }

    /**
     * @param ids
     * @Description: 删除居委会人员设置
     * @return:
     */
    @Override
    public ReturnData delSetUp(@PathVariable String ids) {
        return null;
    }

    /***
     * 查询居委会人员设置列表（按职务正序）
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findSetUpList(@PathVariable long communityId, @PathVariable int page, @PathVariable int count) {
        return null;
    }
}
