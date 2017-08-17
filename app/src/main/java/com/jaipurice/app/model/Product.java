package com.jaipurice.app.model;

import java.io.Serializable;

/**
 * Created by SoNu on 8/14/2017.
 */

public class Product implements Serializable {
    private String itemId, itemName, itemPrice, itemImageURL;
    private int empTotalItemsQty, userSelectedQty;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemImageURL() {
        return itemImageURL;
    }

    public void setItemImageURL(String itemImageURL) {
        this.itemImageURL = itemImageURL;
    }

    public int getEmpTotalItemsQty() {
        return empTotalItemsQty;
    }

    public void setEmpTotalItemsQty(int empTotalItemsQty) {
        this.empTotalItemsQty = empTotalItemsQty;
    }

    public int getUserSelectedQty() {
        return userSelectedQty;
    }

    public void setUserSelectedQty(int userSelectedQty) {
        this.userSelectedQty = userSelectedQty;
    }
}