package com.busi.dao;

import com.busi.entity.ShopFloor;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @program: ehome
 * @description: 楼店
 * @author: ZHaoJiaJie
 * @create: 2019-11-12 16:53
 */
@Mapper
@Repository
public interface ShopFloorDao {

    /***
     * 新增楼店
     * @param homeShopCenter
     * @return
     */
    @Insert("insert into ShopFloor(userId,shopName,shopHead,videoUrl,videoCoverUrl,content,payState,deleteType,addTime,lat,lon,address)" +
            "values (#{userId},#{shopName},#{shopHead},#{videoUrl},#{videoCoverUrl},#{content},#{payState},#{deleteType},#{addTime},#{lat},#{lon},#{address})")
    @Options(useGeneratedKeys = true)
    int addHomeShop(ShopFloor homeShopCenter);

    /***
     * 更新楼店
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloor set" +
            " shopName=#{shopName}," +
            " shopHead=#{shopHead}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " content=#{content}," +
            " address=#{address}," +
            " lon=#{lon}," +
            " lat=#{lat}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateHomeShop(ShopFloor homeShopCenter);

    /***
     * 更新楼店保证金支付状态
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloor set" +
            " payState=#{payState}" +
            " where userId=#{userId}" +
            "</script>")
    int updatePayStates(ShopFloor homeShopCenter);

    /***
     * 更新楼店营业状态
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloor set" +
            " shopState=#{shopState}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBusiness(ShopFloor homeShopCenter);

    /***
     * 根据用户ID查询楼店状态
     * @param userId
     * @return
     */
    @Select("select * from ShopFloor where userId=#{userId} and deleteType=0")
    ShopFloor findByUserId(@Param("userId") long userId);

}
