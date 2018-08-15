package com.busi.service;

import com.busi.dao.LoveAndFriendsDao;
import com.busi.entity.PageBean;
import com.busi.entity.LoveAndFriends;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * 婚恋交友Service
 * author：zhaojiajie
 * create time：2018-8-2 11:24:17
 */
@Service
public class LoveAndFriendsService {

    @Autowired
    private LoveAndFriendsDao loveAndFriendsDao;
    /***
     * 新增用户
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( LoveAndFriends loveAndFriends){
        return loveAndFriendsDao.add(loveAndFriends);
    }

    /***
     * 删除
     * @param userId 将要删除的userId
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int del(long userId){
        return loveAndFriendsDao.del(userId);
    }

    /***
     * 更新
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(LoveAndFriends loveAndFriends){
        return  loveAndFriendsDao.update(loveAndFriends);
    }

    /***
     * 更新删除状态
     * @param loveAndFriends
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateDel(LoveAndFriends loveAndFriends){
        return  loveAndFriendsDao.updateDel(loveAndFriends);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public LoveAndFriends findUserById(long id){
        return loveAndFriendsDao.findUserById(id);
    }

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    public LoveAndFriends findByIdUser(long userId){
        return loveAndFriendsDao.findByIdUser(userId);
    }

    /***
     * 分页条件查询
     * @param sex   性别:0不限，1男，2女
     * @param income 收入:0不限，1（<3000），2（3000-5000），3（5000-7000），4（7000-9000），5（9000-12000），6（12000-15000），7（15000-20000），8（>20000）
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<LoveAndFriends> findList(int sex, int income, int page, int count) {

        List<LoveAndFriends> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = loveAndFriendsDao.findList(sex,income);

        return PageUtils.getPageBean(p,list);
    }
}
