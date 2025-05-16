package dev.yanallah.ui.panels;

import dev.yanallah.MiniProject;
import dev.yanallah.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommandesPanel extends JPanel {
    private List<Order> orders;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JDialog createOrderDialog;
    private JPanel formPanel;
    private JComboBox<Client> clientComboBox;
    private JComboBox<StockItem> stockItemComboBox;
    private JSpinner quantitySpinner;
    private List<OrderItem> currentOrderItems;
    private JTable orderItemsTable;
    private DefaultTableModel orderItemsTableModel;

    public CommandesPanel() {
        this.initComponents();
        this.orders = MiniProject.getInstance().getDatabase().getAllOrders();
        this.loadOrderData();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Titre du panel
        JLabel titleLabel = new JLabel("Gestion des Commandes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Créer le modèle de table pour les commandes
        String[] columnNames = {"ID", "Client", "Date", "Statut", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Créer la table des commandes
        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(25);
        ordersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        ordersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        ordersTable.setSelectionBackground(new Color(184, 207, 229));
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Centrer les valeurs pour toutes les colonnes
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < ordersTable.getColumnCount(); i++) {
            ordersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Scroll pane pour la table des commandes
        scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        scrollPane.setPreferredSize(new Dimension(800, 200));

        // Bouton pour créer une nouvelle commande
        JButton addOrderButton = new JButton("Nouvelle Commande");
        addOrderButton.setBackground(new Color(70, 130, 180));
        addOrderButton.setForeground(Color.WHITE);
        addOrderButton.setFocusPainted(false);
        addOrderButton.addActionListener(e -> showCreateOrderDialog());

        // Panel pour le bouton
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addOrderButton);

        // Ajout des composants au panel principal
        this.add(titleLabel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showCreateOrderDialog() {
        createOrderDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouvelle Commande", true);
        createOrderDialog.setLayout(new BorderLayout());
        createOrderDialog.setSize(800, 600);
        createOrderDialog.setLocationRelativeTo(this);

        // Initialiser la liste des items de la commande en cours
        currentOrderItems = new ArrayList<>();

        // Créer le formulaire
        formPanel = createFormPanel();
        createOrderDialog.add(formPanel, BorderLayout.CENTER);

        // Bouton pour fermer la fenêtre
        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> createOrderDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        createOrderDialog.add(buttonPanel, BorderLayout.SOUTH);

        createOrderDialog.setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Panel pour la sélection du client et des items
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        // ComboBox pour les clients
        clientComboBox = new JComboBox<>(MiniProject.getInstance().getDatabase().getAllClients().toArray(new Client[0]));
        clientComboBox.setPreferredSize(new Dimension(200, 30));

        // ComboBox pour les items de stock
        stockItemComboBox = new JComboBox<>(MiniProject.getInstance().getDatabase().getAllStockItems().toArray(new StockItem[0]));
        stockItemComboBox.setPreferredSize(new Dimension(200, 30));

        // Spinner pour la quantité
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setPreferredSize(new Dimension(100, 30));

        // Ajouter un écouteur sur le champ de texte du spinner
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) quantitySpinner.getEditor();
        JFormattedTextField textField = editor.getTextField();
        textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                checkQuantity();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                checkQuantity();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                checkQuantity();
            }

            private void checkQuantity() {
                try {
                    String text = textField.getText();
                    if (!text.isEmpty()) {
                        int quantity = Integer.parseInt(text);
                        StockItem selectedItem = (StockItem) stockItemComboBox.getSelectedItem();
                        if (selectedItem != null) {
                            int totalQuantityRequested = quantity;

                            // Calculer la quantité totale déjà demandée
                            for (OrderItem existingItem : currentOrderItems) {
                                if (existingItem.getStockItemId() == selectedItem.getId()) {
                                    totalQuantityRequested += existingItem.getQuantity();
                                }   
                            }

                            // Si la quantité dépasse le stock disponible, ajuster le spinner
                            if (selectedItem.getQuantityInStock() < totalQuantityRequested) {
                                final int maxAvailable = Math.max(1, selectedItem.getQuantityInStock() - (totalQuantityRequested - quantity));
                                SwingUtilities.invokeLater(() -> {
                                    quantitySpinner.setValue(maxAvailable);
                                    JOptionPane.showMessageDialog(CommandesPanel.this,
                                            "Stock insuffisant. Quantité maximale disponible : " + maxAvailable,
                                            "Attention",
                                            JOptionPane.WARNING_MESSAGE);
                                });
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    // Ignorer les entrées non numériques
                }
            }
        });

        // Bouton pour ajouter un item
        JButton addItemButton = new JButton("Ajouter l'item");
        addItemButton.setBackground(new Color(70, 130, 180));
        addItemButton.setForeground(Color.WHITE);
        addItemButton.setFocusPainted(false);
        addItemButton.addActionListener(e -> addItemToOrder());

        // Ajout des composants au panel de sélection
        gbc.gridx = 0;
        gbc.gridy = 0;
        selectionPanel.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(clientComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        selectionPanel.add(new JLabel("Produit:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(stockItemComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        selectionPanel.add(new JLabel("Quantité:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(quantitySpinner, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        selectionPanel.add(addItemButton, gbc);

        // Table pour les items de la commande en cours
        String[] orderItemColumns = {"Produit", "Quantité", "Prix unitaire", "Total"};
        orderItemsTableModel = new DefaultTableModel(orderItemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderItemsTable = new JTable(orderItemsTableModel);
        orderItemsTable.setRowHeight(25);
        JScrollPane orderItemsScrollPane = new JScrollPane(orderItemsTable);

        // Bouton pour créer la commande
        JButton createOrderButton = new JButton("Créer la commande");
        createOrderButton.setBackground(new Color(46, 139, 87));
        createOrderButton.setForeground(Color.WHITE);
        createOrderButton.setFocusPainted(false);
        createOrderButton.addActionListener(e -> createOrder());

        // Panel pour le bouton de création
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(createOrderButton);

        // Ajout des composants au panel principal
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(orderItemsScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addItemToOrder() {
        Client selectedClient = (Client) clientComboBox.getSelectedItem();
        StockItem selectedItem = (StockItem) stockItemComboBox.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();

        // Vérifier la quantité totale demandée (incluant les items déjà ajoutés)
        int totalQuantityRequested = quantity;
        for (OrderItem existingItem : currentOrderItems) {
            if (existingItem.getStockItemId() == selectedItem.getId()) {
                totalQuantityRequested += existingItem.getQuantity();
            }
        }

        if (selectedItem.getQuantityInStock() < totalQuantityRequested) {
            JOptionPane.showMessageDialog(this,
                    "Quantité insuffisante en stock. Stock disponible : " + selectedItem.getQuantityInStock() +
                            "\nQuantité déjà demandée : " + (totalQuantityRequested - quantity) +
                            "\nQuantité restante disponible : " + (selectedItem.getQuantityInStock() - (totalQuantityRequested - quantity)),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        OrderItem orderItem = new OrderItem(0, 0, selectedItem.getId(), quantity, selectedItem.getPrice());
        orderItem.setStockItem(selectedItem);
        currentOrderItems.add(orderItem);

        // Mettre à jour la table des items
        orderItemsTableModel.addRow(new Object[]{
                selectedItem.getName(),
                quantity,
                String.format("%.2f €", selectedItem.getPrice()),
                String.format("%.2f €", orderItem.getTotalPrice())
        });

        // Réinitialiser le spinner à 1
        SwingUtilities.invokeLater(() -> quantitySpinner.setValue(1));
    }

    private void createOrder() {
        if (currentOrderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez ajouter au moins un item à la commande",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client selectedClient = (Client) clientComboBox.getSelectedItem();
        Order newOrder = new Order(0, selectedClient.getId(), LocalDateTime.now(), OrderStatus.CREATED);

        // Ajouter tous les items à la commande
        for (OrderItem item : currentOrderItems) {
            newOrder.addItem(item);
        }

        // Créer la commande dans la base de données
        MiniProject.getInstance().getDatabase().createOrder(newOrder);

        // Réinitialiser le formulaire
        currentOrderItems.clear();
        orderItemsTableModel.setRowCount(0);

        // Fermer la fenêtre de dialogue
        createOrderDialog.dispose();

        // Rafraîchir la liste des commandes
        refreshData();

        JOptionPane.showMessageDialog(this,
                "Commande créée avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadOrderData() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Order order : orders) {
            Object[] rowData = {
                    order.getId(),
                    order.getClient().toString(),
                    order.getOrderDate().format(formatter),
                    order.getStatus().getDisplayName(),
                    String.format("%.2f €", order.getTotalAmount())
            };
            tableModel.addRow(rowData);
        }
    }

    public void refreshData() {
        this.orders = MiniProject.getInstance().getDatabase().getAllOrders();
        this.loadOrderData();
    }
} 