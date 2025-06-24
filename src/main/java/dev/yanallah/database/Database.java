package dev.yanallah.database;

import dev.yanallah.models.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:data.db";

    public Database() {
        createTables();
        initializeDemoData();
    }

    private void createTables() {
        // Table des stocks
        String stockTableSql = "CREATE TABLE IF NOT EXISTS stock_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "quantity_in_stock INTEGER NOT NULL," +
                "price REAL NOT NULL" +
                ")";

        // Table des clients
        String clientTableSql = "CREATE TABLE IF NOT EXISTS clients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT NOT NULL," +
                "prenom TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "telephone TEXT NOT NULL," +
                "adresse TEXT NOT NULL" +
                ")";

        // Table des commandes
        String orderTableSql = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "client_id INTEGER NOT NULL," +
                "order_date TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "FOREIGN KEY (client_id) REFERENCES clients (id)" +
                ")";

        // Table des items de commande
        String orderItemTableSql = "CREATE TABLE IF NOT EXISTS order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER NOT NULL," +
                "stock_item_id INTEGER NOT NULL," +
                "quantity INTEGER NOT NULL," +
                "unit_price REAL NOT NULL," +
                "FOREIGN KEY (order_id) REFERENCES orders (id)," +
                "FOREIGN KEY (stock_item_id) REFERENCES stock_items (id)" +
                ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(stockTableSql);
            stmt.execute(clientTableSql);
            stmt.execute(orderTableSql);
            stmt.execute(orderItemTableSql);
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

    // Méthodes pour les commandes
    public void createOrder(Order order) {
        String orderSql = "INSERT INTO orders (client_id, order_date, status) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, order.getClientId());
            pstmt.setString(2, order.getOrderDate().toString());
            pstmt.setString(3, order.getStatus().name());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setId(orderId);

                    // Ajouter les items de la commande
                    for (OrderItem item : order.getItems()) {
                        addOrderItem(orderId, item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOrderItem(int orderId, OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, stock_item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, item.getStockItemId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getUnitPrice());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }

            // Mettre à jour le stock
            updateStock(item.getStockItem(), item.getQuantity());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT id, client_id, order_date, status FROM orders ORDER BY order_date DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        LocalDateTime.parse(rs.getString("order_date")),
                        OrderStatus.valueOf(rs.getString("status"))
                );

                // Charger le client
                order.setClient(getClientById(order.getClientId()));

                // Charger les items
                loadOrderItems(order);

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    private void loadOrderItems(Order order) {
        String sql = "SELECT id, stock_item_id, quantity, unit_price FROM order_items WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        order.getId(),
                        rs.getInt("stock_item_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                );

                // Charger l'item de stock associé
                item.setStockItem(getStockItemById(item.getStockItemId()));

                order.addItem(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStockQuantity(int stockItemId, int quantityChange) {
        String sql = "UPDATE stock_items SET quantity_in_stock = quantity_in_stock + ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, stockItemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrder(Order order) {
        String orderSql = "UPDATE orders SET client_id = ?, order_date = ?, status = ? WHERE id = ?";
        String selectOldItemsSql = "SELECT stock_item_id, quantity FROM order_items WHERE order_id = ?";
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try {
                // 1. Récupérer les anciens items pour restaurer le stock
                List<OrderItem> oldItems = new ArrayList<>();
                try (PreparedStatement pstmt = conn.prepareStatement(selectOldItemsSql)) {
                    pstmt.setInt(1, order.getId());
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        OrderItem oldItem = new OrderItem(0, order.getId(),
                                rs.getInt("stock_item_id"), rs.getInt("quantity"), 0.0);
                        oldItems.add(oldItem);
                    }
                }

                // 2. Restaurer le stock des anciens items
                for (OrderItem oldItem : oldItems) {
                    updateStockQuantity(conn, oldItem.getStockItemId(), oldItem.getQuantity());
                }

                // 3. Mettre à jour la commande
                try (PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
                    pstmt.setInt(1, order.getClientId());
                    pstmt.setString(2, order.getOrderDate().toString());
                    pstmt.setString(3, order.getStatus().name());
                    pstmt.setInt(4, order.getId());
                    pstmt.executeUpdate();
                }

                // 4. Supprimer les anciens items
                try (PreparedStatement pstmt = conn.prepareStatement(deleteItemsSql)) {
                    pstmt.setInt(1, order.getId());
                    pstmt.executeUpdate();
                }

                // 5. Ajouter les nouveaux items (qui vont décrémenter le stock)
                for (OrderItem item : order.getItems()) {
                    addOrderItem(conn, order.getId(), item);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier cette méthode pour accepter une connexion existante
    private void addOrderItem(Connection conn, int orderId, OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, stock_item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, item.getStockItemId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getUnitPrice());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }

            // Mettre à jour le stock en utilisant la même connexion
            updateStockQuantity(conn, item.getStockItemId(), -item.getQuantity());
        }
    }

    // Modifier cette méthode pour accepter une connexion existante
    private void updateStockQuantity(Connection conn, int stockItemId, int quantityChange) throws SQLException {
        String sql = "UPDATE stock_items SET quantity_in_stock = quantity_in_stock + ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, stockItemId);
            pstmt.executeUpdate();
        }
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

    public void updateStock(StockItem item, int newQuantity) {
        String sql = "UPDATE stock_items SET quantity_in_stock = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}