package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 周公解梦相关接口
 * author：zhaojiajie
 * create time：2020-10-30 15:44:22
 */
public interface ZhouGongDreamApiController {

    /***
     * 查询剩余次数
     * @return
     */
    @GetMapping("findDreamNum")
    ReturnData findDreamNum();

    /***
     * 查询解梦详情
     * @param id  签子ID
     * @return
     */
    @GetMapping("findDreams/{id}")
    ReturnData findDreams(@PathVariable long id);

    /***
     * 查询二级分类
     * @param biglx 大分类
     * @return
     */
    @GetMapping("findDreamsTwoSort/{biglx}")
    ReturnData findDreamsTwoSort(@PathVariable String biglx);

    /***
     * 条件查询
     * @param title 关键字
     * @param biglx 大分类
     * @param smalllx 小分类
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findDreamsSortList/{title}/{biglx}/{smalllx}/{page}/{count}")
    ReturnData findDreamsSortList(@PathVariable String title, @PathVariable String biglx, @PathVariable String smalllx, @PathVariable int page, @PathVariable int count);

    /***
     * 查询解梦记录
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findDreamsList/{userId}/{page}/{count}")
    ReturnData findDreamsList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);
}
