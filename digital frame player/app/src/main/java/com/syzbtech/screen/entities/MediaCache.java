package com.syzbtech.screen.entities;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import lombok.Data;

@Data
@Table(name="media_cache")
public class MediaCache {
    @Column(name="id", isId = true)
    private Long id;
    @Column(name="userid")
    private Long userid; //用户ID
    @Column(name="deviceid")
    private Long deviceid; //设备ID
    @Column(name="path")
    private String path; //本地保存地址
    @Column(name="md5")
    private String md5; //md5
    @Column(name="url")
    private String url; //远程地址
    @Column(name="size")
    private Integer size;
}
