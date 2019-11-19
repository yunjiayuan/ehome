package com.busi.dao;

import com.busi.entity.ShopFloor;
import com.busi.entity.YongHuiGoodsSort;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    @Insert("insert into ShopFloor(userId,shopName,shopHead,videoUrl,videoCoverUrl,content,payState,deleteType,addTime,lat,lon,address,villageName,villageOnly)" +
            "values (#{userId},#{shopName},#{shopHead},#{videoUrl},#{videoCoverUrl},#{content},#{payState},#{deleteType},#{addTime},#{lat},#{lon},#{address},#{villageName},#{villageOnly})")
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
            " villageOnly=#{villageOnly}," +
            " villageName=#{villageName}," +
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

    /***
     * 查询所有店铺
     * @return
     */
    @Select("select * from ShopFloor where deleteType=0")
    List<ShopFloor> find();

    /***
     * 新增永辉分类
     * @param homeShopCenter
     * @return
     */
    @Insert("insert into YongHuiGoodsSort(name,levelOne,levelTwo,letter,picture,enabled)" +
            "values (#{name},#{levelOne},#{levelTwo},#{letter},#{picture},#{enabled})")
    @Options(useGeneratedKeys = true)
    int addYHSort(YongHuiGoodsSort homeShopCenter);

    /***
     * 更新永辉分类
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update YongHuiGoodsSort set" +
            " name=#{name}," +
            " levelOne=#{levelOne}," +
            " levelTwo=#{levelTwo}," +
            " letter=#{letter}," +
            " picture=#{picture}," +
            " enabled=#{enabled}" +
            " where id=#{id}" +
            "</script>")
    int changeYHSort(YongHuiGoodsSort homeShopCenter);

    /***
     * 查询永辉分类
     * @param levelOne 商品1级分类  默认为0, -2为不限
     * @param levelTwo 商品2级分类  默认为0, -2为不限
     * @return
     */
    @Select("<script>" +
            "select * from YongHuiGoodsSort" +
            " where 1=1" +
            "<if test=\"levelOne >= 0 \">" +
            " and levelTwo > -1" +
            " and levelOne = #{levelOne}" +
            "</if>" +
            "<if test=\"levelOne == -2 \">" +
            " and levelOne > -1" +
            " and levelTwo = -1" +
            "</if>" +
            " and enabled = 0" +
            /*"<if test=\"levelTwo >= 0 \">" +
            " and levelTwo = #{levelTwo}" +
            "</if>" +
            "<if test=\"levelTwo == -2 \">" +
            " and levelTwo > -1" +
            "</if>" +*/
            "</script>")
    List<YongHuiGoodsSort> findYHSort(@Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo,@Param("letter") String letter);


    /***
     * 删除永辉分类
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from YongHuiGoodsSort" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delYHSort(@Param("ids") String[] ids);
}
