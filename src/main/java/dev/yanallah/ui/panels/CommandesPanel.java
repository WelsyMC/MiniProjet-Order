package dev.yanallah.ui.panels;

import dev.yanallah.MiniProject;
import dev.yanallah.models.*;
import dev.yanallah.utils.BonGenerator;

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
    private JDialog editOrderDialog;
    private JPanel formPanel;
    private JComboBox<Client> clientComboBox;
    private JComboBox<StockItem> stockItemComboBox;
    private JComboBox<OrderStatus> statusComboBox;
    private JSpinner quantitySpinner;
    private List<OrderItem> currentOrderItems;
    private JTable orderItemsTable;
    private DefaultTableModel orderItemsTableModel;
    private Order selectedOrder;

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
        ordersTable.setShowGrid(true);
        ordersTable.setGridColor(new Color(220, 220, 220));

        // Ajouter le double-clic sur la table
        ordersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = ordersTable.getSelectedRow();
                    if (row != -1) {
                        selectedOrder = orders.get(row);
                        showEditOrderDialog();
                    }
                }
            }
        });

        // Centrer les valeurs pour toutes les colonnes
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < ordersTable.getColumnCount(); i++) {
            ordersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Renderer spécial pour la colonne statut avec couleurs
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (!isSelected && value != null) {
                    String statusText = value.toString();
                    // Déterminer la couleur basée sur le texte du statut
                    if (statusText.equals("Créée")) {
                        setForeground(new Color(23, 162, 184));
                    } else if (statusText.equals("En préparation")) {
                        setForeground(new Color(255, 193, 7));
                    } else if (statusText.equals("Envoyée")) {
                        setForeground(new Color(70, 130, 180));
                    } else if (statusText.equals("Reçue")) {
                        setForeground(new Color(40, 167, 69));
                    } else if (statusText.equals("Annulée")) {
                        setForeground(new Color(220, 53, 69));
                    }
                } else if (isSelected) {
                    setForeground(table.getSelectionForeground());
                }
                
                return this;
            }
        };
        ordersTable.getColumnModel().getColumn(3).setCellRenderer(statusRenderer); // Colonne statut

        // Scroll pane pour la table des commandes
        scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        scrollPane.setPreferredSize(new Dimension(800, 200));

        // Bouton pour créer une nouvelle commande
        JButton addOrderButton = new JButton("Nouvelle commande");
        addOrderButton.setBackground(new Color(70, 130, 180));
        addOrderButton.setFocusPainted(false);
        addOrderButton.addActionListener(e -> showCreateOrderDialog());

        // Bouton pour voir les détails d'une commande
        JButton viewOrderButton = new JButton("Voir détails");
        viewOrderButton.setBackground(new Color(40, 167, 69));
        viewOrderButton.setFocusPainted(false);
        viewOrderButton.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedOrder = orders.get(selectedRow);
                showViewOrderDialog();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner une commande à visualiser.",
                        "Aucune sélection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewOrderButton);
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

        // Réinitialiser la commande sélectionnée pour une nouvelle commande
        selectedOrder = null;

        // Initialiser la liste des items de la commande en cours
        currentOrderItems = new ArrayList<>();

        // Créer le formulaire
        formPanel = createFormPanel();
        createOrderDialog.add(formPanel, BorderLayout.CENTER);

        // Bouton pour fermer la fenêtre
        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> {
            selectedOrder = null; // Réinitialiser la commande sélectionnée
            createOrderDialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        createOrderDialog.add(buttonPanel, BorderLayout.SOUTH);

        createOrderDialog.setVisible(true);
    }

    private void showEditOrderDialog() {
        editOrderDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier la commande #" + selectedOrder.getId(), true);
        editOrderDialog.setLayout(new BorderLayout());
        editOrderDialog.setSize(800, 600);
        editOrderDialog.setLocationRelativeTo(this);

        // Initialiser la liste des items de la commande
        currentOrderItems = new ArrayList<>(selectedOrder.getItems());

        // Créer le formulaire
        formPanel = createFormPanel();
        editOrderDialog.add(formPanel, BorderLayout.CENTER);

        // Bouton pour fermer la fenêtre
        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> {
            selectedOrder = null; // Réinitialiser la commande sélectionnée
            editOrderDialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        editOrderDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Charger les items existants
        loadOrderItems();

        editOrderDialog.setVisible(true);
    }

    private void showViewOrderDialog() {
        JDialog viewOrderDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Détails de la commande #" + selectedOrder.getId(), true);
        viewOrderDialog.setLayout(new BorderLayout());
        viewOrderDialog.setSize(800, 600);
        viewOrderDialog.setLocationRelativeTo(this);

        // Panel principal pour les détails
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Informations générales de la commande
        JPanel infoPanel = createOrderInfoPanel();
        detailsPanel.add(infoPanel, BorderLayout.NORTH);

        // Table des items (en lecture seule)
        String[] orderItemColumns = {"Produit", "Quantité", "Prix unitaire", "Total"};
        DefaultTableModel viewTableModel = new DefaultTableModel(orderItemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tout en lecture seule
            }
        };

        JTable viewItemsTable = new JTable(viewTableModel);
        viewItemsTable.setRowHeight(25);
        viewItemsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        viewItemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Charger les items de la commande
        for (OrderItem item : selectedOrder.getItems()) {
            viewTableModel.addRow(new Object[]{
                    item.getStockItem().getName(),
                    item.getQuantity(),
                    String.format("%.2f €", item.getUnitPrice()),
                    String.format("%.2f €", item.getTotalPrice())
            });
        }

        JScrollPane viewScrollPane = new JScrollPane(viewItemsTable);
        detailsPanel.add(viewScrollPane, BorderLayout.CENTER);

        // Panel pour le total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        JLabel totalLabel = new JLabel("Total de la commande : " + String.format("%.2f €", selectedOrder.getTotalAmount()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(70, 130, 180));
        totalPanel.add(totalLabel);
        detailsPanel.add(totalPanel, BorderLayout.SOUTH);

        viewOrderDialog.add(detailsPanel, BorderLayout.CENTER);

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Boutons conditionnels selon le statut
        OrderStatus status = selectedOrder.getStatus();
        
        // Bouton "Bon de commande" pour les statuts "En préparation" et "Envoyée"
        if (status == OrderStatus.PREPARING || status == OrderStatus.SENT) {
            JButton bonCommandeButton = new JButton("Générer bon de commande");
            bonCommandeButton.setBackground(new Color(23, 162, 184)); // Bleu info
            bonCommandeButton.setFocusPainted(false);
            bonCommandeButton.addActionListener(e -> {
                BonGenerator.generateCommande(selectedOrder.getClient(), selectedOrder);
            });
            buttonPanel.add(bonCommandeButton);
        }
        
        // Bouton "Bon de livraison" uniquement pour le statut "Envoyée"
        if (status == OrderStatus.SENT) {
            JButton bonLivraisonButton = new JButton("Générer bon de livraison");
            bonLivraisonButton.setBackground(new Color(70, 130, 180)); // Bleu primary
            bonLivraisonButton.setFocusPainted(false);
            bonLivraisonButton.addActionListener(e -> {
                BonGenerator.generateLivraison(selectedOrder.getClient(), selectedOrder);
            });
            buttonPanel.add(bonLivraisonButton);
        }
        
        // Bouton "Modifier" (toujours présent)
        JButton editButton = new JButton("Modifier");
        editButton.setBackground(new Color(255, 193, 7));
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> {
            viewOrderDialog.dispose();
            showEditOrderDialog();
        });

        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> {
            selectedOrder = null;
            viewOrderDialog.dispose();
        });

        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        viewOrderDialog.add(buttonPanel, BorderLayout.SOUTH);

        viewOrderDialog.setVisible(true);
    }

    private JPanel createOrderInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Informations de la commande"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID de la commande
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID :"), gbc);
        gbc.gridx = 1;
        JLabel idLabel = new JLabel(String.valueOf(selectedOrder.getId()));
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(idLabel, gbc);

        // Client
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Client :"), gbc);
        gbc.gridx = 1;
        String clientName = selectedOrder.getClient() != null 
            ? selectedOrder.getClient().getNom() + " " + selectedOrder.getClient().getPrenom()
            : "Client #" + selectedOrder.getClientId();
        JLabel clientLabel = new JLabel(clientName);
        clientLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(clientLabel, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Date :"), gbc);
        gbc.gridx = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        JLabel dateLabel = new JLabel(selectedOrder.getOrderDate().format(formatter));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(dateLabel, gbc);

        // Statut avec couleur
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Statut :"), gbc);
        gbc.gridx = 1;
        JLabel statusLabel = new JLabel(selectedOrder.getStatus().getDisplayName());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(getStatusColor(selectedOrder.getStatus()));
        panel.add(statusLabel, gbc);

        return panel;
    }

    private Color getStatusColor(OrderStatus status) {
        switch (status) {
            case CREATED:
                return new Color(23, 162, 184); // INFO_COLOR
            case PREPARING:
                return new Color(255, 193, 7); // WARNING_COLOR
            case SENT:
                return new Color(70, 130, 180); // PRIMARY_COLOR
            case RECEIVED:
                return new Color(40, 167, 69); // SUCCESS_COLOR
            case CANCELLED:
                return new Color(220, 53, 69); // DANGER_COLOR
            default:
                return new Color(23, 162, 184);
        }
    }

    private void loadOrderItems() {
        orderItemsTableModel.setRowCount(0);
        for (OrderItem item : currentOrderItems) {
            orderItemsTableModel.addRow(new Object[]{
                    item.getStockItem().getName(),
                    item.getQuantity(),
                    String.format("%.2f €", item.getUnitPrice()),
                    String.format("%.2f €", item.getTotalPrice())
            });
        }
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
        if (selectedOrder != null) {
            for (int i = 0; i < clientComboBox.getItemCount(); i++) {
                Client client = clientComboBox.getItemAt(i);
                if (client.getId() == selectedOrder.getClientId()) {
                    clientComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        // ComboBox pour le statut (seulement en mode édition)
        statusComboBox = new JComboBox<>(OrderStatus.values());
        statusComboBox.setPreferredSize(new Dimension(200, 30));
        statusComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OrderStatus) {
                    setText(((OrderStatus) value).getDisplayName());
                }
                return this;
            }
        });
        
        // Pré-sélectionner le statut si on édite une commande
        if (selectedOrder != null) {
            statusComboBox.setSelectedItem(selectedOrder.getStatus());
        } else {
            statusComboBox.setSelectedItem(OrderStatus.CREATED); // Statut par défaut pour nouvelle commande
        }

        // ComboBox pour les items de stock
        List<StockItem> stockItems = new ArrayList<>();
        stockItems.add(null); // Ajouter un choix vide
        stockItems.addAll(MiniProject.getInstance().getDatabase().getAllStockItems());
        stockItemComboBox = new JComboBox<>(stockItems.toArray(new StockItem[0]));
        stockItemComboBox.setPreferredSize(new Dimension(200, 30));
        
        // Renderer personnalisé pour afficher "-- Sélectionner un produit --" pour l'élément null
        stockItemComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Sélectionner un produit --");
                    setForeground(Color.GRAY);
                } else {
                    setText(value.toString());
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });

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

                            // Si la quantité dépasse le stock disponible, sélectionner le choix vide
                            if (selectedItem.getQuantityInStock() < totalQuantityRequested) {
                                final int maxAvailable = Math.max(0, selectedItem.getQuantityInStock() - (totalQuantityRequested - quantity));
                                int finalTotalQuantityRequested = totalQuantityRequested;
                                SwingUtilities.invokeLater(() -> {
                                    // Sélectionner le choix vide pour éviter la boucle infinie
                                    stockItemComboBox.setSelectedIndex(0);
                                    quantitySpinner.setValue(1);
                                    JOptionPane.showMessageDialog(CommandesPanel.this,
                                            "Stock insuffisant pour " + selectedItem.getName() + ".\n" +
                                            "Stock disponible : " + selectedItem.getQuantityInStock() + "\n" +
                                            "Quantité déjà demandée : " + (finalTotalQuantityRequested - quantity) + "\n" +
                                            "Veuillez sélectionner un autre produit ou ajuster la quantité.",
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
        addItemButton.setFocusPainted(false);
        addItemButton.addActionListener(e -> addItemToOrder());

        // Ajout des composants au panel de sélection
        gbc.gridx = 0;
        gbc.gridy = 0;
        selectionPanel.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(clientComboBox, gbc);

        // Ajouter le statut seulement si on édite une commande ou si on veut le montrer en création
        gbc.gridx = 0;
        gbc.gridy = 1;
        selectionPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(statusComboBox, gbc);
        
        // Désactiver la ComboBox du statut en mode création
        if (selectedOrder == null) {
            statusComboBox.setEnabled(false);
        }

        gbc.gridx = 0;
        gbc.gridy = 2;
        selectionPanel.add(new JLabel("Produit:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(stockItemComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        selectionPanel.add(new JLabel("Quantité:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(quantitySpinner, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
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

        // Ajouter le double-clic sur la table des items
        orderItemsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = orderItemsTable.getSelectedRow();
                    if (row != -1) {
                        OrderItem item = currentOrderItems.get(row);
                        showEditItemDialog(item, row);
                    }
                }
            }
        });

        JScrollPane orderItemsScrollPane = new JScrollPane(orderItemsTable);

        // Bouton pour sauvegarder la commande
        JButton saveOrderButton = new JButton(selectedOrder != null ? "Mettre à jour la commande" : "Créer la commande");
        saveOrderButton.setBackground(new Color(46, 139, 87));
        saveOrderButton.setFocusPainted(false);
        saveOrderButton.addActionListener(e -> {
            if (selectedOrder != null) {
                updateOrder();
            } else {
                createOrder();
            }
        });

        // Panel pour le bouton de sauvegarde
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveOrderButton);

        // Ajout des composants au panel principal
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(orderItemsScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showEditItemDialog(OrderItem item, int row) {
        JDialog dialog = new JDialog(editOrderDialog != null ? editOrderDialog : createOrderDialog, "Modifier l'item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(editOrderDialog != null ? editOrderDialog : createOrderDialog);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Spinner pour la quantité
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(item.getQuantity(), 1, 100, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setPreferredSize(new Dimension(100, 30));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Quantité:"), gbc);
        gbc.gridx = 1;
        panel.add(quantitySpinner, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton deleteButton = new JButton("Supprimer");
        JButton cancelButton = new JButton("Annuler");

        saveButton.addActionListener(e -> {
            int newQuantity = (Integer) quantitySpinner.getValue();
            item.setQuantity(newQuantity);
            currentOrderItems.set(row, item);
            loadOrderItems();
            dialog.dispose();
        });

        deleteButton.addActionListener(e -> {
            currentOrderItems.remove(row);
            loadOrderItems();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateOrder() {
        if (currentOrderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez ajouter au moins un item à la commande",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client selectedClient = (Client) clientComboBox.getSelectedItem();
        OrderStatus selectedStatus = (OrderStatus) statusComboBox.getSelectedItem();
        
        selectedOrder.setClientId(selectedClient.getId());
        selectedOrder.setStatus(selectedStatus); // Mettre à jour le statut
        selectedOrder.getItems().clear();
        selectedOrder.getItems().addAll(currentOrderItems);

        // Mettre à jour la commande dans la base de données
        MiniProject.getInstance().getDatabase().updateOrder(selectedOrder);

        // Fermer la fenêtre de dialogue
        editOrderDialog.dispose();

        // Réinitialiser la commande sélectionnée
        selectedOrder = null;

        // Rafraîchir la liste des commandes
        refreshData();

        JOptionPane.showMessageDialog(this,
                "Commande mise à jour avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addItemToOrder() {
        Client selectedClient = (Client) clientComboBox.getSelectedItem();
        StockItem selectedItem = (StockItem) stockItemComboBox.getSelectedItem();
        
        // Vérifier qu'un produit est sélectionné
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit avant d'ajouter un item.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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
            // Sélectionner le choix vide après l'erreur
            stockItemComboBox.setSelectedIndex(0);
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

        // Réinitialiser le formulaire
        SwingUtilities.invokeLater(() -> {
            stockItemComboBox.setSelectedIndex(0); // Remettre sur le choix vide
            quantitySpinner.setValue(1);
        });
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
        OrderStatus selectedStatus = (OrderStatus) statusComboBox.getSelectedItem();
        Order newOrder = new Order(0, selectedClient.getId(), LocalDateTime.now(), selectedStatus);

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

        // Réinitialiser la commande sélectionnée
        selectedOrder = null;

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