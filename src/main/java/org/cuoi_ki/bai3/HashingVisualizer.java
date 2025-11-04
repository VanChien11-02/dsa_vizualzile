package org.cuoi_ki.bai3;

import javax.swing.*;
import java.awt.*;
// Main class, tạo frame chính, chứa các panel con
public class HashingVisualizer extends JFrame {
    private ControlPanel controlPanel;
    private LogPanel logPanel;
    private HashPanel hashPanel;
    private HashTable hashTable;

    public HashingVisualizer() {
        setTitle("Hashing Visualization - Graphical View");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Khởi tạo các panel chính
        controlPanel = new ControlPanel(this);
        logPanel = new LogPanel();
        hashPanel = new HashPanel(); // << KHỞI TẠO PANEL VẼ MỚI

        // Đặt panel vẽ vào trong một JScrollPane để có thể cuộn
        JScrollPane drawingScrollPane = new JScrollPane(hashPanel);
        drawingScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        drawingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Chia đôi màn hình giữa khu vực vẽ và khu vực log
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, drawingScrollPane, logPanel);
        centerSplit.setDividerLocation(800); // Tăng không gian cho khu vực vẽ

        add(controlPanel, BorderLayout.NORTH);
        add(centerSplit, BorderLayout.CENTER);
    }

    // Phương thức kiểm tra null (giữ nguyên)
    private boolean isTableCreated() {
        if (hashTable == null) {
            JOptionPane.showMessageDialog(this, "Please create a hash table first!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // Các phương thức gọi đến HashTable (giữ nguyên)
    public void createTable(int size, String method) {
        hashTable = new HashTable(size, method, this);
        hashTable.updateHashTableView();
        logPanel.clear();
        logPanel.append("Đã tạo table có kích thước là " + size + " sử dụng " + method + "\n");
    }
    public void addValue(int value) { if (isTableCreated()) hashTable.addValue(value); }
    public void deleteValue(int value) { if (isTableCreated()) hashTable.deleteValue(value); }
    public void searchValue(int value) { if (isTableCreated()) hashTable.searchValue(value); }

    // Các phương thức Getter để các panel con có thể giao tiếp
    public LogPanel getLogPanel() { return logPanel; }
    public HashPanel getHashPanel() { return hashPanel; } // << GETTER MỚI

    // Main method (giữ nguyên)
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new HashingVisualizer().setVisible(true));
    }
}