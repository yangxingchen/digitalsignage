package com.syzbtech.screen.entities;

import lombok.Data;

@Data
public class Setting {
    private int resourceId;
    private int title;
    private String property;

    private boolean selected;
    private int keyIndex;

    public Setting() {

    }

    public Setting(int keyIndex, int resourceId, int title, String property, boolean selected) {
        this.keyIndex = keyIndex;
        this.resourceId =resourceId;
        this.title = title;
        this.property = property;
        this.selected = selected;
    }

    public Setting(int keyIndex, int resourceId, int title, String property){
       this(keyIndex, resourceId, title, property, false);
    }

}
