import ui.EditorUI;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EditorUI ui = new EditorUI();
            ui.setVisible(true);
        });
    }
}