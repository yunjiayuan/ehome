package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.ShippingAddress;
import com.busi.service.ShippingAddressService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 收货地址相关接口实现
 * @author: ZHaoJiaJie
 * @create: 2018-09-20 13:00
 */
@RestController
public class ShippingAddressController extends BaseController implements ShippingAddressApiController {

    @Autowired
    ShippingAddressService shippingAddressService;

    /***
     * 新增收货地址
     * @param shippingAddress
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addAddress(@Valid @RequestBody ShippingAddress shippingAddress, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, shippingAddress.getProvince(), shippingAddress.getCity(), shippingAddress.getDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        //判断该用户是否达到地址个数上限 最多10条
        int num = shippingAddressService.findNum(CommonUtils.getMyId());
        if (num >= 10) {
            return returnData(StatusCode.CODE_IPS_SHIPPINGADDRESS_TOPLIMIT.CODE_VALUE, "新增收货地址数量超过上限,拒绝新增！", new JSONObject());
        }
        shippingAddress.setAddressState(0);
        shippingAddress.setAddTime(new Date());
        shippingAddress.setRefreshTime(new Date());
        if (num <= 0) {//判断是不是首增收货地址（默认第一条为默认收货地址）
            shippingAddress.setDefaultAddress(1);
        } else {
            if (shippingAddress.getDefaultAddress() == 1) {
                //设置前重置默认地址(替换上次默认地址设置)
                ShippingAddress address = null;
                address = shippingAddressService.findDefault(CommonUtils.getMyId());
                if (address != null) {
                    address.setDefaultAddress(0);
                    shippingAddressService.updateDefault(address);
                }
            }
        }
        shippingAddressService.add(shippingAddress);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除收货地址
     * @return:
     */
    @Override
    public ReturnData delAddress(@PathVariable long id, @PathVariable long userId) {
        //验证参数
        if (userId <= 0 || id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的公告信息", new JSONObject());
        }
        ShippingAddress s = null;
        ShippingAddress lf = shippingAddressService.findUserById(id);
        if (lf != null) {
            if (lf.getDefaultAddress() == 1) {//判断要删除的是不是默认地址
                lf.setAddressState(1);
                shippingAddressService.updateDel(lf);
                List addressList = shippingAddressService.findList(userId);
                if (addressList != null && addressList.size() > 0) {
                    s = (ShippingAddress) addressList.get(0);
                    s.setDefaultAddress(1);//设置新的默认地址
                    shippingAddressService.updateDefault(s);
                }
            } else {
                lf.setAddressState(1);
                shippingAddressService.updateDel(lf);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新收货地址
     * @Param: shippingAddress
     * @return:
     */
    @Override
    public ReturnData updateAddress(@Valid @RequestBody ShippingAddress shippingAddress, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != shippingAddress.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + shippingAddress.getUserId() + "]的公告信息", new JSONObject());
        }
        // 查询数据库
        ShippingAddress posts = shippingAddressService.findUserById(shippingAddress.getId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }

        shippingAddress.setRefreshTime(new Date());
        if (shippingAddress.getDefaultAddress() == 1) {
            //设置前重置默认地址(替换上次默认地址设置)
            ShippingAddress address = null;
            address = shippingAddressService.findDefault(CommonUtils.getMyId());
            if (address != null) {
                address.setDefaultAddress(0);
                shippingAddressService.updateDefault(address);
            }
        }
        shippingAddressService.update(shippingAddress);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * 查询收货地址详情
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData getAddress(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        ShippingAddress is = shippingAddressService.findUserById(id);
        if (is == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", is);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * 查询默认收货地址
     *
     * @return
     */
    @Override
    public ReturnData getDefault() {
        ShippingAddress is = shippingAddressService.findDefault(CommonUtils.getMyId());
        if (is == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", is);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 分页查询收货地址
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findAddressList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ShippingAddress> pageBean = null;
        pageBean = shippingAddressService.findAoList(CommonUtils.getMyId(), page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /**
     * @Description: 设置默认收货地址
     * @Param: id  ID
     * @return:
     */
    @Override
    public ReturnData setDefault(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        ShippingAddress h = shippingAddressService.findUserById(id);
        if (h != null) {
            ShippingAddress address = shippingAddressService.findDefault(CommonUtils.getMyId());
            //设置前重置默认地址(清除上次默认地址设置)
            if (address != null) {
                address.setDefaultAddress(0);
                shippingAddressService.updateDefault(address);
            }
            h.setDefaultAddress(1);
            shippingAddressService.updateDefault(h);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
