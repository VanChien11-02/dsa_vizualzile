package org.cuoi_ki.bai1;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
class ConversionPanel extends JPanel {
    private String status = "Ready";
    private int stepNumber = 0;
    private String partialOutput = "";
    private List<String> stackElements = new ArrayList<>(); //Danh sách các phần tử hiện đang nằm trong stack
    private boolean isFinalResult = false;
    private JTextArea logArea;
    private List<String> tokens = new ArrayList<>();
    private int currentIndex = -1;

    public void setExpression(String expression) {
        tokens.clear();

        StringBuilder number = new StringBuilder();
        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                number.append(c); // gom chữ số hoặc dấu thập phân
            } else if (c != ' ') { // gặp toán tử hoặc dấu ngoặc
                if (!number.isEmpty()) {
                    tokens.add(number.toString());
                    number.setLength(0); // reset
                }
                tokens.add(String.valueOf(c)); // thêm toán tử hoặc ngoặc
            }
        }
// nếu còn số cuối cùng
        if (!number.isEmpty()) {
            tokens.add(number.toString());
        }
        repaint();
    }

    public ConversionPanel() {
        setBackground(new Color(245, 245, 250));
        setPreferredSize(new Dimension(400, 500));
        setLayout(new BorderLayout());

// Drawing area
        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

// Draw step number
                if (stepNumber > 0 && !isFinalResult) {
                    g2.setColor(new Color(255, 87, 34));
                    g2.setFont(new Font("Arial", Font.BOLD, 16));
                    g2.drawString("Step " + stepNumber, 15, 25);
                } else if (isFinalResult) {
                    g2.setColor(new Color(76, 175, 80));
                    g2.setFont(new Font("Arial", Font.BOLD, 16));
                    g2.drawString("RESULT", 15, 25);
                }
// Draw status
                g2.setColor(new Color(33, 37, 41));
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                g2.drawString("Status: " + status, 15, 50);

// Draw Expression
                if (!tokens.isEmpty()) {
                    g2.setFont(new Font("Consolas", Font.PLAIN, 14));
                    g2.setColor(new Color(33, 37, 41));
                    g2.drawString("Expression:", 15, 100);

                    int x = 110;
                    for (int i = 0; i < tokens.size(); i++) {
                        String tk = tokens.get(i);
                        if (i == currentIndex) {
                            g2.setColor(new Color(255, 215, 0)); // highlight vàng
                            g2.fillRoundRect(x - 3, 86, tk.length() * 9 + 6, 20, 5, 5);
                        }
                        g2.setColor(Color.BLACK);
                        g2.drawString(tk, x, 100);
                        x += tk.length() * 9 + 12;
                    }
                }

// Draw output
                g2.setFont(new Font("Consolas", Font.PLAIN, 13));
                g2.drawString("Output: " + (partialOutput.isEmpty() ? "[]" : partialOutput), 15, 75);
// Draw stack
                int x = getWidth() - 130;
                g2.setColor(new Color(46, 196, 182));
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("STACK", x + 25, 120);
                int boxHeight = 40;
                int boxWidth = 100;
                int startY = 140;
                for (int i = 0; i < stackElements.size(); i++) {
                    int y = startY + i * (boxHeight + 10);
                    if (y + boxHeight > getHeight() - 20) break;
                    g2.setColor(new Color(46, 196, 182));
                    g2.fillRoundRect(x, y, boxWidth, boxHeight, 12, 12);
                    g2.setColor(new Color(30, 160, 150));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(x, y, boxWidth, boxHeight, 12, 12);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Consolas", Font.BOLD, 15));
                    String value = stackElements.get(i);
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(value);
                    g2.drawString(value, x + (boxWidth - textWidth) / 2, y + boxHeight / 2 + 5);
                    if (i == 0) {
                        g2.setColor(new Color(255, 152, 0));
                        g2.setFont(new Font("Arial", Font.BOLD, 11));
                        g2.drawString("Top", x - 28, y + boxHeight / 2 + 4);
                    }
                }
                if (!stackElements.isEmpty()) {
                    int bottomY = startY + Math.min(stackElements.size(), 8) * (boxHeight + 10);
                    g2.setColor(new Color(150, 150, 150));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(x - 10, bottomY, x + boxWidth + 10, bottomY);
                }
            }
        };
        drawingPanel.setBackground(new Color(245, 245, 250));
        add(drawingPanel, BorderLayout.CENTER);

// Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(new Color(245, 245, 250));
        logArea.setForeground(new Color(33, 37, 41));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(400, 150)); // Adjust height as needed
        add(scrollPane, BorderLayout.SOUTH);
    }

    public void setCurrentStep(ExpressionConverter.ConversionStep step, int stepNum) {
        this.stepNumber = stepNum;
        this.status = "Processing " + (step.currentChar.isEmpty() ? "operator" : "'" + step.currentChar + "'");
        this.partialOutput = step.partialOutput;
        this.isFinalResult = false;

        // Xác định vị trí token đang xử lý
        this.currentIndex = tokens.indexOf(step.currentChar);

        // Cập nhật stack
        String stackStr = step.stackState.replaceAll("[\\[\\]]", "").trim();
        stackElements = stackStr.isEmpty() ? new ArrayList<>() :
                new ArrayList<>(Arrays.asList(stackStr.split(", ")));

        // Log
        logArea.append("Step " + stepNum + ": " + status + "\n");
        logArea.append("Output: " + (partialOutput.isEmpty() ? "[]" : partialOutput) + "\n");
        logArea.append("Stack: " + stackElements + "\n\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());

        repaint();
    }

    public void setFinalResult(String result) {
        this.stepNumber = 0;
        this.status = result;
        this.partialOutput = result;
        this.stackElements = new ArrayList<>();
        this.isFinalResult = true;
// Append to log
        logArea.append(status + "\n\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
        repaint();
    }
    public void reset() {
        this.stepNumber = 0;
        this.status = "Ready";
        this.partialOutput = "";
        this.stackElements = new ArrayList<>();
        this.isFinalResult = false;
        tokens.clear();
        logArea.setText("");
        currentIndex = -1;
        repaint();
    }
}