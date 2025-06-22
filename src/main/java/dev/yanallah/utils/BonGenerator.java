package dev.yanallah.utils;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import dev.yanallah.models.Client;
import dev.yanallah.models.Order;
import dev.yanallah.models.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class BonGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Génère un bon de commande pour une commande en préparation
     * @param client Le client de la commande
     * @param order La commande
     */
    public static void generateCommande(Client client, Order order) throws IOException {
        createPdfFolder();

        String fileName = "pdf/bon_commande_" + order.getId() + ".pdf";

        // Prépare le PDF
        PdfWriter pdfWriter = new PdfWriter(new File(fileName));
        PdfDocument pdfDoc = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(36, 36, 36, 36); // marges

        // Police
        PdfFont font = PdfFontFactory.createFont();
        PdfFont boldFont = PdfFontFactory.createFont();

        // Titre
        Paragraph title = new Paragraph("BON DE COMMANDE")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Infos de la commande
        Table orderInfo = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        orderInfo.setWidth(UnitValue.createPercentValue(100));
        orderInfo.addCell(new Cell().add(new Paragraph("Commande N°: " + order.getId()).setFont(font)).setBorder(Border.NO_BORDER));
        orderInfo.addCell(new Cell().add(new Paragraph("Date : " + order.getOrderDate().format(DATE_FORMATTER)).setFont(font)).setBorder(Border.NO_BORDER));
        orderInfo.addCell(new Cell().add(new Paragraph("Statut : " + order.getStatus().getDisplayName()).setFont(font)).setBorder(Border.NO_BORDER));
        orderInfo.addCell(new Cell().add(new Paragraph("Client : " + client.getNom() + " " + client.getPrenom()).setFont(font)).setBorder(Border.NO_BORDER));
        orderInfo.setMarginBottom(20);
        document.add(orderInfo);

        // Table d'articles
        Table articleTable = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}));
        articleTable.setWidth(UnitValue.createPercentValue(100));
        articleTable.addHeaderCell(new Cell().add(new Paragraph("Produit").setFont(boldFont)).setBackgroundColor(new DeviceRgb(200, 200, 200)));
        articleTable.addHeaderCell(new Cell().add(new Paragraph("Qté").setFont(boldFont)).setBackgroundColor(new DeviceRgb(200, 200, 200)));
        articleTable.addHeaderCell(new Cell().add(new Paragraph("Prix unitaire").setFont(boldFont)).setBackgroundColor(new DeviceRgb(200, 200, 200)));
        articleTable.addHeaderCell(new Cell().add(new Paragraph("Total").setFont(boldFont)).setBackgroundColor(new DeviceRgb(200, 200, 200)));

        double totalCommande = 0;
        for (OrderItem item : order.getItems()) {
            articleTable.addCell(new Cell().add(new Paragraph(item.getStockItem().getName()).setFont(font)));
            articleTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFont(font)));
            articleTable.addCell(new Cell().add(new Paragraph(String.format("%.2f €", item.getUnitPrice())).setFont(font)));
            articleTable.addCell(new Cell().add(new Paragraph(String.format("%.2f €", item.getTotalPrice())).setFont(font)));

            totalCommande += item.getTotalPrice();
        }

        document.add(articleTable);

        // Total
        Paragraph total = new Paragraph("TOTAL : " + String.format("%.2f €", totalCommande))
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20);
        document.add(total);

        // Notes
        Paragraph notes = new Paragraph(
                "Instructions de préparation:\n" +
                        "• Vérifier la disponibilité des articles\n" +
                        "• Préparer les articles selon les quantités\n" +
                        "• Vérifier l'état des produits avant emballage\n" +
                        "• Emballer soigneusement pour éviter les dommages\n"
        ).setFont(font).setMarginTop(20);
        document.add(notes);

        document.close();
        
        // Ouvrir le fichier avec l'application par défaut
        File file = new File(fileName).getAbsoluteFile();
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file.getParentFile());
        }
    }
    /**
     * Génère un bon de livraison pour une commande envoyée
     * @param client Le client de la commande
     * @param order La commande
     */
    public static void generateLivraison(Client client, Order order) throws IOException {
        createPdfFolder();
        String fileName = "pdf/bon_livraison_" + order.getId() + ".pdf";

        // Prépare le PDF
        PdfWriter pdfWriter = new PdfWriter(new File(fileName));
        PdfDocument pdfDoc = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(36, 36, 36, 36); // marges

        // Police
        PdfFont font = PdfFontFactory.createFont();
        PdfFont boldFont = PdfFontFactory.createFont();

        // Titre
        Paragraph title = new Paragraph("BON DE LIVRAISON")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Infos de la commande
        Table orderInfo = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        orderInfo.setWidth(UnitValue.createPercentValue(100));
        orderInfo.addCell(new Cell().add(new Paragraph("Commande N°: " + order.getId()).setFont(font)).setBorder(Border.NO_BORDER));
        orderInfo.addCell(new Cell().add(new Paragraph("Date de commande : " + order.getOrderDate().format(DATE_FORMATTER)).setFont(font)).setBorder(Border.NO_BORDER));
        orderInfo.addCell(new Cell().add(new Paragraph("Date d'expédition : " + java.time.LocalDateTime.now().format(DATE_FORMATTER)).setFont(font)).setBorder(Border.NO_BORDER));
        orderInfo.addCell(new Cell().add(new Paragraph("Statut : " + order.getStatus().getDisplayName()).setFont(font)).setBorder(Border.NO_BORDER));
        orderInfo.setMarginBottom(20);
        document.add(orderInfo);

        // Adresse de livraison
        Paragraph adresseTitre = new Paragraph("ADRESSE DE LIVRAISON")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginBottom(10);
        document.add(adresseTitre);

        Paragraph adresse = new Paragraph(
                client.getNom() + " " + client.getPrenom() + "\n" +
                client.getAdresse() + "\n" +
                "Tél : " + client.getTelephone() + "\n" +
                "Email : " + client.getEmail()
        ).setFont(font).setMarginBottom(20);
        document.add(adresse);

        // Table d'articles
        Table articleTable = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2}));
        articleTable.setWidth(UnitValue.createPercentValue(100));
        articleTable.addHeaderCell(new Cell().add(new Paragraph("Produit").setFont(boldFont)).setBackgroundColor(new DeviceRgb(200, 200, 200)));
        articleTable.addHeaderCell(new Cell().add(new Paragraph("Quantité").setFont(boldFont)).setBackgroundColor(new DeviceRgb(200, 200, 200)));
        articleTable.addHeaderCell(new Cell().add(new Paragraph("Prix unitaire").setFont(boldFont)).setBackgroundColor(new DeviceRgb(200, 200, 200)));

        double totalLivraison = 0;
        for (OrderItem item : order.getItems()) {
            articleTable.addCell(new Cell().add(new Paragraph(item.getStockItem().getName()).setFont(font)));
            articleTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFont(font)));
            articleTable.addCell(new Cell().add(new Paragraph(String.format("%.2f €", item.getUnitPrice())).setFont(font)));

            totalLivraison += item.getTotalPrice();
        }

        document.add(articleTable);

        // Total
        Paragraph total = new Paragraph("TOTAL LIVRAISON : " + String.format("%.2f €", totalLivraison))
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20);
        document.add(total);

        // Conditions de livraison
        Paragraph conditions = new Paragraph(
                "Conditions de livraison :\n" +
                        "• Vérifier l'identité du destinataire\n" +
                        "• Contrôler l'état des articles à la réception\n" +
                        "• Signaler tout dommage ou article manquant\n" +
                        "• Conserver ce bon comme preuve de livraison\n"
        ).setFont(font).setMarginTop(20);
        document.add(conditions);

        // Signatures
        Table signaturesTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        signaturesTable.setWidth(UnitValue.createPercentValue(100));
        signaturesTable.setMarginTop(30);
        
        signaturesTable.addCell(new Cell().add(new Paragraph("Signature du livreur :\n\n\n\nDate : ____________").setFont(font)).setBorder(Border.NO_BORDER));
        signaturesTable.addCell(new Cell().add(new Paragraph("Signature du destinataire :\n\n\n\nDate : ____________").setFont(font)).setBorder(Border.NO_BORDER));
        
        document.add(signaturesTable);

        document.close();
        
        // Ouvrir le fichier avec l'application par défaut
        File file = new File(fileName).getAbsoluteFile();
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file.getParentFile());
        }
    }

    private static void createPdfFolder(){
        final File pdfFolder = new File("pdf");

        if(!pdfFolder.exists())
            pdfFolder.mkdir();
    }
} 