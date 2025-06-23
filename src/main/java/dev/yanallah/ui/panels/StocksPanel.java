package dev.yanallah.ui.panels;

import dev.yanallah.MiniProject;
import dev.yanallah.models.StockItem;
import dev.yanallah.toast.Toast;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class StocksPanel extends JPanel {

    private List<StockItem> stocks;
    private JTable stocksTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JPanel infoPanel;
    private JLabel totalItemsLabel;

    public StocksPanel() {
        this.initComponents();
        this.stocks = MiniProject.getInstance().getDatabase().getAllStockItems();
        this.loadStockData();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Titre du panel
        JLabel titleLabel = new JLabel("Gestion des Stocks");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Créer le modèle de table avec les colonnes
        String[] columnNames = {"Nom du produit", "Quantité en stock", "Prix unitaire"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Seule la colonne quantité (index 1) est éditable
                return column == 1;
            }

            @Override
            public void setValueAt(Object value, int row, int col) {
                if (col == 1) { // Colonne quantité
                    try {
                        int newQuantity = Integer.parseInt(value.toString());
                        if (newQuantity >= 0) {
                            StockItem item = stocks.get(row);
                            onQuantityChanged(item, newQuantity);
                            super.setValueAt(newQuantity, row, col);
                            // Mettre à jour le total après modification
                            updateTotalItems();
                        } else {
                            JOptionPane.showMessageDialog(StocksPanel.this,
                                    "La quantité doit être un nombre positif ou zéro.",
                                    "Erreur de saisie",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(StocksPanel.this,
                                "Veuillez saisir un nombre valide.",
                                "Erreur de saisie",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    super.setValueAt(value, row, col);
                }
            }
        };

        // Créer la table avec le modèle
        stocksTable = new JTable(tableModel);
        stocksTable.setRowHeight(30);
        stocksTable.setFont(new Font("Arial", Font.PLAIN, 14));
        stocksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        stocksTable.setSelectionBackground(new Color(184, 207, 229));
        stocksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stocksTable.setShowGrid(true);
        stocksTable.setGridColor(new Color(220, 220, 220));

        // Centrer les valeurs pour la colonne "Quantité"
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        stocksTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Formatter la colonne de prix
        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                if (value instanceof Double) {
                    value = currencyFormatter.format(value);
                }

                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        priceRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        stocksTable.getColumnModel().getColumn(2).setCellRenderer(priceRenderer);

        // Gérer les largeurs des colonnes
        stocksTable.getColumnModel().getColumn(0).setPreferredWidth(350); // Nom produit
        stocksTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Quantité
        stocksTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Prix

        // Scroll pane pour la table
        scrollPane = new JScrollPane(stocksTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Panel pour les informations supplémentaires et statistiques
        infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        totalItemsLabel = new JLabel("Total des articles dans le stock: 0", SwingConstants.CENTER);
        totalItemsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(totalItemsLabel);

        // Ajout des composants au panel principal
        this.add(titleLabel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(infoPanel, BorderLayout.SOUTH);
    }

    private void loadStockData() {
        // Vider la table
        tableModel.setRowCount(0);

        // Ajouter les données des stocks
        for (StockItem item : stocks) {
            Object[] rowData = {
                    item.getName(),
                    item.getQuantityInStock(),
                    item.getPrice()
            };
            tableModel.addRow(rowData);
        }

        // Mettre à jour les informations
        updateTotalItems();
    }

    private void updateTotalItems() {
        final int totalItems = stocks.stream().reduce(0, (sub, el) -> sub + el.getQuantityInStock(), Integer::sum);
        totalItemsLabel.setText("Total des articles dans le stock: " + totalItems);
    }

    /**
     * Méthode appelée lorsque la quantité d'un article est modifiée
     * @param item L'article dont la quantité a été modifiée
     * @param newQuantity La nouvelle quantité
     */
    protected void onQuantityChanged(StockItem item, int newQuantity) {
        if(item.getQuantityInStock() == newQuantity)return;

        item.setQuantityInStock(newQuantity);

        MiniProject.getInstance().getDatabase().updateStock(item, newQuantity);
        Toast.INSTANCE.success(
                this,
                "Quantité modifiée !",
                    String.format(
                            "La quantité pour l'article \"%s\" a été modifiée avec succès ! Nouvelle quantité: %d",
                            item.getName(),
                            newQuantity
                    )
                );
    }

    // Méthode pour rafraîchir les données
    public void refreshData() {
        this.stocks = MiniProject.getInstance().getDatabase().getAllStockItems();
        this.loadStockData();
    }
}