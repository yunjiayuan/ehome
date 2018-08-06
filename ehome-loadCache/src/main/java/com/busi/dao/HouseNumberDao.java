package com.busi.dao;

import com.busi.entity.HouseNumber;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 门牌号DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface HouseNumberDao {

    /***
     * 查询门牌号记录表
     * @return
     */
    @Select("select * from HouseNumber")
    List<HouseNumber> find();

}
