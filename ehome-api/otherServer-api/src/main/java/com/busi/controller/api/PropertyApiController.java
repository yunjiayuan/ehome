package com.busi.controller.api;

import com.busi.entity.PropertyResident;
import com.busi.entity.Property;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 物业相关接口
 * author：ZJJ
 * create time：2020-04-07 16:49:44
 */
public interface PropertyApiController {

    /***
     * 查询是否已加入物业
     * @param userId
     * @return
     */
    @GetMapping("findJoinProperty/{userId}")
    ReturnData findJoinProperty(@PathVariable long userId);

    /***
     * 新增物业
     * @param homeHospital
     * @return
     */
    @PostMapping("addProperty")
    ReturnData addProperty(@Valid @RequestBody Property homeHospital, BindingResult bindingResult);

    /***
     * 更新物业
     * @param homeHospital
     * @return
     */
    @PutMapping("changeProperty")
    ReturnData changeProperty(@Valid @RequestBody Property homeHospital, BindingResult bindingResult);

    /***
     * 查询物业详情
     * @param id
     * @return
     */
    @GetMapping("findProperty/{id}")
    ReturnData findProperty(@PathVariable long id);

    /***
     * 查询物业列表
     * @param userId    用户ID
     * @param lon     经度
     * @param lat     纬度
     * @param string    模糊搜索
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findPropertyList/{userId}/{lon}/{lat}/{string}/{province}/{city}/{district}/{page}/{count}")
    ReturnData findPropertyList(@PathVariable long userId, @PathVariable double lon, @PathVariable double lat, @PathVariable String string, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count);

    /***
     * 新增居民
     * @param homeHospital
     * @return
     */
    @PostMapping("addPResident")
    ReturnData addPResident(@Valid @RequestBody PropertyResident homeHospital, BindingResult bindingResult);

    /***
     * 更新居民权限
     * @param homeHospital
     * @return
     */
    @PutMapping("changePResident")
    ReturnData changePResident(@Valid @RequestBody PropertyResident homeHospital, BindingResult bindingResult);

    /***
     * 删除居民
     * @param type 0删除居民  1删除管理员
     * @return:
     */
    @DeleteMapping("delPResident/{type}/{ids}/{propertyId}")
    ReturnData delPResident(@PathVariable int type, @PathVariable String ids, @PathVariable long propertyId);


    /***
     * 查询居民详情
     * @param propertyId
     * @return
     */
    @GetMapping("findPResiden/{propertyId}/{homeNumber}")
    ReturnData findPResiden(@PathVariable long propertyId, @PathVariable String homeNumber);

    /***
     * 查询居民列表
     * @param type    0所有人  1管理员
     * @param propertyId    物业ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findPResidentList/{type}/{propertyId}/{page}/{count}")
    ReturnData findPResidentList(@PathVariable int type, @PathVariable long propertyId, @PathVariable int page, @PathVariable int count);

}
