package dev.yanallah.models;

public class StockItem {

    private final String name;
    private int quantityInStock;
    private final double price;

    public StockItem(String name, int quantityInStock, double price) {
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }
}

