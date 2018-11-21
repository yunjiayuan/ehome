package com.busi.service;

import com.busi.dao.OnlineMusicDao;
import com.busi.entity.OnlineMusic;
import com.busi.entity.PageBean;
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
 * @description: 在线音乐
 * @author: ZHaoJiaJie
 * @create: 2018-10-31 14:19
 */
@Service
public class OnlineMusicService {

    @Autowired
    private OnlineMusicDao onlineMusicDao;

    /***
     * 新增歌曲
     * @param onlineMusic
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(OnlineMusic onlineMusic) {
        return onlineMusicDao.add(onlineMusic);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public OnlineMusic findMusic(long id) {
        return onlineMusicDao.findMusic(id);
    }

    /***
     * 查询歌曲列表
     * @param name  歌名或歌手
     * @param songType 歌曲类型：0.热歌榜 1.流行 2.纯音乐 3.摇滚 4.神曲 5.DJ 6.电音趴 7.说唱 8.国风 9.欧美（PS：仅在name为空时有效）
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<OnlineMusic> findPaging(int songType, String name, int page, int count) {

        List<OnlineMusic> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(name)) {
            list = onlineMusicDao.findPaging2(songType);
        } else {
            list = onlineMusicDao.findPaging(name);
        }
        return PageUtils.getPageBean(p, list);
    }

}
