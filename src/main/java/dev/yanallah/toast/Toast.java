package dev.yanallah.toast;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public enum Toast {
    INSTANCE;

    static final int LEVEL_INFO = 0;
    static final int LEVEL_SUCCESS = 1;
    static final int LEVEL_WARNING = 2;
    static final int LEVEL_ERROR = 3;
    private final List<ToastObj> toasts = new ArrayList<>();

    public void info(Component view, String message, String title) {
        final ToastObj toastObj = new ToastObj(
                System.currentTimeMillis(),
                5000L,
                title,
                message,
                LEVEL_INFO
        );

        toasts.add(toastObj);

        Window window = SwingUtilities.getWindowAncestor(view);
        if (window != null) {
            window.repaint();
        }

        // Lance un timer pour retirer le toast après sa durée
        new Timer((int) toastObj.getDurationMS(), e -> {
            toasts.remove(toastObj); // supprime le toast
            Window w = SwingUtilities.getWindowAncestor(view);
            if (w != null) {
                w.repaint(); // redessine la fenêtre
            }
        }) {{
            setRepeats(false); // une seule fois
            start();
        }};
    }

    public void success(Component view, String message, String title) {
        final ToastObj toastObj = new ToastObj(
                System.currentTimeMillis(),
                5000L,
                title,
                message,
                LEVEL_SUCCESS
        );

        toasts.add(toastObj);

        Window window = SwingUtilities.getWindowAncestor(view);
        if (window != null) {
            window.repaint();
        }

        // Lance un timer pour retirer le toast après sa durée
        new Timer((int) toastObj.getDurationMS(), e -> {
            toasts.remove(toastObj); // supprime le toast
            Window w = SwingUtilities.getWindowAncestor(view);
            if (w != null) {
                w.repaint(); // redessine la fenêtre
            }
        }) {{
            setRepeats(false); // une seule fois
            start();
        }};
    }

    public void warn(Component view, String message, String title) {
        final ToastObj toastObj = new ToastObj(
                System.currentTimeMillis(),
                5000L,
                title,
                message,
                LEVEL_WARNING
        );

        toasts.add(toastObj);

        Window window = SwingUtilities.getWindowAncestor(view);
        if (window != null) {
            window.repaint();
        }

        // Lance un timer pour retirer le toast après sa durée
        new Timer((int) toastObj.getDurationMS(), e -> {
            toasts.remove(toastObj); // supprime le toast
            Window w = SwingUtilities.getWindowAncestor(view);
            if (w != null) {
                w.repaint(); // redessine la fenêtre
            }
        }) {{
            setRepeats(false); // une seule fois
            start();
        }};
    }

    public void error(Component view, String message, String title) {
        final ToastObj toastObj = new ToastObj(
                System.currentTimeMillis(),
                5000L,
                title,
                message,
                LEVEL_ERROR
        );

        toasts.add(toastObj);

        Window window = SwingUtilities.getWindowAncestor(view);
        if (window != null) {
            window.repaint();
        }

        // Lance un timer pour retirer le toast après sa durée
        new Timer((int) toastObj.getDurationMS(), e -> {
            toasts.remove(toastObj); // supprime le toast
            Window w = SwingUtilities.getWindowAncestor(view);
            if (w != null) {
                w.repaint(); // redessine la fenêtre
            }
        }) {{
            setRepeats(false); // une seule fois
            start();
        }};
    }

    public List<ToastObj> getToasts() {
        return toasts;
    }

    public static class ToastObj {
        private long showedSince;
        private long durationMS;
        private String title;
        private String message;
        private int level;

        public ToastObj(long showedSince, long durationMS, String title, String message, int level) {
            this.showedSince = showedSince;
            this.durationMS = durationMS;
            this.title = title;
            this.message = message;
            this.level = level;
        }

        public long getDurationMS() {
            return durationMS;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public int getLevel() {
            return level;
        }

        public long getShowedSince() {
            return showedSince;
        }

        public void setShowedSince(long showedSince) {
            this.showedSince = showedSince;
        }
    }
}
