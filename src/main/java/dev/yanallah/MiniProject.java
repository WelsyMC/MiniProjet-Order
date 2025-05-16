package dev.yanallah;

import dev.yanallah.database.Database;
import dev.yanallah.ui.MainFrame;

public class MiniProject {
    private static MiniProject instance;

    private String[] args;
    private MainFrame frame;

    private Database database;

    public MiniProject(String[] args) {
        this.args = args;
        instance = this;
        this.database = new Database();
    }

    public void start(){
        this.frame = new MainFrame();
        this.frame.setVisible(true);
    }

    public MainFrame getMainFrame() {
        return frame;
    }

    public Database getDatabase() {
        return database;
    }

    public static MiniProject getInstance() {
        return instance;
    }
}
