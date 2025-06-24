package dev.yanallah.services;

import dev.yanallah.MiniProject;
import dev.yanallah.database.Database;
import dev.yanallah.models.Order;
import dev.yanallah.models.OrderStatus;

import java.util.List;

public class OrderService {
    private static OrderService instance;
    private final Database database;
    private final Observable<List<Order>> ordersObservable;

    private OrderService() {
        this.database = MiniProject.getInstance().getDatabase();
        this.ordersObservable = new Observable<>();
        loadOrders();
    }

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public Observable<List<Order>> getOrdersObservable() {
        return ordersObservable;
    }

    public void createOrder(Order order) {
        database.createOrder(order);
        loadOrders();
        // Notifier également le service de stock car les quantités peuvent changer
        StockService.getInstance().refreshStocks();
    }

    public void updateOrder(Order order) {
        database.updateOrder(order);
        loadOrders();
        // Notifier également le service de stock car les quantités peuvent changer
        StockService.getInstance().refreshStocks();
    }

    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
        database.updateOrderStatus(orderId, newStatus);
        loadOrders();
    }

    public void refreshOrders() {
        loadOrders();
    }

    public double getTotalRevenue() {
        List<Order> orders = ordersObservable.getValue();
        if (orders == null) return 0.0;
        return orders.stream().mapToDouble(Order::getTotalAmount).sum();
    }

    public int getOrderCountByStatus(OrderStatus status) {
        List<Order> orders = ordersObservable.getValue();
        if (orders == null) return 0;
        return (int) orders.stream().filter(order -> order.getStatus() == status).count();
    }

    private void loadOrders() {
        List<Order> orders = database.getAllOrders();
        ordersObservable.setValue(orders);
    }
} 