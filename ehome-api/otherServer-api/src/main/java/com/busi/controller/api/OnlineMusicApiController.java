package com.busi.controller.api;

import com.busi.entity.OnlineMusic;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/***
 * 在线音乐相关接口
 * author：zhaojiajie
 * create time：2018-10-31 13:05:49
 */
public interface OnlineMusicApiController {

    /***
     * 新增音乐
     * @param onlineMusic
     * @return
     */
    @PostMapping("addMusic")
    ReturnData addMusic(@Valid @RequestBody OnlineMusic onlineMusic, BindingResult bindingResult);

    /***
     * 查询详情
     * @return
     */
    @GetMapping("findMusic/{id}")
    ReturnData findMusic(@PathVariable long id);

    /***
     * 查询歌曲列表
     * @param name  歌名或歌手
     * @param songType 歌曲类型：0.热歌榜 1.流行 2.纯音乐 3.摇滚 4.神曲 5.DJ 6.电音趴 7.说唱 8.国风 9.欧美（PS：仅在name为空时有效）
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findMusicList/{songType}/{name}/{page}/{count}")
    ReturnData findMusicList(@PathVariable int songType, @PathVariable String name, @PathVariable int page, @PathVariable int count);

}
