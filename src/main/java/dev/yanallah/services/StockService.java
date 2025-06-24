package dev.yanallah.services;

import dev.yanallah.MiniProject;
import dev.yanallah.database.Database;
import dev.yanallah.models.StockItem;

import java.util.List;

public class StockService {
    private static StockService instance;
    private final Database database;
    private final Observable<List<StockItem>> stocksObservable;

    private StockService() {
        this.database = MiniProject.getInstance().getDatabase();
        this.stocksObservable = new Observable<>();
        loadStocks();
    }

    public static StockService getInstance() {
        if (instance == null) {
            instance = new StockService();
        }
        return instance;
    }

    public Observable<List<StockItem>> getStocksObservable() {
        return stocksObservable;
    }

    public void addStockItem(StockItem stockItem) {
        database.addStockItem(stockItem);
        loadStocks();
    }

    public void updateStock(StockItem item, int newQuantity) {
        database.updateStock(item, newQuantity);
        loadStocks();
    }

    public void updateStockQuantity(int stockItemId, int quantityChange) {
        database.updateStockQuantity(stockItemId, quantityChange);
        loadStocks();
    }

    public StockItem getStockItemById(int id) {
        return database.getStockItemById(id);
    }

    public void refreshStocks() {
        loadStocks();
    }

    public int getTotalItemsInStock() {
        List<StockItem> stocks = stocksObservable.getValue();
        if (stocks == null) return 0;
        return stocks.stream().mapToInt(StockItem::getQuantityInStock).sum();
    }

    private void loadStocks() {
        List<StockItem> stocks = database.getAllStockItems();
        stocksObservable.setValue(stocks);
    }
} 