package com.busi.controller.api;

import com.busi.entity.SearchGoods;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/***
 * 寻人寻物失物招领相关接口
 * author：zhaojiajie
 * create time：2018-8-1 18:12:20
 */
public interface SearchGoodsApiController {
    /***
     * 新增
     * @param searchGoods
     * @param bindingResult
     * @return
     */
    @PostMapping("addMatter")
    ReturnData addMatter(@Valid @RequestBody SearchGoods searchGoods, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delMatter/{id}/{userId}")
    ReturnData delMatter(@PathVariable long id, @PathVariable long userId);

    /**
     * @Description: 更新
     * @Param: searchGoods
     * @return:
     */
    @PutMapping("updateMatter")
    ReturnData updateMatter(@Valid @RequestBody SearchGoods searchGoods, BindingResult bindingResult);

    /**
     * 查询
     *
     * @param id
     * @return
     */
    @GetMapping("getMatter/{id}")
    ReturnData getMatter(@PathVariable long id);

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
    @GetMapping("findMatterList/{{province}/}/{city}/{district}/{beginAge}/{endAge}/{missingSex}/{searchType}/{page}/{count}")
    ReturnData findMatterList(@PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int beginAge, @PathVariable int endAge, @PathVariable int missingSex, @PathVariable int searchType, @PathVariable int page, @PathVariable int count);
}
