package com.example.binusezyfood.DataClasses;

public class ItemCart {
    private final int id;
    private final int item_id;
    private final int quantity;
    private final int subtotal_price;

    public ItemCart(int id, int item_id, int quantity, int subtotal_price) {
        this.id = id;
        this.item_id = item_id;
        this.quantity = quantity;
        this.subtotal_price = subtotal_price;
    }

    public int getId() {
        return id;
    }

    public int getItem_id() {
        return item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSubtotal_price() {
        return subtotal_price;
    }
}
