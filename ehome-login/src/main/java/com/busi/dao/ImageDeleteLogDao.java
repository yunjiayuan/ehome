package com.busi.dao;

import com.busi.entity.ImageDeleteLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 图片删除记录DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface ImageDeleteLogDao {

    /***
     * 新增
     * @param imageDeleteLog
     * @return
     */
    @Insert("insert into ImageDeleteLog(imageUrl,time) values (#{imageUrl},#{time})")
    @Options(useGeneratedKeys = true)
    int add(ImageDeleteLog imageDeleteLog);

    /***
     * 删除
     * @param id
     * @return
     */
    @Delete(("delete from ImageDeleteLog where id=#{id}"))
    int delete(@Param("id") long id);

    /***
     * 查询7天之前的数据列表
     * @return
     */
    @Select(("select * from ImageDeleteLog where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(time)"))
    List<ImageDeleteLog> findImageDeleteLogList();

}
