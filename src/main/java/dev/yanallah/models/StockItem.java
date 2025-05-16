package dev.yanallah.models;

public class StockItem {

    private int id;
    private String name;
    private int quantityInStock;
    private double price;

    public StockItem(int id, String name, int quantityInStock, double price) {
        this.id = id;
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }
}

