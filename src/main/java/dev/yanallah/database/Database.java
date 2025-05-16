package dev.yanallah.database;

import dev.yanallah.models.StockItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:data.db";

    public Database() {
        System.out.println("[Database] Database opened.");
        createStockItemTable();
        insertTestData(); // Insertion des données de test
    }

    private void createStockItemTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS stock_items (
                        name TEXT PRIMARY KEY,
                        quantity_in_stock INTEGER NOT NULL,
                        price REAL NOT NULL
                    );
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertStockItem(StockItem item) {
        String sql = "INSERT OR REPLACE INTO stock_items (name, quantity_in_stock, price) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantityInStock());
            pstmt.setDouble(3, item.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StockItem> getAllStockItems() {
        List<StockItem> items = new ArrayList<>();
        String sql = "SELECT name, quantity_in_stock,price FROM stock_items";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity_in_stock");
                double price = rs.getDouble("price");
                items.add(new StockItem(name, quantity, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private void insertTestData() {
        List<StockItem> testItems = List.of(
                new StockItem("Clé USB 16Go", 120, 4.99),
                new StockItem("Clavier mécanique", 45, 29.99),
                new StockItem("Écran 24 pouces", 32, 279.99),
                new StockItem("Souris optique", 87, 39.99),
                new StockItem("Disque SSD 512Go", 60, 69.99)
        );

        for (StockItem item : testItems) {
            insertStockItem(item);
        }

        System.out.println("[Database] Test data inserted.");
    }
}
