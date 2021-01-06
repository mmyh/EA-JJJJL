package com.mmyh.eajjjjl.library;

public class EAListItemInfo {

    public static final int ITEM_HEAD_TYPE = -1;

    public static final int ITEM_FOOT_TYPE = -2;

    public static final int ITEM_NORMAL_TYPE = -3;

    public Class bindingClass;

    public int itemType;

    public EAListItemInfo(Class bindingClass, int itemType) {
        if (itemType < 0) {
            throw new IllegalArgumentException("itemType must > 0");
        }
        this.bindingClass = bindingClass;
        this.itemType = itemType;
    }

}
