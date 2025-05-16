package dev.yanallah.models;

public class OrderItem {
    private int id;
    private int orderId;
    private int stockItemId;
    private int quantity;
    private double unitPrice;
    private StockItem stockItem;

    public OrderItem(int id, int orderId, int stockItemId, int quantity, double unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.stockItemId = stockItemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getStockItemId() {
        return stockItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    // Setters
    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    public double getTotalPrice() {
        return quantity * unitPrice;
    }
}