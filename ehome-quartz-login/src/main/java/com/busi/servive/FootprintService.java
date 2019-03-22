package com.busi.servive;

import com.busi.dao.FootprintDao;
import com.busi.entity.Footprint;
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
     * 查询离开时间为空的
     * @return
     */
    public List<Footprint> find() {
        List<Footprint> list;
        list = footprintDao.find();
        return list;
    }

    /***
     * 更新离开时间
     * @param footprint
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(Footprint footprint) {
        return footprintDao.update(footprint);
    }
}
