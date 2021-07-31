package com.syzbtech.screen.entities;

import com.syzbtech.screen.http.JsonResponseParser;

import org.xutils.http.annotation.HttpResponse;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author coowalt@sina.com
 * @Date 2021/2/25 23:05
 */
@Data
public class DeviceMedia implements Serializable {
    private Long id;
    private Long userId;
    private Long deviceId;
    private Long mediaId;
    private Long sort;
    private Date createTime;
    private Date updateTime;
    private Integer mediaType;
    private Media media;
}
