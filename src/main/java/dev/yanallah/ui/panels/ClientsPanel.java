package dev.yanallah.ui.panels;

import dev.yanallah.models.Client;
import dev.yanallah.services.ClientService;
import dev.yanallah.toast.Toast;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientsPanel extends JPanel {
    private final ClientService clientService;
    private JTable clientsTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JPanel formPanel;
    private JTextField nomField, prenomField, emailField, telephoneField, adresseField;

    public ClientsPanel() {
        this.clientService = ClientService.getInstance();
        this.initComponents();
        this.subscribeToClientUpdates();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Titre du panel
        JLabel titleLabel = new JLabel("Gestion des Clients");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Créer le modèle de table
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Adresse"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Créer la table
        clientsTable = new JTable(tableModel);
        clientsTable.setRowHeight(30);
        clientsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        clientsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        clientsTable.setSelectionBackground(new Color(184, 207, 229));
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientsTable.setShowGrid(true);
        clientsTable.setGridColor(new Color(220, 220, 220));

        // Centrer les valeurs pour toutes les colonnes
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < clientsTable.getColumnCount(); i++) {
            clientsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Scroll pane pour la table
        scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Panel pour le formulaire d'ajout
        formPanel = createFormPanel();

        // Ajout des composants au panel principal
        this.add(titleLabel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(formPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Création des champs
        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        emailField = new JTextField(20);
        telephoneField = new JTextField(20);
        adresseField = new JTextField(20);

        // Ajout des labels et champs
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        panel.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        panel.add(prenomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1;
        panel.add(telephoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Adresse:"), gbc);
        gbc.gridx = 1;
        panel.add(adresseField, gbc);

        // Bouton d'ajout
        JButton addButton = new JButton("Ajouter un client");
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addClient());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addButton, gbc);

        return panel;
    }

    private void addClient() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || adresse.isEmpty()) {
            Toast.INSTANCE.error(this,
                    "Veuillez remplir tous les champs",
                    "Erreur");
            return;
        }

        // Ajouter le client via le service
        Client newClient = new Client(
                0, // L'ID sera généré par la base de données
                nom,
                prenom,
                email,
                telephone,
                adresse
        );

        clientService.addClient(newClient);
        Toast.INSTANCE.success(this,
                "Le client a été créé avec succès !",
                "Succès - Création de client");

        // Réinitialiser les champs
        clearForm();
    }

    private void subscribeToClientUpdates() {
        clientService.getClientsObservable().subscribe(this::loadClientData);
    }

    private void loadClientData(List<Client> clients) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            if (clients != null) {
                for (Client client : clients) {
                    Object[] rowData = {
                            client.getId(),
                            client.getNom(),
                            client.getPrenom(),
                            client.getEmail(),
                            client.getTelephone(),
                            client.getAdresse()
                    };
                    tableModel.addRow(rowData);
                }
            }
        });
    }

    private void clearForm() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        telephoneField.setText("");
        adresseField.setText("");
    }

    public void refreshData() {
        clientService.refreshClients();
    }
} 