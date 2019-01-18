package com.busi.dao;

import com.busi.entity.RedBagRain;
import com.busi.entity.Task;
import com.busi.entity.TaskList;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务Dao
 * author：zhaojiajie
 * create time：2018-8-15 16:57:56
 */
@Mapper
@Repository
public interface TaskDao {

    /***
     * 新增红包雨记录
     * @param redBagRain
     * @return
     */
    @Insert("insert into RedBagRain(userId,pizeType,quota,time) " +
            "values (#{userId},#{pizeType},#{quota},#{time})")
    @Options(useGeneratedKeys = true)
    int addRain(RedBagRain redBagRain);

    /***
     * 删除过期中奖人员数据
     * @return
     */
    @Delete("<script>" +
            "delete from RedBagRain" +
            " where userId >= 13870 and userId &lt;= 53870" +
            "</script>")
    int batchDel();

}
