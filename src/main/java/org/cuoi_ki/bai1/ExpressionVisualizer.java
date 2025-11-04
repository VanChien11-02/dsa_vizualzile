package org.cuoi_ki.bai1;

import javax.swing.*;
import java.awt.*;
import java.util.List;
public class ExpressionVisualizer extends JFrame {
    private JTextField tfInfix;
    private JComboBox<String> cbConversionType;
    private JButton btnPlay, btnPause, btnReset;

    private JSlider speedSlider;
    private EvaluationPanel visualPanel;
    private ConversionPanel conversionPanel;
    private ExpressionConverter converter;

    private Timer timer;
    private List<ExpressionConverter.ConversionStep> conversionSteps;
    private int currentStepIndex;
    private String finalResult;
    public int delay;
    /*conversionSteps	Danh sách các bước khi chuyển đổi Infix → Postfix/Prefix
    currentStepIndex	Chỉ số bước hiện tại
    finalResult	Kết quả cuối cùng sau khi chuyển đổi*/

    private boolean isPostfix;
    private boolean isPaused = false;

    public ExpressionVisualizer() {
        converter = new ExpressionConverter();
        setTitle("Expression Converter & Visualizer");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = createTopPanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.5);
        conversionPanel = new ConversionPanel();
        conversionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                "Conversion Process", 0, 0,
                new Font("Arial", Font.BOLD, 13), new Color(255, 140, 0)
        ));

        visualPanel = new EvaluationPanel();
        visualPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
                "Evaluation Process", 0, 0,
                new Font("Arial", Font.BOLD, 13), new Color(33, 150, 243)
        ));
        splitPane.setLeftComponent(conversionPanel);
        splitPane.setRightComponent(visualPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblExpr = new JLabel("Expression:");
        lblExpr.setFont(new Font("Arial", Font.BOLD, 13));
        tfInfix = new JTextField("(3+5)*2-8/4", 20);
        tfInfix.setFont(new Font("Consolas", Font.PLAIN, 14));
        JLabel lblType = new JLabel("Type:");
        lblType.setFont(new Font("Arial", Font.BOLD, 13));
        cbConversionType = new JComboBox<>(new String[]{"Infix -> Postfix", "Infix -> Prefix"});
        cbConversionType.setFont(new Font("Arial", Font.PLAIN, 13));

        btnPlay = createButton("Start", new Color(76, 175, 80));
        btnPause = createButton("Pause", new Color(255, 193, 7));
        btnReset = createButton("Reset", new Color(244, 67, 54));

        speedSlider = new JSlider(0, 100, 50);
        speedSlider.setMinimum(0);
        speedSlider.setMaximum(100);
        speedSlider.setValue(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setMajorTickSpacing(25); // tạo vạch 25 đơn vị

        // Tạo nhãn 0 - 25 - 50 - 75 - 100
        java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
        labelTable.put(0, new JLabel("0"));
        labelTable.put(25, new JLabel("25"));
        labelTable.put(50, new JLabel("50"));
        labelTable.put(75, new JLabel("75"));
        labelTable.put(100, new JLabel("100"));
        speedSlider.setLabelTable(labelTable);

        speedSlider.setBackground(new Color(245, 245, 250));
        JLabel lblSpeed = new JLabel("Speed:");

        lblSpeed.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblExpr, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(tfInfix, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1;
        panel.add(lblType, gbc);
        gbc.gridx = 4;
        panel.add(cbConversionType, gbc);
        gbc.gridx = 5;
        panel.add(btnPlay, gbc);
        gbc.gridx = 6;
        panel.add(btnPause, gbc);
        gbc.gridx = 7;
        panel.add(btnReset, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblSpeed, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(speedSlider, gbc);

        btnPlay.addActionListener(e -> startExpression());
        btnPause.addActionListener(e -> togglePause());
        btnReset.addActionListener(e -> resetAll());

        return panel;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private void startExpression() {
        String infix = tfInfix.getText().trim();
        if(!converter.isValidInfix(infix)){
            JOptionPane.showMessageDialog(this, "Biểu thức không hợp lệ!");
            return;
        }
        if (infix.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an expression!");
            return;
        }
        try {
            conversionPanel.reset();
            visualPanel.reset();
            String selectedType = (String) cbConversionType.getSelectedItem();
            isPostfix = selectedType.contains("Postfix");
            ExpressionConverter.ConversionResult result = isPostfix ?
                    converter.InfixToPostfix(infix) : converter.infixToPrefix(infix);
            finalResult = result.result;
            conversionSteps = result.steps;
            currentStepIndex = 0;
            delay = (int) (1250 * (2.0 - (speedSlider.getValue() / 50.0)));

            if (timer != null) timer.stop();
            timer = new Timer(delay, e -> animateConversion());
            timer.start();
            isPaused = false;
            btnPause.setText("Pause");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void animateConversion() {
        if(!isPostfix) {
            String reverseInfix = converter.reverseInfix(tfInfix.getText().trim());
            conversionPanel.setExpression(reverseInfix); // hoặc false nếu là prefix
        } else {
            conversionPanel.setExpression(tfInfix.getText().trim()); // hoặc false nếu là prefix
        }

        if (currentStepIndex < conversionSteps.size()) {
            conversionPanel.setCurrentStep(conversionSteps.get(currentStepIndex), currentStepIndex + 1);
            currentStepIndex++;
        } else {
            timer.stop();
            conversionPanel.setFinalResult((isPostfix ? "Postfix: " : "Prefix: ") + finalResult);
            visualPanel.startVisualization(this, finalResult, isPostfix);
        }
    }

    private void togglePause() {
        if (timer != null) {
            if (isPaused) {
                timer.start();
                visualPanel.resume();
                btnPause.setText("Pause");
                isPaused = false;
            } else {
                timer.stop();
                visualPanel.pause();
                btnPause.setText("Resume");
                isPaused = true;
            }
        }
    }


    private void resetAll() {
        if (timer != null) timer.stop();
        tfInfix.setText("");
        conversionPanel.reset();
        visualPanel.reset();
        cbConversionType.setSelectedIndex(0);
        speedSlider.setValue(50);
        isPaused = false;
        btnPause.setText("Pause");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ExpressionVisualizer().setVisible(true);
        });
    }
}