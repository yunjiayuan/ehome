package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import com.busi.entity.Test;

/***
 * Test
 * author：zhaojiajie
 * create time：2018-7-24 16:56:12
 */
public interface TestApiController {

    /***
     * 新增
     * @param test
     * @param bindingResult
     * @return
     */
    @PostMapping("testAdd")
    ReturnData testAdd(@Valid @RequestBody Test test,BindingResult bindingResult);

    /**
     *@Description: 删除
     *@return:
     */
    @DeleteMapping("delTest/{id}")
    ReturnData delTest(@PathVariable  long id);

    /**
     *@Description: 更新
     *@Param: test
     *@return:
     */
    @PutMapping("updateTest")
    ReturnData updateTest(@Valid @RequestBody Test test, BindingResult bindingResult);

    /**
     * 查询
     * @param id
     * @return
     */
    @GetMapping("getTest/{id}")
    ReturnData getTest(@PathVariable  long id);

    /***
     * 分页查询接口
     * @param page
     * @param count
     * @return
     */
    @GetMapping("findList/{page}/{count}")
    ReturnData findList(@PathVariable int page, @PathVariable int count);
}
