package com.syzbtech.screen.http;

import org.xutils.http.annotation.HttpResponse;

import lombok.Data;

@HttpResponse(parser = JsonResponseParser.class)
@Data
public class Result<T> {
    private T data;
    private String msg;
    private Integer code;

    public Result() {}
    public Result(Integer code) {
        this(code, "", null);
    }
    public Result(Integer code, String msg) {
        this(code, msg, null);
    }
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
