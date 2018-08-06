package com.busi.dao;

import com.busi.entity.PickNumber;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 靓号、预售账号、预选账号、VIP账号记录 DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface PickNumberDao {

    /***
     * 查询靓号、预售账号、预选账号、VIP账号记录
     * @return
     */
    @Select("select * from pickNumber")
    List<PickNumber> find();

}
