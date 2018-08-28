package com.busi.service;

import com.busi.dao.CollectDao;
import com.busi.entity.Collect;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @program: 收藏
 * @author: ZHaoJiaJie
 * @create: 2018-08-24 16:12
 */
@Service
public class CollectService {

    @Autowired
    private CollectDao collectDao;

    /***
     * 新增
     * @param collect
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( Collect collect){
        return collectDao.add(collect);
    }

    /***
     * 删除
     * @param ids
     * @param userId
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int del(String[] ids ,long userId){
        return collectDao.del(ids,userId);
    }

    /***
     * 统计收藏次数
     * @param infoId
     * @param afficheType
     * @return
     */
    public int findUserById(long infoId ,int afficheType){
        return collectDao.findUserById(infoId,afficheType);
    }


    /***
     * 分页查询
     * @param myId 用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<Collect> findList(long myId, int page, int count) {

        List<Collect> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = collectDao.findList(myId);

        return PageUtils.getPageBean(p,list);
    }
}
