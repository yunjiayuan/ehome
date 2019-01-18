package com.busi.servive;

import com.busi.dao.TaskDao;
import com.busi.entity.RedBagRain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: 任务
 * @author: ZHaoJiaJie
 * @create: 2018-08-15 17:49
 */
@Service
public class TaskService {

    @Autowired
    private TaskDao taskDao;

    /***
     * 新增红包雨记录
     * @param redBagRain
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addRain(RedBagRain redBagRain) {
        return taskDao.addRain(redBagRain);
    }

    /***
     * 删除过期中奖人员数据
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int batchDel() {
        return taskDao.batchDel();
    }

}
