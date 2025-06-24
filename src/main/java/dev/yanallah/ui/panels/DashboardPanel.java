package dev.yanallah.ui.panels;

import dev.yanallah.models.Order;
import dev.yanallah.models.OrderStatus;
import dev.yanallah.models.StockItem;
import dev.yanallah.services.DashboardService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    private final DashboardService dashboardService;
    private DashboardService.DashboardData currentData;

    // Composants UI qui seront mis à jour dynamiquement
    private JPanel statsCardsPanel;
    private JPanel chartsPanel;
    private JPanel detailsPanel;
    
    // Couleurs pour le thème
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color INFO_COLOR = new Color(23, 162, 184);
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;

    public DashboardPanel(){
        this.dashboardService = DashboardService.getInstance();
        
        this.initComponents();
        this.subscribeToDashboardUpdates();
    }

    public void initComponents(){
        this.setLayout(new BorderLayout());
        this.setBackground(LIGHT_GRAY);

        // En-tête avec titre et date
        JPanel headerPanel = createHeaderPanel();
        
        // Panel principal avec les statistiques
        JPanel mainPanel = createMainPanel();
        
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Dashboard - Gestion de Commandes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        JLabel dateLabel = new JLabel("Dernière mise à jour: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(220, 220, 220));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void subscribeToDashboardUpdates() {
        dashboardService.getDashboardDataObservable().subscribe(this::updateDashboardComponents);
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Première ligne - Cartes de statistiques
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 0.3;
        statsCardsPanel = new JPanel();
        mainPanel.add(statsCardsPanel, gbc);
        
        // Deuxième ligne - Graphiques et analyses
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 1; gbc.weighty = 0.4;
        chartsPanel = new JPanel();
        mainPanel.add(chartsPanel, gbc);
        
        // Troisième ligne - Informations détaillées
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 1; gbc.weighty = 0.3;
        detailsPanel = new JPanel();
        mainPanel.add(detailsPanel, gbc);
        
        return mainPanel;
    }

    private void updateDashboardComponents(DashboardService.DashboardData data) {
        SwingUtilities.invokeLater(() -> {
            this.currentData = data;
            if (data != null) {
                updateStatsCardsPanel(data);
                updateChartsPanel(data);
                updateDetailsPanel(data);
                revalidate();
                repaint();
            }
        });
    }

    private void updateStatsCardsPanel(DashboardService.DashboardData data) {
        statsCardsPanel.removeAll();
        statsCardsPanel.setLayout(new GridLayout(1, 4, 15, 0));
        statsCardsPanel.setBackground(LIGHT_GRAY);
        
        // Carte Clients
        statsCardsPanel.add(createStatCard("Clients", String.valueOf(data.getTotalClients()), 
            "Total des clients enregistrés", SUCCESS_COLOR));
        
        // Carte Commandes
        statsCardsPanel.add(createStatCard("Commandes", String.valueOf(data.getTotalOrders()), 
            "Total des commandes", INFO_COLOR));
        
        // Carte Chiffre d'affaires
        statsCardsPanel.add(createStatCard("CA Total", String.format("%.2f €", data.getTotalRevenue()), 
            "Chiffre d'affaires total", PRIMARY_COLOR));
        
        // Carte Stock
        statsCardsPanel.add(createStatCard("Articles", String.valueOf(data.getTotalStockItems()), 
            "Total articles en stock", WARNING_COLOR));
    }
    
    private JPanel createStatCard(String title, String value, String description, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 15, 15);
                
                // Fond de la carte
                g2.setColor(CARD_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 15, 15);
                
                // Barre colorée en haut
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth()-2, 5, 15, 15);
                
                g2.dispose();
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setPreferredSize(new Dimension(200, 120));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(50, 50, 50));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(120, 120, 120));
        
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(titleLabel, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);
        content.add(descLabel, BorderLayout.SOUTH);
        
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }

    private void updateChartsPanel(DashboardService.DashboardData data) {
        chartsPanel.removeAll();
        chartsPanel.setLayout(new GridLayout(1, 2, 15, 0));
        chartsPanel.setBackground(LIGHT_GRAY);
        
        // Graphique des statuts de commandes
        chartsPanel.add(createOrderStatusChart(data));
        
        // Graphique des stocks faibles
        chartsPanel.add(createLowStockChart(data));
    }

    private JPanel createOrderStatusChart(DashboardService.DashboardData data) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(CARD_BACKGROUND);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Répartition des Commandes par Statut");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        // Utiliser les données du service
        Map<OrderStatus, Integer> statusCounts = data.getOrdersByStatus();

        JPanel chartContent = new JPanel(new GridLayout(Math.max(statusCounts.size(), 1), 1, 5, 5));
        chartContent.setBackground(CARD_BACKGROUND);

        if (statusCounts.isEmpty()) {
            JLabel noDataLabel = new JLabel("Aucune commande");
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            chartContent.add(noDataLabel);
        } else {
            int maxCount = statusCounts.values().stream().mapToInt(Integer::intValue).max().orElse(1);

            for (Map.Entry<OrderStatus, Integer> entry : statusCounts.entrySet()) {
                JPanel barPanel = createStatusBar(entry.getKey().getDisplayName(),
                        entry.getValue(), maxCount, getStatusColor(entry.getKey()));
                chartContent.add(barPanel);
            }
        }
        
        chartPanel.add(titleLabel, BorderLayout.NORTH);
        chartPanel.add(chartContent, BorderLayout.CENTER);
        
        return chartPanel;
    }

    private JPanel createLowStockChart(DashboardService.DashboardData data) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(CARD_BACKGROUND);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Articles avec Stock Faible (< 10)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));

        // Utiliser les données du service
        List<StockItem> lowStockItems = data.getLowStockItems().stream()
            .limit(5) // Limiter à 5 pour l'affichage
            .collect(Collectors.toList());
        
        JPanel chartContent = new JPanel(new GridLayout(Math.max(lowStockItems.size(), 1), 1, 5, 5));
        chartContent.setBackground(CARD_BACKGROUND);
        
        if (lowStockItems.isEmpty()) {
            JLabel noDataLabel = new JLabel("Tous les stocks sont suffisants !");
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noDataLabel.setForeground(SUCCESS_COLOR);
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            chartContent.add(noDataLabel);
        } else {
            int maxStock = lowStockItems.stream().mapToInt(StockItem::getQuantityInStock).max().orElse(1);
            
            for (StockItem item : lowStockItems) {
                JPanel barPanel = createStockBar(item.getName(), 
                    item.getQuantityInStock(), maxStock);
                chartContent.add(barPanel);
            }
        }
        
        chartPanel.add(titleLabel, BorderLayout.NORTH);
        chartPanel.add(chartContent, BorderLayout.CENTER);
        
        return chartPanel;
    }

    private void updateDetailsPanel(DashboardService.DashboardData data) {
        detailsPanel.removeAll();
        detailsPanel.setLayout(new GridLayout(1, 2, 15, 0));
        detailsPanel.setBackground(LIGHT_GRAY);
        
        // Dernières commandes
        detailsPanel.add(createRecentOrdersPanel(data));
        
        // Statistiques avancées
        detailsPanel.add(createAdvancedStatsPanel(data));
    }

    private JPanel createRecentOrdersPanel(DashboardService.DashboardData data) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Dernières Commandes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));
        
        JPanel ordersContent = new JPanel();
        ordersContent.setLayout(new BoxLayout(ordersContent, BoxLayout.Y_AXIS));
        ordersContent.setBackground(CARD_BACKGROUND);

        // Utiliser les données du service
        List<Order> recentOrders = data.getRecentOrders();
        
        for (Order order : recentOrders) {
            JPanel orderPanel = createOrderSummaryPanel(order);
            ordersContent.add(orderPanel);
            ordersContent.add(Box.createVerticalStrut(8));
        }

        if (recentOrders.isEmpty()) {
            JLabel noDataLabel = new JLabel("Aucune commande récente");
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            ordersContent.add(noDataLabel);
        }
        
        JScrollPane scrollPane = new JScrollPane(ordersContent);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CARD_BACKGROUND);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createAdvancedStatsPanel(DashboardService.DashboardData data) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Statistiques Avancées");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 50, 50));
        
        JPanel statsContent = new JPanel();
        statsContent.setLayout(new BoxLayout(statsContent, BoxLayout.Y_AXIS));
        statsContent.setBackground(CARD_BACKGROUND);

        // Calcul des statistiques basées sur les données du service
        double avgOrderValue = data.getTotalOrders() == 0 ? 0 :
                data.getTotalRevenue() / data.getTotalOrders();

        // Valeur totale du stock (estimée - on pourrait ajouter cette donnée au service)
        double totalStockValue = data.getLowStockItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantityInStock())
            .sum();

        // Commandes en attente
        int pendingOrders = data.getOrdersByStatus().getOrDefault(OrderStatus.CREATED, 0) +
                data.getOrdersByStatus().getOrDefault(OrderStatus.PREPARING, 0);

        // Nombre de produits différents
        int productTypes = data.getLowStockItems().size(); // Approximation
        
        // Affichage des statistiques
        statsContent.add(createStatRow("€", "Valeur moyenne commande", 
            String.format("%.2f €", avgOrderValue)));
        statsContent.add(Box.createVerticalStrut(12));

        statsContent.add(createStatRow("€", "Valeur stock faible", 
            String.format("%.2f €", totalStockValue)));
        statsContent.add(Box.createVerticalStrut(12));
        
        statsContent.add(createStatRow("!", "Commandes en attente", 
            String.valueOf(pendingOrders)));
        statsContent.add(Box.createVerticalStrut(12));

        statsContent.add(createStatRow("#", "Articles stock faible",
                String.valueOf(productTypes)));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(statsContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Méthodes utilitaires pour créer les composants
    
    private JPanel createStatusBar(String label, int count, int maxCount, Color color) {
        JPanel barPanel = new JPanel(new BorderLayout());
        barPanel.setBackground(CARD_BACKGROUND);
        barPanel.setPreferredSize(new Dimension(0, 25));
        
        JLabel labelText = new JLabel(label + " (" + count + ")");
        labelText.setFont(new Font("Arial", Font.PLAIN, 12));
        labelText.setPreferredSize(new Dimension(120, 20));
        
        JPanel barContainer = new JPanel(new BorderLayout());
        barContainer.setBackground(new Color(240, 240, 240));
        barContainer.setPreferredSize(new Dimension(0, 15));
        
        int barWidth = maxCount > 0 ? (count * 100) / maxCount : 0;
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(barWidth, 15));
        
        barContainer.add(colorBar, BorderLayout.WEST);
        
        barPanel.add(labelText, BorderLayout.WEST);
        barPanel.add(barContainer, BorderLayout.CENTER);
        
        return barPanel;
    }
    
    private JPanel createStockBar(String itemName, int quantity, int maxQuantity) {
        JPanel barPanel = new JPanel(new BorderLayout());
        barPanel.setBackground(CARD_BACKGROUND);
        barPanel.setPreferredSize(new Dimension(0, 25));
        
        JLabel labelText = new JLabel(itemName + " (" + quantity + ")");
        labelText.setFont(new Font("Arial", Font.PLAIN, 12));
        labelText.setPreferredSize(new Dimension(150, 20));
        
        JPanel barContainer = new JPanel(new BorderLayout());
        barContainer.setBackground(new Color(240, 240, 240));
        barContainer.setPreferredSize(new Dimension(0, 15));
        
        int barWidth = maxQuantity > 0 ? (quantity * 100) / maxQuantity : 0;
        Color barColor = quantity < 5 ? DANGER_COLOR : WARNING_COLOR;
        
        JPanel colorBar = new JPanel();
        colorBar.setBackground(barColor);
        colorBar.setPreferredSize(new Dimension(barWidth, 15));
        
        barContainer.add(colorBar, BorderLayout.WEST);
        
        barPanel.add(labelText, BorderLayout.WEST);
        barPanel.add(barContainer, BorderLayout.CENTER);
        
        return barPanel;
    }
    
    private JPanel createOrderSummaryPanel(Order order) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        String clientName = "Client #" + order.getClientId();
        if (order.getClient() != null) {
            clientName = order.getClient().getNom() + " " + order.getClient().getPrenom();
        }
        
        JLabel orderInfo = new JLabel("Commande #" + order.getId() + " - " + clientName);
        orderInfo.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel orderDetails = new JLabel(String.format("%.2f € - %s", 
            order.getTotalAmount(), order.getStatus().getDisplayName()));
        orderDetails.setFont(new Font("Arial", Font.PLAIN, 11));
        orderDetails.setForeground(new Color(100, 100, 100));
        
        panel.add(orderInfo, BorderLayout.NORTH);
        panel.add(orderDetails, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatRow(String icon, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD_BACKGROUND);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        iconLabel.setPreferredSize(new Dimension(30, 20));
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Arial", Font.BOLD, 12));
        valueText.setForeground(PRIMARY_COLOR);
        
        row.add(iconLabel, BorderLayout.WEST);
        row.add(labelText, BorderLayout.CENTER);
        row.add(valueText, BorderLayout.EAST);
        
        return row;
    }
    
    private Color getStatusColor(OrderStatus status) {
        switch (status) {
            case CREATED:
                return INFO_COLOR;
            case PREPARING:
                return WARNING_COLOR;
            case SENT:
                return PRIMARY_COLOR;
            case RECEIVED:
                return SUCCESS_COLOR;
            case CANCELLED:
                return DANGER_COLOR;
            default:
                return INFO_COLOR;
        }
    }
    
    public void updateDashboard() {
        dashboardService.refreshDashboard();
    }
}
