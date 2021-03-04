package com.example.mobilestore.orders;

public class Order {
    public String OrderNumber;
    public String OrderDate;

    public Order(String orderNumber, String orderDate) {
        OrderNumber = orderNumber;
        OrderDate = orderDate;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public String getOrderDate() {
        return OrderDate;
    }
}
