package com.syzbtech.screen.websocket;

import lombok.Data;

@Data
public class Command<T> {
    private Integer type;
    private Integer command;
    private T data;
}
