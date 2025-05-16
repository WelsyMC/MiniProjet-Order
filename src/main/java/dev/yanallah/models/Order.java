package dev.yanallah.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int clientId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItem> items;
    private Client client;

    public Order(int id, int clientId, LocalDateTime orderDate, OrderStatus status) {
        this.id = id;
        this.clientId = clientId;
        this.orderDate = orderDate;
        this.status = status;
        this.items = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Client getClient() {
        return client;
    }

    // Setters
    public void setClient(Client client) {
        this.client = client;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }
}