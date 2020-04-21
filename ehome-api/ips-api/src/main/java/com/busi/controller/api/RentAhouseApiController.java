package com.busi.controller.api;

import com.busi.entity.RentAhouse;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 租房买房相关接口
 * author ZhaoJiaJie
 * Create time 2020-04-20 11:34:02
 */
public interface RentAhouseApiController {
    /***
     * 新增房源
     * @param homeHospital
     * @return
     */
    @PostMapping("addRentAhouse")
    ReturnData addRentAhouse(@Valid @RequestBody RentAhouse homeHospital, BindingResult bindingResult);

    /***
     * 更新房源
     * @param homeHospital
     * @return
     */
    @PutMapping("changeRentAhouse")
    ReturnData changeRentAhouse(@Valid @RequestBody RentAhouse homeHospital, BindingResult bindingResult);

    /***
     * 删除房源
     * @param ids 房源ID
     * @return:
     */
    @DeleteMapping("delRentAhouse/{ids}")
    ReturnData delRentAhouse(@PathVariable String ids);

    /***
     * 查询房源详情
     * @param id 房源ID
     * @return
     */
    @GetMapping("findRentAhouse/{id}")
    ReturnData findRentAhouse(@PathVariable long id);

    /***
     * 条件查询房源
     * @param userId    用户ID
     * @param sellState  -1不限 leaseState=0时：0出售中  1已售出  leaseState=1时：0出租中  1已出租
     * @param leaseState  -1不限 0出售  1出租
     * @param sort  排序条件:0最新发布，1价格最低，2价格最高
     * @param nearby  附近 0不限  1附近
     * @param roomType     房型：0不限 1一室 2二室 3三室 4四室 5五室及以上
     * @param lon     经度
     * @param lat     纬度
     * @param province     省
     * @param city      市
     * @param district    区
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param minArea  最小面积
     * @param maxArea  最大面积
     * @param orientation  朝向：-1不限 0南北、1东北、2东南、3西南、4西北、5东西、6南、7东、8西、9北
     * @param renovation   房屋装修：-1不限 0精装 1普装 2毛坯
     * @param floor   房屋楼层：-1不限 0低楼层 1中楼层 2高楼层
     * @param bedroomType   卧室类型：-1不限 0主卧 1次卧 2其他
     * @param houseType  房源类型: -1不限 0业主直租 1中介
     * @param paymentMethod  支付方式: -1不限  0押一付一 1押一付三 2季付 3半年付 4年付
     * @param openHome  看房时间
     * @param string    模糊搜索
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findRentAhouseList/{userId}/{sellState}/{leaseState}/{sort}/{nearby}/{roomType}/{lon}/{lat}/{province}/{city}/{district}/{minPrice}/{maxPrice}/{minArea}/{maxArea}/{orientation}/{renovation}/{floor}/{bedroomType}/{houseType}/{paymentMethod}/{openHome}/{string}/{page}/{count}")
    ReturnData findRentAhouseList(@PathVariable long userId, @PathVariable int sellState, @PathVariable int leaseState, @PathVariable int sort, @PathVariable int nearby, @PathVariable int roomType, @PathVariable double lon, @PathVariable double lat, @PathVariable int province, @PathVariable int city,
                                  @PathVariable int district, @PathVariable int minPrice, @PathVariable int maxPrice, @PathVariable double minArea, @PathVariable double maxArea, @PathVariable int orientation, @PathVariable int renovation, @PathVariable int floor, @PathVariable int bedroomType,
                                  @PathVariable int houseType, @PathVariable int paymentMethod, @PathVariable int openHome, @PathVariable String string, @PathVariable int page, @PathVariable int count);

}
