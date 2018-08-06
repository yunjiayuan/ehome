package com.busi.controller.api;


import com.busi.entity.Demo;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.*;


/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/5/28 19:04
 */
public interface TestApiController {

    /***
     * 新增demo
     * @param demo
     * @return
     */
    @PostMapping("add")
    ReturnData add(@RequestBody Demo demo);

    /***
     * 查询Demo
     * @return
     */
    @GetMapping("findDemoById/{id}")
    ReturnData findDemoById(@PathVariable long id);

    /***
     * 查询列表
     * @return
     */
    @GetMapping("findList/{page}/{count}")
    ReturnData findList(@PathVariable int page,@PathVariable int count);

    /***
     * 修改
     * @return
     */
    @PutMapping("update")
    ReturnData update(@RequestBody Demo demo);

    /***
     * 删除
     * @return
     */
    @DeleteMapping("delete/{id}")
    ReturnData delete(@PathVariable long id);
}
