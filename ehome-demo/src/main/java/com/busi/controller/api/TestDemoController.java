package com.busi.controller.api;


import com.busi.controller.BaseController;
import com.busi.entity.Demo;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.fegin.LoginControllerFegin;
import com.busi.service.DemoService;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 具体业务实现 Controller层
 * author：SunTianJie
 * create time：2018/5/29 14:24
 */
@RestController //此处必须继承BaseController和实现项目对应的接口TestApiController
public class TestDemoController extends BaseController implements TestApiController  {

    @Autowired
    private DemoService demoService;
//    @Autowired
//    private LoginControllerFegin loginControllerFegin;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 新增
     * @param demo
     * @return
     */
    @Override
    public ReturnData add(@RequestBody Demo demo) {
        //操作数据 及其他业务
        demoService.add(demo);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,demo);//返回成功
    }

    /***
     * 查询demo信息
     * @param id
     * @return
     */
    @Override
    public ReturnData findDemoById(@PathVariable long id) {
        String [] array = {"houseNumber","name","password"};
        List<Object> list = redisUtils.multiGet("user_17",array);
        Demo demo = demoService.findDemoById(id);
        if(demo==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,"{}");
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,demo);
    }
    /***
     * 更新
     * @param demo
     * @return
     */
    @Override
    public ReturnData update(@RequestBody Demo demo) {
        demoService.update(demo);
        return returnData();
    }

    /***
     * 删除
     * @param id
     * @return
     */
    @Override
    public ReturnData delete(@PathVariable long id) {
        //删除业务
        demoService.delete(id);
        return returnData();
    }

    /***
     * 查询列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findList(@PathVariable int page,@PathVariable int count) {
        PageBean<Demo> pageBean;
        pageBean  = demoService.findList(page,count);
        if(pageBean==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,"[]");
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }

//    /***
//     * fegin调用其他项目api
//     */
//    @GetMapping("/demo/feginTest")
//    public ReturnData feginTest(){
//        loginControllerFegin.testFegin(1);
//        return returnData();
//    }

}
