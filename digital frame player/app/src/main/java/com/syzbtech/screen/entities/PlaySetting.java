package com.syzbtech.screen.entities;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2021-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PlaySetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long deviceId;

    private Long userId;

    private Integer playTime;

    /**
     * 播放时间选择方式，1-系统，2-自定义
     */
    private Integer playTimeType;

    /**
     * 发布方式,1-覆盖，2-追加
     */
    private Integer publishType;
    /**
     * 播放方式，1-全屏播放，2-原始尺寸
     */
    private Integer playType;
    /**
     * 优先顺序,1-视频， 2-图片
     */

    private Integer priorityOrder;
    /**
     * 日期时间,1-不显示，2-显示
     */
    private Integer showTime;
    /**
     * 循环方式,1-不循环，2-循环
     */
    private Integer cycleType;

    /**
     * 图片特效，1-⽆效果/2-轻微放⼤/3-轻微缩⼩/4-向左滑动/5-向右滑动/6-旋涡旋转/7-镜像翻转/8-上下抖动/9-左右抖动
     */
    private Integer pictureEffects;

}
