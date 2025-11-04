package org.cuoi_ki.bai3;
// Vùng hiển thị log (text area + auto-scroll)
import javax.swing.*;

public class LogPanel extends JScrollPane {
    private JTextArea area;

    public LogPanel() {
        area = new JTextArea();
        area.setEditable(false);
        setViewportView(area);
    }

    public void append(String text) {
        area.append(text);
        SwingUtilities.invokeLater(() -> area.setCaretPosition(area.getDocument().getLength()));
    }

    public void clear() {
        area.setText("");
    }
}

