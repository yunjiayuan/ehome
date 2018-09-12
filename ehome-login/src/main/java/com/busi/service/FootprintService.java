package com.busi.service;

import com.busi.dao.FootprintDao;
import com.busi.entity.Footprint;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 脚印Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class FootprintService {

    @Autowired
    private FootprintDao footprintDao;

    /***
     * 新增脚印
     * @param footprint
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(Footprint footprint){
        return  footprintDao.add(footprint);
    }

    /***
     * 更新离开时间
     * @param footprint
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(Footprint footprint){
        return  footprintDao.update(footprint);
    }

    /***
     * 查询脚印记录和在线的人
     * @param userId       当前登录者ID
     * @param findType     历史脚印查询类型 当isOnlineType=1时有效 0查询自己被访问过的脚印记录  1查询自己访问过的脚印记录
     * @param isOnlineType 查询类型  0表示查询当时正在家的人  1表示查询历史脚印记录
     * @param page
     * @param count
     * @return
     */
    public PageBean<Footprint> findFootList(long userId, int findType, int isOnlineType,int page, int count){
        List<Footprint> list;
        Page p;
        if(isOnlineType==0){
            p = PageHelper.startPage(1,10);//为此行代码下面的第一行sql查询结果进行分页
            list = footprintDao.findOnlineList(userId);
        }else{
            p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
            list = footprintDao.findFootList(userId,findType);
        }
        return PageUtils.getPageBean(p,list);
    }



}
