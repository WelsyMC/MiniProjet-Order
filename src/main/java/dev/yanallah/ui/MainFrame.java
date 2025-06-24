package dev.yanallah.ui;

import dev.yanallah.toast.Toast;
import dev.yanallah.ui.panels.ClientsPanel;
import dev.yanallah.ui.panels.CommandesPanel;
import dev.yanallah.ui.panels.DashboardPanel;
import dev.yanallah.ui.panels.StocksPanel;
import dev.yanallah.utils.References;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel navigationPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private DashboardPanel dashboardPanel;
    private StocksPanel stocksPanel;
    private CommandesPanel commandesPanel;
    private ClientsPanel clientsPanel;
    private Timer animationTimer;

    public MainFrame(){
        this.initComponents();
    }

    private void initComponents(){
        this.setTitle(References.FRAME_TITLE);
        this.setResizable(true);
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

        // Création des différents panneaux avec références de classe
        stocksPanel = new StocksPanel();
        commandesPanel = new CommandesPanel();
        clientsPanel = new ClientsPanel();
        dashboardPanel = new DashboardPanel();

        // Ajout des panneaux au CardLayout
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(stocksPanel, "Stocks");
        contentPanel.add(commandesPanel, "Commandes");
        contentPanel.add(clientsPanel, "Clients");

        // Ajout des panels à la frame
        this.add(navigationPanel, BorderLayout.WEST);
        this.add(contentPanel, BorderLayout.CENTER);

        // Configuration des actions des boutons avec mise à jour des données
        homeButton.addActionListener(e -> {
            dashboardPanel.updateDashboard();
            cardLayout.show(contentPanel, "Dashboard");
        });
        stocksButton.addActionListener(e -> {
            stocksPanel.refreshData();
            cardLayout.show(contentPanel, "Stocks");
        });
        commandesButton.addActionListener(e -> {
            commandesPanel.refreshData();
            cardLayout.show(contentPanel, "Commandes");
        });
        clientsButton.addActionListener(e -> {
            clientsPanel.refreshData();
            cardLayout.show(contentPanel, "Clients");
        });

        cardLayout.show(contentPanel, "Dashboard");

        JPanel glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawAllToasts(g);
            }
        };

        glassPane.setOpaque(false);  // transparent
        setGlassPane(glassPane);
        glassPane.setVisible(true);
    }

    private void drawAllToasts(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Démarrer le timer d'animation si nécessaire
        List<Toast.ToastObj> toasts = Toast.INSTANCE.getToasts();
        if (!toasts.isEmpty() && (animationTimer == null || !animationTimer.isRunning())) {
            startAnimationTimer();
        } else if (toasts.isEmpty() && animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        // Activation de l'antialiasing pour un rendu plus lisse
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int toastWidth = 320;
        int toastHeight = 80;
        int padding = 12;
        int margin = 20;
        int startX = getGlassPane().getWidth() - toastWidth - margin;
        int startY = getGlassPane().getHeight() - toastHeight - margin;

        long currentTime = System.currentTimeMillis();

        for (Toast.ToastObj toastObj : toasts) {
            long elapsed = currentTime - toastObj.getShowedSince();
            long duration = toastObj.getDurationMS();

            // Calcul de l'opacité pour l'animation de fade-out
            float alpha = 1.0f;
            if (elapsed > duration - 500) { // Fade-out dans les 500 dernières ms
                alpha = Math.max(0, (duration - elapsed) / 500.0f);
            } else if (elapsed < 300) { // Fade-in dans les 300 premières ms
                alpha = elapsed / 300.0f;
            }

            // Calcul du décalage pour l'animation de slide-in
            int slideOffset = 0;
            if (elapsed < 300) {
                slideOffset = (int) ((1.0f - (elapsed / 300.0f)) * 50);
            }

            int currentX = startX + slideOffset;

            // Couleurs selon le niveau
            Color backgroundColor, borderColor, textColor;
            String iconText;

            switch (toastObj.getLevel()) {
                case 0: // Info
                    backgroundColor = new Color(59, 130, 246, (int)(alpha * 240));
                    borderColor = new Color(59, 130, 246, (int)(alpha * 255));
                    textColor = new Color(255, 255, 255, (int)(alpha * 255));
                    iconText = "ℹ";
                    break;
                case 1: // Success
                    backgroundColor = new Color(34, 197, 94, (int)(alpha * 240));
                    borderColor = new Color(34, 197, 94, (int)(alpha * 255));
                    textColor = new Color(255, 255, 255, (int)(alpha * 255));
                    iconText = "✓";
                    break;
                case 2: // Warning
                    backgroundColor = new Color(245, 158, 11, (int)(alpha * 240));
                    borderColor = new Color(245, 158, 11, (int)(alpha * 255));
                    textColor = new Color(255, 255, 255, (int)(alpha * 255));
                    iconText = "⚠";
                    break;
                case 3: // Error
                    backgroundColor = new Color(239, 68, 68, (int)(alpha * 240));
                    borderColor = new Color(239, 68, 68, (int)(alpha * 255));
                    textColor = new Color(255, 255, 255, (int)(alpha * 255));
                    iconText = "✕";
                    break;
                default:
                    backgroundColor = new Color(75, 85, 99, (int)(alpha * 240));
                    borderColor = new Color(75, 85, 99, (int)(alpha * 255));
                    textColor = new Color(255, 255, 255, (int)(alpha * 255));
                    iconText = "•";
            }

            // Ombre portée
            g2d.setColor(new Color(0, 0, 0, (int)(alpha * 40)));
            g2d.fillRoundRect(currentX + 3, startY + 3, toastWidth, toastHeight, 12, 12);

            // Fond du toast avec dégradé subtil
            GradientPaint gradient = new GradientPaint(
                    currentX, startY, backgroundColor,
                    currentX, startY + toastHeight, new Color(
                    backgroundColor.getRed(),
                    backgroundColor.getGreen(),
                    backgroundColor.getBlue(),
                    (int)(backgroundColor.getAlpha() * 0.8f)
            )
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(currentX, startY, toastWidth, toastHeight, 12, 12);

            // Bordure
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(currentX, startY, toastWidth, toastHeight, 12, 12);

            // Barre de progression
            if (duration > 0) {
                float progress = Math.min(1.0f, (float) elapsed / duration);
                g2d.setColor(new Color(255, 255, 255, (int)(alpha * 100)));
                g2d.fillRoundRect(currentX + 8, startY + toastHeight - 6, toastWidth - 16, 2, 1, 1);
                g2d.setColor(new Color(255, 255, 255, (int)(alpha * 200)));
                g2d.fillRoundRect(currentX + 8, startY + toastHeight - 6,
                        (int)((toastWidth - 16) * progress), 2, 1, 1);
            }

            // Icône
            g2d.setColor(textColor);
            g2d.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
            FontMetrics iconMetrics = g2d.getFontMetrics();
            int iconX = currentX + 15;
            int iconY = startY + (toastHeight - iconMetrics.getHeight()) / 2 + iconMetrics.getAscent();
            g2d.drawString(iconText, iconX, iconY);

            // Titre
            if (toastObj.getTitle() != null && !toastObj.getTitle().isEmpty()) {
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics titleMetrics = g2d.getFontMetrics();
                int titleX = currentX + 45;
                int titleY = startY + 20;

                // Truncate title if too long
                String title = toastObj.getTitle();
                int maxTitleWidth = toastWidth - 60;
                if (titleMetrics.stringWidth(title) > maxTitleWidth) {
                    while (titleMetrics.stringWidth(title + "...") > maxTitleWidth && title.length() > 1) {
                        title = title.substring(0, title.length() - 1);
                    }
                    title += "...";
                }
                g2d.drawString(title, titleX, titleY);
            }

            // Message
            if (toastObj.getMessage() != null && !toastObj.getMessage().isEmpty()) {
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                FontMetrics messageMetrics = g2d.getFontMetrics();
                int messageX = currentX + 45;
                int messageY = startY + (toastObj.getTitle() != null && !toastObj.getTitle().isEmpty() ? 40 : 32);

                // Gestion du texte multi-lignes
                String message = toastObj.getMessage();
                int maxMessageWidth = toastWidth - 60;
                String[] words = message.split(" ");
                StringBuilder currentLine = new StringBuilder();
                int lineHeight = messageMetrics.getHeight();
                int maxLines = toastObj.getTitle() != null && !toastObj.getTitle().isEmpty() ? 2 : 3;
                int currentLineNum = 0;

                for (String word : words) {
                    String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                    if (messageMetrics.stringWidth(testLine) > maxMessageWidth) {
                        if (currentLine.length() > 0) {
                            if (currentLineNum < maxLines - 1) {
                                g2d.drawString(currentLine.toString(), messageX, messageY + currentLineNum * lineHeight);
                                currentLineNum++;
                                currentLine = new StringBuilder(word);
                            } else {
                                // Dernière ligne, ajouter "..."
                                String lastLine = currentLine.toString();
                                while (messageMetrics.stringWidth(lastLine + "...") > maxMessageWidth && lastLine.length() > 1) {
                                    lastLine = lastLine.substring(0, lastLine.length() - 1);
                                }
                                g2d.drawString(lastLine + "...", messageX, messageY + currentLineNum * lineHeight);
                                break;
                            }
                        }
                    } else {
                        currentLine = new StringBuilder(testLine);
                    }
                }

                // Dessiner la dernière ligne
                if (currentLine.length() > 0 && currentLineNum < maxLines) {
                    g2d.drawString(currentLine.toString(), messageX, messageY + currentLineNum * lineHeight);
                }
            }

            startY -= (toastHeight + padding);
        }
        g2d.dispose();
    }

    private void startAnimationTimer() {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        animationTimer = new Timer(16, e -> { // ~60 FPS
            List<Toast.ToastObj> toasts = Toast.INSTANCE.getToasts();
            if (!toasts.isEmpty()) {
                // Forcer le repaint de la zone des toasts
                repaintToastArea();
            } else {
                // Arrêter le timer si plus de toasts
                animationTimer.stop();
            }
        });
        animationTimer.start();
    }

    private void repaintToastArea() {
        // Calculer la zone où sont affichés les toasts
        int toastWidth = 320;
        int toastHeight = 80;
        int padding = 12;
        int margin = 20;
        int maxToasts = 5; // Nombre maximum de toasts visibles

        int x = getGlassPane().getWidth() - toastWidth - margin - 50; // +50 pour l'animation slide
        int y = getGlassPane().getHeight() - (toastHeight + padding) * maxToasts - margin;
        int width = toastWidth + 60; // +60 pour l'ombre et l'animation
        int height = (toastHeight + padding) * maxToasts + 20;

        // Repaint seulement la zone des toasts
        getGlassPane().repaint(x, y, width, height);
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        System.out.println("paint()");
    }
}