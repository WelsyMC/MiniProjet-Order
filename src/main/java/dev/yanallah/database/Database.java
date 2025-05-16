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
    }

    // Création de la table
    private void createStockItemTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS stock_items (
                        name TEXT PRIMARY KEY,
                        quantity_in_stock INTEGER NOT NULL
                    );
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insérer un StockItem
    public void insertStockItem(StockItem item) {
        String sql = "INSERT OR REPLACE INTO stock_items (name, quantity_in_stock) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantityInStock());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lire tous les StockItem
    public List<StockItem> getAllStockItems() {
        List<StockItem> items = new ArrayList<>();
        String sql = "SELECT name, quantity_in_stock FROM stock_items";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity_in_stock");
                items.add(new StockItem(name, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
}
