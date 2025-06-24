package dev.yanallah.services;

import dev.yanallah.models.Client;
import dev.yanallah.models.Order;
import dev.yanallah.models.OrderStatus;
import dev.yanallah.models.StockItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardService {
    private static DashboardService instance;

    private final ClientService clientService;
    private final OrderService orderService;
    private final StockService stockService;

    private final Observable<DashboardData> dashboardDataObservable;

    private DashboardService() {
        this.clientService = ClientService.getInstance();
        this.orderService = OrderService.getInstance();
        this.stockService = StockService.getInstance();
        this.dashboardDataObservable = new Observable<>();

        // S'abonner aux changements de tous les services
        clientService.getClientsObservable().subscribe(clients -> updateDashboardData());
        orderService.getOrdersObservable().subscribe(orders -> updateDashboardData());
        stockService.getStocksObservable().subscribe(stocks -> updateDashboardData());

        // Charger les données initiales
        updateDashboardData();
    }

    public static DashboardService getInstance() {
        if (instance == null) {
            instance = new DashboardService();
        }
        return instance;
    }

    public Observable<DashboardData> getDashboardDataObservable() {
        return dashboardDataObservable;
    }

    public void refreshDashboard() {
        updateDashboardData();
    }

    private void updateDashboardData() {
        List<Client> clients = clientService.getClientsObservable().getValue();
        List<Order> orders = orderService.getOrdersObservable().getValue();
        List<StockItem> stocks = stockService.getStocksObservable().getValue();

        if (clients == null || orders == null || stocks == null) {
            return; // Pas encore toutes les données chargées
        }

        DashboardData data = new DashboardData(
                clients.size(),
                orders.size(),
                orderService.getTotalRevenue(),
                stockService.getTotalItemsInStock(),
                getOrdersByStatus(orders),
                getLowStockItems(stocks),
                getRecentOrders(orders)
        );

        dashboardDataObservable.setValue(data);
    }

    private Map<OrderStatus, Integer> getOrdersByStatus(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    private List<StockItem> getLowStockItems(List<StockItem> stocks) {
        return stocks.stream()
                .filter(item -> item.getQuantityInStock() < 10) // Seuil bas configurable
                .collect(Collectors.toList());
    }

    private List<Order> getRecentOrders(List<Order> orders) {
        return orders.stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(5)
                .collect(Collectors.toList());
    }

    // Classe interne pour encapsuler les données du dashboard
    public static class DashboardData {
        private final int totalClients;
        private final int totalOrders;
        private final double totalRevenue;
        private final int totalStockItems;
        private final Map<OrderStatus, Integer> ordersByStatus;
        private final List<StockItem> lowStockItems;
        private final List<Order> recentOrders;

        public DashboardData(int totalClients, int totalOrders, double totalRevenue,
                             int totalStockItems, Map<OrderStatus, Integer> ordersByStatus,
                             List<StockItem> lowStockItems, List<Order> recentOrders) {
            this.totalClients = totalClients;
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.totalStockItems = totalStockItems;
            this.ordersByStatus = ordersByStatus;
            this.lowStockItems = lowStockItems;
            this.recentOrders = recentOrders;
        }

        // Getters
        public int getTotalClients() {
            return totalClients;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public int getTotalStockItems() {
            return totalStockItems;
        }

        public Map<OrderStatus, Integer> getOrdersByStatus() {
            return ordersByStatus;
        }

        public List<StockItem> getLowStockItems() {
            return lowStockItems;
        }

        public List<Order> getRecentOrders() {
            return recentOrders;
        }
    }
} 