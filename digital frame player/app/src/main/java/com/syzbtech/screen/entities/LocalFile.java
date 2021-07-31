package com.syzbtech.screen.entities;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

import lombok.Data;

@Data
@Table(name="local_file")
public class LocalFile implements Serializable {
    @Column(name="id", isId = true)
    private Long id;
    @Column(name="path")
    private String path; //本地保存地址
    @Column(name="md5")
    private String md5; //md5
    @Column(name="thumb")
    private String thumb;
    @Column(name="display")
    private String display;
    @Column(name="size")
    private Long size;
    @Column(name="name")
    private String name;
    @Column(name="sort")
    private Long sort;
    @Column(name="type")
    private Integer type;
    @Column(name="suffix")
    private String suffix;
    private boolean selected;
}
