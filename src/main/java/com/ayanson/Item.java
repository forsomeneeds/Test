package com.ayanson;

public class Item {

    private int itemId;
    private int groupId;

    public int getItemId() {
        return  itemId;
    }

    public int getGroupId() {
        return  groupId;
    }

    public Item(int itemId, int groupId){
        this.itemId = itemId;
        this.groupId = groupId;
    }

}
