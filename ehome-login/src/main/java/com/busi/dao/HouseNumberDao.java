package com.busi.dao;

import com.busi.entity.HouseNumber;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 门牌号DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface HouseNumberDao {

    /***
     * 更新用户信息
     * @param houseNumber
     * @return
     */
    @Update(("update HouseNumber set newNumber = #{newNumber} where proKeyWord = #{proKeyWord}"))
    int update(HouseNumber houseNumber);

}
