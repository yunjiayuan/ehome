package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CommunityMessageService;
import com.busi.service.CommunityService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import com.busi.utils.UserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 居委会、物业消息相关接口
 * @author: ZHaoJiaJie
 * @create: 2020-03-24 13:58:12
 */
@RestController
public class CommunityMessageController extends BaseController implements CommunityMessageApiController {

    @Autowired
    private UserInfoUtils userInfoUtils;

    @Autowired
    CommunityService communityService;

    @Autowired
    private CommunityMessageService communityMessageService;

    /***
     * 查询消息接口
     * @param communityType     类别   0居委会  1物业
     * @param communityId     type=0时为居委会ID  type=1时为物业ID
     * @param userId     用户ID
     * @param type       查询类型  0所有 1未读 2已读
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findMessageList(@PathVariable int communityType, @PathVariable int communityId, @PathVariable int type, @PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<CommunityMessage> pageBean;
        pageBean = communityMessageService.findMessageList(communityType, communityId, type, userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        String ids = "";
        String blIds = "";
        List list = null;
        List list2 = null;
        UserInfo userInfo = null;
        CommunityMessage mess = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                mess = (CommunityMessage) list.get(i);
                if (mess != null) {
                    if (mess.getNewsState() == 1) {
                        ids += mess.getId() + ",";//消息ID
                    }
                    if (mess.getCommunityId() > 0) {
                        blIds += mess.getCommunityId() + ",";//居委会或物业ID
                    }
                }
            }
            int identity = 0;   //身份:0普通 1管理员 2创建者
            if (communityType == 0) {//居委会
                //查询身份
                CommunityResident resident = communityService.findResident(communityId, userId);
                if (resident != null) {
                    identity = resident.getIdentity();
                }
                list2 = communityService.findCommunityList2(blIds);
                if (list2 != null && list2.size() > 0) {
                    Community homeBlog = null;
                    for (int i = 0; i < list.size(); i++) {
                        mess = (CommunityMessage) list.get(i);
                        if (mess != null) {
                            for (int j = 0; j < list2.size(); j++) {
                                homeBlog = (Community) list2.get(j);
                                if (homeBlog != null) {
                                    boolean flag = false;
                                    if (mess.getCommunityId() > 0 && mess.getCommunityId() == homeBlog.getId()) {
                                        flag = true;
                                    }
                                    if (flag) {
                                        userInfo = userInfoUtils.getUserInfo(mess.getUserId());
                                        if (userInfo != null) {
                                            mess.setUserHead(userInfo.getHead());
                                            mess.setUserName(userInfo.getName());
                                        }
                                        mess.setName(homeBlog.getName());
                                        if (mess.getReplayId() > 0) {
                                            userInfo = userInfoUtils.getUserInfo(mess.getReplayId());
                                            mess.setReplayName(userInfo.getName());
                                        }
                                        //返回图片
                                        mess.setCover(homeBlog.getCover());
                                    }
                                }
                            }
                        }
                    }
                }
            } else {//物业

            }
            if (!CommonUtils.checkFull(ids)) {
                if (identity <= 0) {//普通用户
                    //更新消息状态
                    communityMessageService.updateState(communityType, communityId, userId, ids.split(","));
                } else {//管理员
                    List wardenList = communityService.findWardenList(communityId);
                    String users = "";
                    if (wardenList != null && wardenList.size() > 0) {
                        for (int i = 0; i < wardenList.size(); i++) {
                            CommunityResident resident = (CommunityResident) wardenList.get(i);
                            if (resident != null) {
                                users += resident.getUserId() + ",";
                            }
                        }
                        //更新所有管理员消息状态
                        communityMessageService.updateState2(communityType, communityId, users.split(","));
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 获取未读消息
     * @param communityType     类别   0居委会  1物业
     * @param communityId     type=0时为居委会ID  type=1时为物业ID
     * @param userId     查询用户ID
     * @return
     */
    @Override
    public ReturnData getMessageCount(@PathVariable int communityType, @PathVariable int communityId, @PathVariable long userId) {
        //消息
        long num = communityMessageService.getCount(communityType, communityId, userId);

        Map<String, Long> map = new HashMap<>();
        map.put("num", num);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
