package dev.yanallah.ui;

import dev.yanallah.ui.panels.ClientsPanel;
import dev.yanallah.ui.panels.CommandesPanel;
import dev.yanallah.ui.panels.DashboardPanel;
import dev.yanallah.ui.panels.StocksPanel;
import dev.yanallah.utils.References;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel navigationPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(){
        this.initComponents();
    }

    private void initComponents(){
        this.setTitle(References.FRAME_TITLE);
        this.setResizable(false);
        this.setSize(References.FRAME_WIDTH, References.FRAME_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Configuration du layout principal
        this.setLayout(new BorderLayout());

        // Création du panel de navigation
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setPreferredSize(new Dimension(150, 0));
        navigationPanel.setBackground(new Color(51, 51, 51));

        // Création des boutons de navigation
        JButton homeButton = createNavButton("Home");
        JButton stocksButton = createNavButton("Stocks");
        JButton commandesButton = createNavButton("Commandes");
        JButton clientsButton = createNavButton("Clients");

        // Ajout des boutons au panel de navigation
        navigationPanel.add(Box.createVerticalStrut(10));
        navigationPanel.add(homeButton);
        navigationPanel.add(Box.createVerticalStrut(10));
        navigationPanel.add(clientsButton);
        navigationPanel.add(Box.createVerticalStrut(10));
        navigationPanel.add(stocksButton);
        navigationPanel.add(Box.createVerticalStrut(10));
        navigationPanel.add(commandesButton);


        // Création du panel de contenu avec CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Création des différents panneaux
        StocksPanel stocksPanel = new StocksPanel();
        CommandesPanel commandesPanel = new CommandesPanel();
        ClientsPanel clientsPanel = new ClientsPanel();
        DashboardPanel dashboardPanel = new DashboardPanel();

        // Ajout des panneaux au CardLayout
        contentPanel.add(stocksPanel, "Stocks");
        contentPanel.add(commandesPanel, "Commandes");
        contentPanel.add(clientsPanel, "Clients");
        contentPanel.add(dashboardPanel, "Dashboard");

        // Ajout des panels à la frame
        this.add(navigationPanel, BorderLayout.WEST);
        this.add(contentPanel, BorderLayout.CENTER);

        // Configuration des actions des boutons
        stocksButton.addActionListener(e -> cardLayout.show(contentPanel, "Stocks"));
        commandesButton.addActionListener(e -> cardLayout.show(contentPanel, "Commandes"));
        clientsButton.addActionListener(e -> cardLayout.show(contentPanel, "Clients"));
        homeButton.addActionListener(e -> cardLayout.show(contentPanel, "Dashboard"));

        // Afficher le panel Stocks par défaut
        cardLayout.show(contentPanel, "Home");
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(130, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
        });

        return button;
    }
}