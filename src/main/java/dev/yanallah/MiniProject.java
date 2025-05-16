package dev.yanallah;

import dev.yanallah.ui.MainFrame;

public class MiniProject {
    private static MiniProject instance;

    private String[] args;
    private MainFrame frame;

    public MiniProject(String[] args) {
        this.args = args;
        instance = this;
    }

    public void start(){
        this.frame = new MainFrame();
        this.frame.setVisible(true);
    }

    public MainFrame getMainFrame() {
        return frame;
    }

    public static MiniProject getInstance() {
        return instance;
    }
}
