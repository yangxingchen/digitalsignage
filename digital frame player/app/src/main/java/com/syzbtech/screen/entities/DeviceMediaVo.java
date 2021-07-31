package com.syzbtech.screen.entities;

import com.syzbtech.screen.http.JsonResponseParser;

import org.xutils.http.annotation.HttpResponse;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author coowalt@sina.com
 * @Date 2021/3/9 1:27
 */
@Data
@HttpResponse(parser = JsonResponseParser.class)
public class DeviceMediaVo implements Serializable {
    private List<DeviceMedia> deviceMediaList ;
    private List<PlayBkMusic> musicList;
    private PlaySetting playSetting;
}
