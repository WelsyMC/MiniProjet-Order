package dev.yanallah.utils;

import dev.yanallah.models.Client;
import dev.yanallah.models.Order;
import dev.yanallah.models.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class BonGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Génère un bon de commande pour une commande en préparation
     * @param client Le client de la commande
     * @param order La commande
     */
    public static void generateCommande(Client client, Order order) {
        try {
            String fileName = "bon_commande_" + order.getId() + ".txt";
            File file = new File(fileName);
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("═══════════════════════════════════════════════════════════════\n");
                writer.write("                        BON DE COMMANDE                        \n");
                writer.write("═══════════════════════════════════════════════════════════════\n\n");
                
                writer.write("Commande N° : " + order.getId() + "\n");
                writer.write("Date : " + order.getOrderDate().format(DATE_FORMATTER) + "\n");
                writer.write("Statut : " + order.getStatus().getDisplayName() + "\n\n");
                
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write("                    INFORMATIONS CLIENT                        \n");
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write("Nom : " + client.getNom() + " " + client.getPrenom() + "\n");
                writer.write("Email : " + client.getEmail() + "\n");
                writer.write("Téléphone : " + client.getTelephone() + "\n");
                writer.write("Adresse : " + client.getAdresse() + "\n\n");
                
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write("                       ARTICLES COMMANDÉS                      \n");
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write(String.format("%-30s %10s %15s %15s\n", "Produit", "Quantité", "Prix unitaire", "Total"));
                writer.write("───────────────────────────────────────────────────────────────\n");
                
                double totalCommande = 0;
                for (OrderItem item : order.getItems()) {
                    String productName = item.getStockItem().getName();
                    if (productName.length() > 28) {
                        productName = productName.substring(0, 28) + "..";
                    }
                    
                    writer.write(String.format("%-30s %10d %15.2f € %15.2f €\n",
                        productName,
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()));
                    totalCommande += item.getTotalPrice();
                }
                
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write(String.format("%58s %15.2f €\n", "TOTAL COMMANDE :", totalCommande));
                writer.write("═══════════════════════════════════════════════════════════════\n\n");
                
                writer.write("Instructions de préparation :\n");
                writer.write("• Vérifier la disponibilité de tous les articles\n");
                writer.write("• Préparer les articles selon les quantités indiquées\n");
                writer.write("• Vérifier l'état des produits avant emballage\n");
                writer.write("• Emballer soigneusement pour éviter les dommages\n\n");
                
                writer.write("Date de génération : " + java.time.LocalDateTime.now().format(DATE_FORMATTER) + "\n");
                writer.write("═══════════════════════════════════════════════════════════════\n");
            }
            
            // Ouvrir le fichier avec l'application par défaut
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
            JOptionPane.showMessageDialog(null,
                "Bon de commande généré avec succès !\nFichier : " + file.getAbsolutePath(),
                "Génération réussie",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Erreur lors de la génération du bon de commande :\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Génère un bon de livraison pour une commande envoyée
     * @param client Le client de la commande
     * @param order La commande
     */
    public static void generateLivraison(Client client, Order order) {
        try {
            String fileName = "bon_livraison_" + order.getId() + ".txt";
            File file = new File(fileName);
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("═══════════════════════════════════════════════════════════════\n");
                writer.write("                        BON DE LIVRAISON                       \n");
                writer.write("═══════════════════════════════════════════════════════════════\n\n");
                
                writer.write("Commande N° : " + order.getId() + "\n");
                writer.write("Date de commande : " + order.getOrderDate().format(DATE_FORMATTER) + "\n");
                writer.write("Date d'expédition : " + java.time.LocalDateTime.now().format(DATE_FORMATTER) + "\n");
                writer.write("Statut : " + order.getStatus().getDisplayName() + "\n\n");
                
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write("                    ADRESSE DE LIVRAISON                       \n");
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write(client.getNom() + " " + client.getPrenom() + "\n");
                writer.write(client.getAdresse() + "\n");
                writer.write("Tél : " + client.getTelephone() + "\n");
                writer.write("Email : " + client.getEmail() + "\n\n");
                
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write("                       ARTICLES LIVRÉS                         \n");
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write(String.format("%-40s %10s %15s\n", "Produit", "Quantité", "Prix unitaire"));
                writer.write("───────────────────────────────────────────────────────────────\n");
                
                double totalLivraison = 0;
                for (OrderItem item : order.getItems()) {
                    String productName = item.getStockItem().getName();
                    if (productName.length() > 38) {
                        productName = productName.substring(0, 38) + "..";
                    }
                    
                    writer.write(String.format("%-40s %10d %15.2f €\n",
                        productName,
                        item.getQuantity(),
                        item.getUnitPrice()));
                    totalLivraison += item.getTotalPrice();
                }
                
                writer.write("───────────────────────────────────────────────────────────────\n");
                writer.write(String.format("%53s %15.2f €\n", "TOTAL LIVRAISON :", totalLivraison));
                writer.write("═══════════════════════════════════════════════════════════════\n\n");
                
                writer.write("Conditions de livraison :\n");
                writer.write("• Vérifier l'identité du destinataire\n");
                writer.write("• Contrôler l'état des articles à la réception\n");
                writer.write("• Signaler tout dommage ou article manquant\n");
                writer.write("• Conserver ce bon comme preuve de livraison\n\n");
                
                writer.write("Signature du livreur : ________________    Date : ____________\n\n");
                writer.write("Signature du destinataire : ________________    Date : ____________\n\n");
                
                writer.write("Date de génération : " + java.time.LocalDateTime.now().format(DATE_FORMATTER) + "\n");
                writer.write("═══════════════════════════════════════════════════════════════\n");
            }
            
            // Ouvrir le fichier avec l'application par défaut
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
            JOptionPane.showMessageDialog(null,
                "Bon de livraison généré avec succès !\nFichier : " + file.getAbsolutePath(),
                "Génération réussie",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Erreur lors de la génération du bon de livraison :\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 