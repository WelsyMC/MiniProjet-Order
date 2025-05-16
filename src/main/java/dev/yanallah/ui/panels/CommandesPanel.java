package dev.yanallah.ui.panels;

import javax.swing.*;
import java.awt.*;

public class CommandesPanel extends JPanel {
    
    public CommandesPanel() {
        this.initComponents();
    }
    
    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        
        // Titre du panel
        JLabel titleLabel = new JLabel("Gestion des Commandes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        this.add(titleLabel, BorderLayout.NORTH);
    }
} 