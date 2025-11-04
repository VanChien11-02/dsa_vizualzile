package org.cuoi_ki.bai3;

import javax.swing.*;
import java.awt.*;
// Panel chứa slider, input, nút, comboBox
public class ControlPanel extends JPanel {
    private JSlider sizeSlider;
    private JLabel sizeLabel;
    private JTextField inputField;
    private JComboBox<String> methodCombo;
    private JButton createBtn, addBtn, deleteBtn, searchBtn, resetBtn;

    private HashingVisualizer parent;

    public ControlPanel(HashingVisualizer parent) {
        this.parent = parent;
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Sử dụng FlowLayout
        setBackground(new Color(245, 245, 250));
        initComponents();
    }

    private void initComponents() {
        // --- Panel Cấu hình Bảng ---
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình của table"));
        GridBagConstraints gbcConfig = new GridBagConstraints();
        gbcConfig.insets = new Insets(5, 5, 5, 5);

        gbcConfig.gridx = 0; gbcConfig.gridy = 0; configPanel.add(new JLabel("kích thước table:"), gbcConfig);
        sizeSlider = new JSlider(5, 50, 10);
        sizeSlider.setMajorTickSpacing(5);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        gbcConfig.gridx = 1; gbcConfig.gridy = 0; configPanel.add(sizeSlider, gbcConfig);

        sizeLabel = new JLabel("10");
        gbcConfig.gridx = 2; gbcConfig.gridy = 0; configPanel.add(sizeLabel, gbcConfig);

        gbcConfig.gridx = 0; gbcConfig.gridy = 1; configPanel.add(new JLabel("Method:"), gbcConfig);
        methodCombo = new JComboBox<>(new String[]{"Linear Probing", "Chaining"});
        gbcConfig.gridx = 1; gbcConfig.gridy = 1; configPanel.add(methodCombo, gbcConfig);

        createBtn = new JButton("Create Table");
        gbcConfig.gridx = 0; gbcConfig.gridy = 2; configPanel.add(createBtn, gbcConfig);

        resetBtn = new JButton("Reset");
        gbcConfig.gridx = 1; gbcConfig.gridy = 2; configPanel.add(resetBtn, gbcConfig);

        // --- Panel Thao tác Giá trị ---
        JPanel opsPanel = new JPanel(new GridBagLayout());
        opsPanel.setBorder(BorderFactory.createTitledBorder("Value Operations"));
        GridBagConstraints gbcOps = new GridBagConstraints();
        gbcOps.insets = new Insets(5, 5, 5, 5);

        gbcOps.gridx = 0; gbcOps.gridy = 0; opsPanel.add(new JLabel("Value:"), gbcOps);
        inputField = new JTextField(10);
        gbcOps.gridx = 1; gbcOps.gridy = 0; gbcOps.gridwidth = 3; opsPanel.add(inputField, gbcOps);

        addBtn = new JButton("Add");
        deleteBtn = new JButton("Delete");
        searchBtn = new JButton("Search");
        gbcOps.gridwidth = 1;
        gbcOps.gridx = 1; gbcOps.gridy = 1; opsPanel.add(addBtn, gbcOps);
        gbcOps.gridx = 2; gbcOps.gridy = 1; opsPanel.add(deleteBtn, gbcOps);
        gbcOps.gridx = 3; gbcOps.gridy = 1; opsPanel.add(searchBtn, gbcOps);

        // Thêm các panel con vào panel chính
        add(configPanel);
        add(opsPanel);

        // --- Action Listeners ---
        sizeSlider.addChangeListener(e -> sizeLabel.setText(String.valueOf(sizeSlider.getValue())));
        createBtn.addActionListener(e -> parent.createTable(sizeSlider.getValue(), (String) methodCombo.getSelectedItem()));
        addBtn.addActionListener(e -> handleInput("add"));
        deleteBtn.addActionListener(e -> handleInput("delete"));
        searchBtn.addActionListener(e -> handleInput("search"));
        resetBtn.addActionListener(e -> {
            parent.getLogPanel().clear();
            parent.createTable(sizeSlider.getValue(), (String) methodCombo.getSelectedItem());
        });
    }

    private void handleInput(String action) {
        String inputText = inputField.getText().trim();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập số!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int value = Integer.parseInt(inputText);
            switch (action) {
                case "add" -> parent.addValue(value);
                case "delete" -> parent.deleteValue(value);
                case "search" -> parent.searchValue(value);
            }
            inputField.setText("");
            inputField.requestFocus();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Hãy nhập số thích hợp!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}