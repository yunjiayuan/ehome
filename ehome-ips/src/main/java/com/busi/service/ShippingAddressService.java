package com.busi.service;

import com.busi.dao.ShippingAddressDao;
import com.busi.entity.PageBean;
import com.busi.entity.ShippingAddress;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 收货地址
 * @author: ZHaoJiaJie
 * @create: 2018-09-20 15:13
 */
@Service
public class ShippingAddressService {
    @Autowired
    private ShippingAddressDao shippingAddressDao;

    /***
     * 新增
     * @param shippingAddress
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(ShippingAddress shippingAddress) {
        return shippingAddressDao.add(shippingAddress);
    }

    /***
     * 更新
     * @param shippingAddress
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(ShippingAddress shippingAddress) {
        return shippingAddressDao.update(shippingAddress);
    }

    /***
     * 更新删除状态
     * @param shippingAddress
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(ShippingAddress shippingAddress) {
        return shippingAddressDao.updateDel(shippingAddress);
    }

    /***
     * 更新默认地址
     * @param shippingAddress
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDefault(ShippingAddress shippingAddress) {
        return shippingAddressDao.updateDefault(shippingAddress);
    }


    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public ShippingAddress findUserById(long id) {
        return shippingAddressDao.findAppoint(id);
    }

    /***
     * 查询默认收货地址
     * @return
     */
    public ShippingAddress findDefault(long userId) {
        return shippingAddressDao.findDefault(userId);
    }

    /***
     * 分页查询收货地址
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<ShippingAddress> findAoList(long userId, int page, int count) {

        List<ShippingAddress> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shippingAddressDao.findAoList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计该用户地址数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId) {
        return shippingAddressDao.findNum(userId);
    }

    /***
     * 查询我的收货地址
     * @param userId
     * @return
     */
    public List<ShippingAddress> findList(long userId) {
        List<ShippingAddress> list;
        list = shippingAddressDao.findList(userId);
        return list;
    }

}
