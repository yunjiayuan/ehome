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
//        homeHospital.setReview(2);
        homeHospital.setTime(new Date());
        communityService.addCommunity(homeHospital);

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
//        UserInfo userInfo = null;
//        userInfo = userInfoUtils.getUserInfo(sa.getUserId());
//        if (userInfo != null) {
//            sa.setName(userInfo.getName());
//            sa.setHead(userInfo.getHead());
//            sa.setProTypeId(userInfo.getProType());
//            sa.setHouseNumber(userInfo.getHouseNumber());
//        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", sa);
    }

    /***
     * 查询列表
     * @param cityId     默认-1 百度地图中的城市ID，用于同城搜索
     * @param department  科室
     * @param search    模糊搜索（可以是：症状、疾病、医院、科室、医生名字）
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
//        for (int i = 0; i < list.size(); i++) {
//            Community sa = (Community) list.get(i);
//            if (sa != null) {
//                UserInfo userInfo = null;
//                userInfo = userInfoUtils.getUserInfo(sa.getUserId());
//                if (userInfo != null) {
//                    sa.setName(userInfo.getName());
//                    sa.setHead(userInfo.getHead());
//                    sa.setProTypeId(userInfo.getProType());
//                    sa.setHouseNumber(userInfo.getHouseNumber());
//                }
//            }
//        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 新增居民
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult) {
        return null;
    }

    /***
     * 更新居民
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeResident(@Valid @RequestBody CommunityResident homeHospital, BindingResult bindingResult) {
        return null;
    }

    /***
     * 删除居民
     * @param ids
     * @return:
     */
    @Override
    public ReturnData delResident(@PathVariable String ids) {
        return null;
    }

    /***
     * 查询居民列表
     * @param communityId    居委会ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findResidentList(@PathVariable int communityId, @PathVariable int page, @PathVariable int count) {
        return null;
    }

    /***
     * 新增房屋
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHouse(@Valid @RequestBody CommunityHouse homeHospital, BindingResult bindingResult) {
        return null;
    }

    /***
     * 更新房屋
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeHouse(@Valid @RequestBody CommunityHouse homeHospital, BindingResult bindingResult) {
        return null;
    }

    /***
     * 查询房屋详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findHouse(@PathVariable long id) {
        return null;
    }

    /**
     * @param ids
     * @Description: 删除房屋
     * @return:
     */
    @Override
    public ReturnData delHouse(@PathVariable String ids) {
        return null;
    }

    /***
     * 查询房屋列表
     * @param communityId    居委会ID
     * @param userId    房主ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @Override
    public ReturnData findHouseList(@PathVariable int communityId, @PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        return null;
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
     * 添加事件报备
     * @param shopFloorComment
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addEventReporting(@Valid @RequestBody CommunityEventReporting shopFloorComment, BindingResult bindingResult) {
        return null;
    }

    /***
     * 更新事件报备
     * @param homeHospital
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeEventReporting(@Valid @RequestBody CommunityEventReporting homeHospital, BindingResult bindingResult) {
        return null;
    }

    /***
     * 查询事件报备详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findEventReporting(@PathVariable long id) {
        return null;
    }

    /***
     * 查询事件报备列表
     * @param roomId     房屋ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findEventReportingList(@PathVariable long roomId, @PathVariable int page, @PathVariable int count) {
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
    public ReturnData findSetUpList(@PathVariable int communityId, @PathVariable int page, @PathVariable int count) {
        return null;
    }
}
