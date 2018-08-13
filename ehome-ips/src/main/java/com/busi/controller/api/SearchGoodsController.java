package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.IPS_Home;
import com.busi.entity.SearchGoods;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.mq.MqProducer;
import com.busi.service.SearchGoodsService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

/**
 * @program: 寻人寻物失物招领
 * @author: ZHaoJiaJie
 * @create: 2018-08-10 15:01
 */

@RestController
public class SearchGoodsController extends BaseController implements SearchGoodsApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqProducer mqProducer;

    @Autowired
    SearchGoodsService searchGoodsService;

    /***
     * 新增
     * @param searchGoods
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addMatter(@Valid @RequestBody SearchGoods searchGoods, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //计算公告分数
        int num3 = 0;//图片
        int fraction = 0;//公告分数
        int num = CommonUtils.getStringLengsByByte(searchGoods.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(searchGoods.getContent());//内容
        if (!CommonUtils.checkFull(searchGoods.getImgUrl())) {
            String[] imgArray = searchGoods.getImgUrl().split(",");
            if (imgArray != null) {
                num3 = imgArray.length;

                if (num3 == 1) {
                    fraction += 15;
                }
                if (num3 >= 6) {
                    fraction += 40;
                }
                if (num3 > 3 && num3 < 6) {
                    fraction += 30;
                }
            }
        }
        if (num <= 5 * 2) {
            fraction += 5;
        }
        if (num > 5 * 2 && num <= 10 * 2) {
            fraction += 10;
        }
        if (num > 10 * 2 && num <= 20 * 2) {
            fraction += 20;
        }
        if (num > 20 * 2) {
            fraction += 30;
        }

        if (num2 <= 20 * 2) {
            fraction += 5;
        }
        if (num2 > 20 * 2 && num2 <= 50 * 2) {
            fraction += 10;
        }
        if (num2 > 50 * 2 && num2 <= 80 * 2) {
            fraction += 20;
        }
        if (num2 > 80 * 2) {
            fraction += 30;
        }

        searchGoods.setAuditType(2);
        searchGoods.setDeleteType(1);
        searchGoods.setAfficheStatus(0);
        searchGoods.setFraction(fraction);
        searchGoods.setAddTime(new Date());
        searchGoods.setRefreshTime(new Date());
        searchGoodsService.add(searchGoods);

        //新增home
        IPS_Home ipsHome = new IPS_Home();
        if (fraction > 70) {
            ipsHome.setInfoId(searchGoods.getId());
            ipsHome.setTitle(searchGoods.getTitle());
            ipsHome.setUserId(searchGoods.getUserId());
            ipsHome.setContent(searchGoods.getContent());
            ipsHome.setMediumImgUrl(searchGoods.getImgUrl());
            ipsHome.setReleaseTime(searchGoods.getAddTime());
            ipsHome.setRefreshTime(searchGoods.getRefreshTime());
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setAfficheType(searchGoods.getSearchType());
            ipsHome.setFraction(fraction);

            //放入缓存
            redisUtils.addList(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome.getInfoId() + "_" + ipsHome.getAfficheType(), Constants.USER_TIME_OUT);
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + searchGoods.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除
     * @param id
     * @param userId 将要删除的userId
     * @return
     */
    @Override
    public ReturnData delMatter(@PathVariable long id, @PathVariable long userId) {
        //验证参数
        if (userId <= 0 || id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的婚恋交友信息", new JSONObject());
        }
        // 查询数据库
        SearchGoods posts = searchGoodsService.findUserById(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        posts.setDeleteType(2);
        searchGoodsService.update(posts);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新
     * @param searchGoods
     * @return
     */
    @Override
    public ReturnData updateMatter(@Valid @RequestBody SearchGoods searchGoods, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != searchGoods.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + searchGoods.getUserId() + "]的其他公告信息", new JSONObject());
        }
        //计算公告分数
        int num3 = 0;//图片
        int fraction = 0;//公告分数
        int num = CommonUtils.getStringLengsByByte(searchGoods.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(searchGoods.getContent());//内容
        if (!CommonUtils.checkFull(searchGoods.getImgUrl())) {
            String[] imgArray = searchGoods.getImgUrl().split(",");
            if (imgArray != null) {
                num3 = imgArray.length;

                if (num3 == 1) {
                    fraction += 15;
                }
                if (num3 >= 6) {
                    fraction += 40;
                }
                if (num3 > 3 && num3 < 6) {
                    fraction += 30;
                }
            }
        }
        if (num <= 5 * 2) {
            fraction += 5;
        }
        if (num > 5 * 2 && num <= 10 * 2) {
            fraction += 10;
        }
        if (num > 10 * 2 && num <= 20 * 2) {
            fraction += 20;
        }
        if (num > 20 * 2) {
            fraction += 30;
        }

        if (num2 <= 20 * 2) {
            fraction += 5;
        }
        if (num2 > 20 * 2 && num2 <= 50 * 2) {
            fraction += 10;
        }
        if (num2 > 50 * 2 && num2 <= 80 * 2) {
            fraction += 20;
        }
        if (num2 > 80 * 2) {
            fraction += 30;
        }
        //新增home
        IPS_Home ipsHome = new IPS_Home();
        if (fraction > 70) {
            ipsHome.setInfoId(searchGoods.getId());
            ipsHome.setTitle(searchGoods.getTitle());
            ipsHome.setUserId(searchGoods.getUserId());
            ipsHome.setContent(searchGoods.getContent());
            ipsHome.setMediumImgUrl(searchGoods.getImgUrl());
            ipsHome.setReleaseTime(searchGoods.getAddTime());
            ipsHome.setRefreshTime(searchGoods.getRefreshTime());
            ipsHome.setAuditType(2);
            ipsHome.setDeleteType(1);
            ipsHome.setAfficheType(searchGoods.getSearchType());
            ipsHome.setFraction(fraction);

            //放入缓存
            redisUtils.addList(Constants.REDIS_KEY_IPS_HOMELIST, ipsHome.getInfoId() + "_" + ipsHome.getAfficheType(), Constants.USER_TIME_OUT);
        }
        searchGoods.setFraction(fraction);
        searchGoods.setRefreshTime(new Date());
        searchGoodsService.update(searchGoods);

        if (!CommonUtils.checkFull(searchGoods.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            JSONObject root = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("interfaceType", "5");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发 3表示用户登录时同步登录信息 4表示新增访问量 5删除图片
            JSONObject content = new JSONObject();
            content.put("delImageUrls", searchGoods.getDelImgUrls());
            content.put("userId", searchGoods.getUserId());
            root.put("header", header);
            root.put("content", content);
            String sendMsg = root.toJSONString();
            ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
            mqProducer.sendMsg(activeMQQueue, sendMsg);
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + searchGoods.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * 查询
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData getMatter(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> otherPostsMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_SEARCHGOODS + id);
        if (otherPostsMap == null || otherPostsMap.size() <= 0) {
            SearchGoods posts = searchGoodsService.findUserById(id);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //放入缓存
            otherPostsMap = CommonUtils.objectToMap(posts);
            redisUtils.hmset(Constants.REDIS_KEY_IPS_SEARCHGOODS + id, otherPostsMap, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", otherPostsMap);
    }

    /***
     * 分页查询
     * @param province  省
     * @param city  市
     * @param district  区
     * @param beginAge  开始年龄
     * @param endAge  结束年龄
     * @param missingSex  失踪人性别:1男,2女
     * @param searchType  查找类别:0不限 ,1寻人,2寻物,3失物招领
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findMatterList(@PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int beginAge, @PathVariable int endAge, @PathVariable int missingSex, @PathVariable int searchType, @PathVariable int page, @PathVariable int count) {
        //验证地区正确性
        if (!CommonUtils.checkProvince_city_district(0, province, city, district)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (missingSex < 0 || missingSex > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "missingSex参数有误", new JSONObject());
        }
        if (searchType < 0 || searchType > 3) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "searchType参数有误", new JSONObject());
        }
        if (beginAge <= 0 || beginAge > endAge) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "searchType参数有误", new JSONObject());
        }
        if (endAge <= 0 || endAge < beginAge) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "searchType参数有误", new JSONObject());
        }
        //开始查询
        PageBean<SearchGoods> pageBean;
        pageBean = searchGoodsService.findList(province, city, district, beginAge, endAge, missingSex, searchType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /**
     * 更新公告状态
     *
     * @param id            主键ID
     * @param userId        用户ID
     * @param afficheStatus 0未解决  1已解决
     * @return
     */
    @Override
    public ReturnData updateState(@PathVariable long id, @PathVariable long userId, @PathVariable int afficheStatus) {
        //验证参数
        if (id <= 0 || userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + userId + "]的其他公告信息", new JSONObject());
        }
        SearchGoods posts = searchGoodsService.findUserById(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        posts.setAfficheStatus(afficheStatus);
        searchGoodsService.update(posts);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_SEARCHGOODS + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
