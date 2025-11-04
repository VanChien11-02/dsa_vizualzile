package org.cuoi_ki.bai2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private FamilyTree familyTree;  // quản lý cây gia phả
    private FamilyTreePanel treePanel; //Panel hiển thị sơ đồ cây bằng đồ họa
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, idField, birthYearField, parentIdField;
    private JComboBox<String> genderComboBox;
    private JTextArea statsArea; //Hiển thị thống kê số thế hệ, số con, cháu

    public MainFrame() {
        familyTree = new FamilyTree();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Quản Lý Cây Gia Phả");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Split chính: Trái (Input+Table) | Phải (Tree+Stats)
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(400);

        // Bên trái
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Split dọc cho bên trái: Trên (Input) | Dưới (Table)
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setDividerLocation(280);

        // Input Panel (Đỏ)
        JPanel inputPanel = createInputPanel();
        inputPanel.setPreferredSize(new Dimension(400, 280));

        // Table Panel (Xanh dương)
        JPanel tablePanel = createTablePanel();

        leftSplitPane.setTopComponent(inputPanel);
        leftSplitPane.setBottomComponent(tablePanel);
        leftPanel.add(leftSplitPane, BorderLayout.CENTER);

        // Ben phải
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Split dọc cho bên phải: Trên (Tree) | Dưới (Stats)
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setDividerLocation(500);

        // Tree Panel (Đen)
        JPanel treePanelContainer = new JPanel(new BorderLayout());
        treePanel = new FamilyTreePanel();
        JScrollPane treeScrollPane = new JScrollPane(treePanel);
        treePanelContainer.add(treeScrollPane, BorderLayout.CENTER);
        treePanelContainer.setPreferredSize(new Dimension(900, 500));

        // Stats Panel (Xanh lá)
        JPanel statsPanel = createStatsPanel();

        rightSplitPane.setTopComponent(treePanelContainer);
        rightSplitPane.setBottomComponent(statsPanel);
        rightPanel.add(rightSplitPane, BorderLayout.CENTER);

        // Thêm vào main split pane
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);

        add(mainSplitPane, BorderLayout.CENTER);

        // Thêm sự kiện click cho tree panel
        treePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Person clickedPerson = treePanel.getPersonAt(e.getX(), e.getY());
                if (clickedPerson != null) {
                    showPersonInfo(clickedPerson);
                }
            }
        });

        setSize(1400, 800);
        setLocationRelativeTo(null);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                "Thông tin thành viên"
        ));

        // Form nhập liệu
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Tên:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Năm sinh:"));
        birthYearField = new JTextField();
        formPanel.add(birthYearField);

        formPanel.add(new JLabel("Giới tính:"));
        genderComboBox = new JComboBox<>(new String[]{"Nam", "Nữ"});
        formPanel.add(genderComboBox);

        formPanel.add(new JLabel("ID Cha/Mẹ (nếu có):"));
        parentIdField = new JTextField();
        formPanel.add(parentIdField);

        panel.add(formPanel, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(this::addPerson);
        updateButton.addActionListener(this::updatePerson);
        deleteButton.addActionListener(this::deletePerson);
        clearButton.addActionListener(e -> clearFields());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                "Danh sách thành viên"
        ));

        String[] columns = {"ID", "Tên", "Năm sinh", "Giới tính", "Số con", "Số cháu"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dataTable = new JTable(tableModel);

        dataTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = dataTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    Person person = familyTree.findPersonById(id);
                    if (person != null) {
                        showPersonInfo(person);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(dataTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GREEN, 2),
                "Thống kê"
        ));

        statsArea = new JTextArea(10, 30);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshStatsButton = new JButton("Cập nhật thống kê");
        refreshStatsButton.addActionListener(e -> updateStatistics());
        panel.add(refreshStatsButton, BorderLayout.SOUTH);

        return panel;
    }

    private void addPerson(ActionEvent e) {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String birthYearText = birthYearField.getText().trim();
            String gender = (String) genderComboBox.getSelectedItem();
            String parentId = parentIdField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || birthYearText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
                return;
            }

            int birthYear = Integer.parseInt(birthYearText);

            if (familyTree.findPersonById(id) != null) {
                JOptionPane.showMessageDialog(this, "ID đã tồn tại! Vui lòng chọn ID khác.");
                return;
            }

            Person newPerson = new Person(id, name, birthYear, gender);

            if (familyTree.getRoot() == null) {
                familyTree.setRoot(newPerson);
                JOptionPane.showMessageDialog(this, "Đã thêm thành viên gốc!");
            } else if (!parentId.isEmpty()) {
                Person parent = familyTree.findPersonById(parentId);
                if (parent != null) {
                    familyTree.addPerson(parent, newPerson);
                    JOptionPane.showMessageDialog(this, "Đã thêm con của " + parent.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy cha/mẹ với ID: " + parentId);
                    return;
                }
            } else {
                familyTree.addPerson(newPerson);
                JOptionPane.showMessageDialog(this, "Đã thêm thành viên (chưa có quan hệ cha-con)");
            }

            refreshDisplay();
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Năm sinh phải là số");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updatePerson(ActionEvent e) {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID để sửa");
            return;
        }

        Person person = familyTree.findPersonById(id);
        if (person != null) {
            try {
                String name = nameField.getText().trim();
                String birthYearText = birthYearField.getText().trim();
                String gender = (String) genderComboBox.getSelectedItem();

                if (name.isEmpty() || birthYearText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
                    return;
                }

                int birthYear = Integer.parseInt(birthYearText);

                person.setName(name);
                person.setBirthYear(birthYear);
                person.setGender(gender);

                refreshDisplay();
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Năm sinh phải là số");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thành viên với ID: " + id);
        }
    }

    private void deletePerson(ActionEvent e) {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID để xóa");
            return;
        }

        Person person = familyTree.findPersonById(id);
        if (person != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa " + person.getName() + " và tất cả con cháu?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                familyTree.removePerson(person);
                refreshDisplay();
                clearFields();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thành viên với ID: " + id);
        }
    }

    private void showPersonInfo(Person person) {
        idField.setText(person.getId());
        nameField.setText(person.getName());
        birthYearField.setText(String.valueOf(person.getBirthYear()));
        genderComboBox.setSelectedItem(person.getGender());

        if (person.getParent() != null) {
            parentIdField.setText(person.getParent().getId());
        } else {
            parentIdField.setText("");
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        birthYearField.setText("");
        parentIdField.setText("");
    }

    private void refreshDisplay() {
        treePanel.setFamilyTree(familyTree);
        updateTable();
        updateStatistics();
        treePanel.repaint();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        java.util.List<Person> allPeople = familyTree.getAllPeople();
        for (Person person : allPeople) {
            tableModel.addRow(new Object[]{
                    person.getId(),
                    person.getName(),
                    person.getBirthYear(),
                    person.getGender(),
                    person.getNumberOfChildren(),
                    person.getNumberOfDescendants() - person.getNumberOfChildren()
            });
        }
    }

    private void updateStatistics() {
        StringBuilder stats = new StringBuilder();

        if (familyTree.getRoot() != null) {
            stats.append("Tổng số thành viên: ").append(familyTree.getTotalPeople()).append("\n");
            stats.append("Số thế hệ: ").append(familyTree.getTotalGenerations() + 1).append("\n\n");

            stats.append("Thành viên theo thế hệ:\n");
            for (int i = 0; i <= familyTree.getTotalGenerations(); i++) {
                java.util.List<Person> generation = familyTree.getPeopleInGeneration(i);
                stats.append("Thế hệ ").append(i + 1).append(": ").append(generation.size()).append(" người\n");
                for (Person person : generation) {
                    stats.append("  - ").append(person.getName())
                            .append(" (").append(person.getBirthYear()).append(")")
                            .append(" - ").append(person.getNumberOfChildren()).append(" con")
                            .append(", ").append(person.getNumberOfDescendants() - person.getNumberOfChildren())
                            .append(" cháu\n");
                }
                stats.append("\n");
            }
        } else {
            stats.append("Chưa có dữ liệu gia phả\n");
            stats.append("Hãy thêm thành viên đầu tiên (thành viên gốc)");
        }

        statsArea.setText(stats.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new MainFrame().setVisible(true);
        });
    }
}