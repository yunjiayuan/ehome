package com.busi.utils;

import com.busi.entity.Footmark;
import com.busi.fegin.FootmarkLControllerFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 足迹相关
 * @author: ZHaoJiaJie
 * @create: 2019-03-27 13:56
 */
@Component
public class FootmarkUtils {

    @Autowired
    FootmarkLControllerFegin footmarkLControllerFegin;

    /**
     * @program: ehome
     * @description: 更新足迹
     * @author: ZHaoJiaJie
     * @create: 2019-3-25 13:35:52
     */
    public Footmark updateFootmark(@RequestBody Footmark footmark) {

        footmarkLControllerFegin.updateFootmark(footmark);

        return footmark;
    }

    /**
     * @Description: 删除
     * @return:
     */
    public Footmark delFootmarkPad(@RequestBody Footmark footmark) {

        footmarkLControllerFegin.delFootmarkPad(footmark);

        return footmark;
    }

}
