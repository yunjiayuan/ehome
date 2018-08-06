package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.TestService;
import com.busi.utils.*;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.busi.entity.Test;

/***
 * Test
 * author：zhaojiajie
 * create time：2018-7-24 16:56:29
 */
@RestController
public class TestController extends BaseController implements TestApiController {

    @Autowired
    TestService testService;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 新增
     * @param test
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData testAdd(@Valid @RequestBody Test test, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        test.setTime(new Date());
        testService.add(test);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 删除
     * @param id 将要删除的ID
     * @return
     */
    @Override
    public ReturnData delTest(@PathVariable long id) {
        if(id<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"ID参数有误",new JSONObject());
        }
        testService.del(id);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),1);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 更新
     * @param test
     * @return
     */
    @Override
    public ReturnData updateTest(@Valid @RequestBody Test test, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        testService.update(test);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),1);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /**
     * 查询
     * @param id
     * @return
     */
    @Override
    public ReturnData getTest(@PathVariable  long id) {
        if(id<=0){
            returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"id参数有误",new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String,Object> testMap =  redisUtils.hmget("test_"+id);
        if(testMap==null||testMap.size()<=0){
            Test test = testService.findUserById(id);
            if(test==null){
                returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"id参数有误",new JSONObject());
            }
            //放入缓存
            redisUtils.hmset("test_"+id,CommonUtils.objectToMap(test),Constants.USER_TIME_OUT);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",test);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",testMap);
    }

    /***
     * 获取列表接口
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findList(@PathVariable int page,@PathVariable int count) {
        //验证参数
        if(page<0||count<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"分页参数有误",new JSONObject());
        }
        //从缓存中获取列表
        PageBean<Test> pageBean = null;
        List<Object> list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),0,-1);
        if(list==null||list.size()<=0){
            //缓存中不存在 查询数据库 并同步到缓存中
            pageBean  = testService.findList(CommonUtils.getMyId(),page, count);
            redisUtils.pushList(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),pageBean.getList(),Constants.USER_TIME_OUT);
        }
        if(pageBean==null){
            Page p = new Page();
            p.setTotal(list.size());
            p.setPageNum(page);
            p.setPages(1);
            p.setPageSize(count);
            p.setPageSize(list.size());
            pageBean=PageUtils.getPageBean(p,list);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }

}
