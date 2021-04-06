package com.busi.service;

import com.busi.dao.RentAhouseDao;
import com.busi.entity.LoveAndFriends;
import com.busi.entity.PageBean;
import com.busi.entity.RentAhouse;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 租房买房
 * @author: ZHaoJiaJie
 * @create: 2020-04-20 21:47:23
 */
@Service
public class RentAhouseService {

    @Autowired
    private RentAhouseDao kitchenBookedDao;

    /***
     * 新增房源
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCommunity(RentAhouse kitchenBooked) {
        return kitchenBookedDao.addCommunity(kitchenBooked);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public RentAhouse findRentAhouse(long id) {
        return kitchenBookedDao.findByUserId(id);
    }

    /***
     * 删除房源
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delResident(String[] ids, long userId) {
        return kitchenBookedDao.delDishes(ids, userId);
    }

    /***
     * 更新房源
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeCommunity(RentAhouse kitchenBooked) {
        return kitchenBookedDao.updateBooked(kitchenBooked);
    }

    /***
     * 更新房源状态
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeCommunityState(RentAhouse kitchenBooked) {
        return kitchenBookedDao.changeCommunityState(kitchenBooked);
    }

    /***
     * 条件查询房源
     * @param userId    用户ID
     * @param sellState  -1不限 roomState=0时：0出售中  1已售出  roomState=1时：0出租中  1已出租
     * @param roomState  -1不限 0出售  1出租
     * @param sort  排序条件:0最新发布，1价格最低，2价格最高
     * @param nearby  附近 -1不限  0附近
     * @param residence     房型：-1不限 0一室 1二室 2三室 3四室 4五室及以上
     * @param roomType     房屋类型 roomState=0时：-1不限 0新房 1二手房   roomState=1时：-1不限 0合租 1整租
     * @param lon     经度  nearby=0时有效
     * @param lat     纬度  nearby=0时有效
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
     * @param lookHomeTime  看房时间 ： -1不限 0随时看房 1 周末看房  2下班后看房  3电话预约
     * @param string    模糊搜索
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<RentAhouse> findRentAhouseList(long userId, int sellState, int roomState, int sort, int nearby, int residence, int roomType, double lon, double lat, int province, int city,
                                                   int district, int minPrice, int maxPrice, int minArea, int maxArea, int orientation, int renovation, int floor, int bedroomType,
                                                   int houseType, int paymentMethod, int lookHomeTime, String string, int page, int count) {

        List<RentAhouse> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(string)) {
            string = null;
        }
        list = kitchenBookedDao.findRentAhouseList(userId, sellState, roomState, sort, nearby, residence, roomType, lon,
                lat, province, city, district, minPrice, maxPrice, minArea, maxArea, orientation,
                renovation, floor, bedroomType, houseType, paymentMethod, lookHomeTime, string);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * home推荐列表用
     * @param userId   用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<RentAhouse> findHList(long userId, int page, int count) {

        List<RentAhouse> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = kitchenBookedDao.findHList(userId);

        return PageUtils.getPageBean(p, list);
    }
}
