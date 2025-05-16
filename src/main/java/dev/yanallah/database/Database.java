package dev.yanallah.database;

import dev.yanallah.models.Client;
import dev.yanallah.models.StockItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:data.db";

    public Database() {
        System.out.println("[Database] Database opened.");
        createTables();
        initializeDemoData();
    }

    private void createTables() {
        // Table des stocks
        String stockTableSql = """
                    CREATE TABLE IF NOT EXISTS stock_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        quantity_in_stock INTEGER NOT NULL,
                        price REAL NOT NULL
                    );
                """;

        // Table des clients
        String clientTableSql = """
                    CREATE TABLE IF NOT EXISTS clients (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nom TEXT NOT NULL,
                        prenom TEXT NOT NULL,
                        email TEXT NOT NULL,
                        telephone TEXT NOT NULL,
                        adresse TEXT NOT NULL
                    );
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(stockTableSql);
            stmt.execute(clientTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthodes pour les stocks
    public void insertStockItem(StockItem item) {
        String sql = "INSERT OR REPLACE INTO stock_items (id, name, quantity_in_stock, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getId());
            pstmt.setString(2, item.getName());
            pstmt.setInt(3, item.getQuantityInStock());
            pstmt.setDouble(4, item.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StockItem> getAllStockItems() {
        List<StockItem> items = new ArrayList<>();
        String sql = "SELECT id, name, quantity_in_stock, price FROM stock_items";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new StockItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity_in_stock"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void addStockItem(StockItem item) {
        String sql = "INSERT INTO stock_items (name, quantity_in_stock, price) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantityInStock());
            pstmt.setDouble(3, item.getPrice());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthodes pour les clients
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, email, telephone, adresse FROM clients";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public void addClient(Client client) {
        String sql = "INSERT INTO clients (nom, prenom, email, telephone, adresse) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getEmail());
            pstmt.setString(4, client.getTelephone());
            pstmt.setString(5, client.getAdresse());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Client getClientById(int id) {
        String sql = "SELECT id, nom, prenom, email, telephone, adresse FROM clients WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public StockItem getStockItemById(int id) {
        String sql = "SELECT id, name, quantity_in_stock, price FROM stock_items WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new StockItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity_in_stock"),
                        rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void initializeDemoData() {
        // Vérifier si des données existent déjà
        if (getAllClients().isEmpty()) {
            // Données de démo pour les clients
            addClient(new Client(0, "Dupont", "Jean", "jean.dupont@email.com", "0612345678", "123 rue de Paris, 75001 Paris"));
            addClient(new Client(0, "Martin", "Sophie", "sophie.martin@email.com", "0623456789", "456 avenue des Champs-Élysées, 75008 Paris"));
            addClient(new Client(0, "Bernard", "Pierre", "pierre.bernard@email.com", "0634567890", "789 boulevard Saint-Michel, 75005 Paris"));
            addClient(new Client(0, "Petit", "Marie", "marie.petit@email.com", "0645678901", "321 rue de la Paix, 75002 Paris"));
            addClient(new Client(0, "Robert", "Lucas", "lucas.robert@email.com", "0656789012", "654 rue du Commerce, 75015 Paris"));
        }

        if (getAllStockItems().isEmpty()) {
            // Données de démo pour les stocks
            addStockItem(new StockItem(0, "Ordinateur portable", 10, 899.99));
            addStockItem(new StockItem(0, "Souris sans fil", 50, 29.99));
            addStockItem(new StockItem(0, "Clavier mécanique", 30, 79.99));
            addStockItem(new StockItem(0, "Écran 24 pouces", 15, 199.99));
            addStockItem(new StockItem(0, "Casque audio", 25, 149.99));
        }
    }
}
