package dev.yanallah.models;

public class StockItem {

    private final String name;
    private int quantityInStock;

    public StockItem(String name, int quantityInStock) {
        this.name = name;
        this.quantityInStock = quantityInStock;
    }

    public String getName() {
        return name;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }
}

