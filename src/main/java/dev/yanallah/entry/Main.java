package dev.yanallah.entry;

import dev.yanallah.MiniProject;
import dev.yanallah.utils.Logger;

import javax.swing.*;

public class Main {
    private static final Logger LOGGER = new Logger("Main");

    public static void main(String[] args) {
        LOGGER.info("Main()");

        LOGGER.info("Modification du thème Swing pour appliquer celui de Windows...");
        try {
            applyOsStyle();
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            LOGGER.fatal("Le style OS n'a pas pu être appliqué à Swing: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        LOGGER.info("Initialisation du logiciel.");

        new MiniProject(args).start();
    }

    private static void applyOsStyle() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
}