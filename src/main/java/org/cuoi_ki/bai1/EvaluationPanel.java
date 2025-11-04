package org.cuoi_ki.bai1;
// Panel hiển thị Stack với animation

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
class EvaluationPanel extends JPanel {
    ExpressionVisualizer gui;
    private List<String> tokens = new ArrayList<>();//Danh sách token (số hoặc toán tử) của biểu thức (tách bằng dấu cách).
    private MyStack<Double> stack = new MyStack<>();
    private int currentIndex = 0;
    private String status = "Ready";//Trạng thái hiển thị bên trên (“Push: 5”, “Calc: 2 + 3 = 5”, v.v.).
    private Timer timer;
    private boolean isPostfix = true;
    private JTextArea logArea;//Ô hiển thị lịch sử (log) của từng bước.
    private boolean isRunning = false;

    public EvaluationPanel() {
        setBackground(new Color(30, 30, 40));
        setPreferredSize(new Dimension(400, 500));
        setLayout(new BorderLayout());

// Drawing area
        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

// Draw status
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("Status: " + status, 15, 25);

// Draw expression
                if (!tokens.isEmpty()) {
                    g2.setFont(new Font("Consolas", Font.PLAIN, 13));
                    g2.drawString("Expression: ", 15, 50);
                    int x = 105;
                    for (int i = 0; i < tokens.size(); i++) {
                        if ((isPostfix && i == currentIndex) || (!isPostfix && i == currentIndex)) {
                            g2.setColor(new Color(255, 215, 0));
                            g2.fillRoundRect(x - 3, 36, tokens.get(i).length() * 9 + 6, 20, 5, 5);
                        }
                        g2.setColor(Color.WHITE);
                        g2.drawString(tokens.get(i), x, 50);
                        x += tokens.get(i).length() * 9 + 12;
                    }
                }

// Draw stack
                int x = getWidth() - 150;
                g2.setColor(new Color(97, 218, 251));
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("STACK", x + 15, 85);
                List<Double> stackList = stack.toList();
                int boxHeight = 45;
                int boxWidth = 120;
                int startY = 100;

                for (int i = 0; i < stackList.size(); i++) {
                    int y = startY + i * (boxHeight + 10);
                    if (y + boxHeight > getHeight() - 20) break;
                    g2.setColor(new Color(97, 218, 251));
                    g2.fillRoundRect(x, y, boxWidth, boxHeight, 12, 12);
                    g2.setColor(new Color(70, 180, 220));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(x, y, boxWidth, boxHeight, 12, 12);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Consolas", Font.BOLD, 16));
                    String value = String.format("%.2f", stackList.get(i));
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(value);
                    g2.drawString(value, x + (boxWidth - textWidth) / 2, y + boxHeight / 2 + 6);
                    if (i == 0) {
                        g2.setColor(new Color(255, 215, 0));
                        g2.setFont(new Font("Arial", Font.BOLD, 11));
                        g2.drawString("Top", x - 28 , y + boxHeight / 2 + 4);
                    }
                }
                if (!stackList.isEmpty()) {
                    int bottomY = startY + Math.min(stackList.size(), 8) * (boxHeight + 10);
                    g2.setColor(new Color(150, 150, 150));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(x - 10, bottomY, x + boxWidth + 10, bottomY);
                }
            }
        };
        drawingPanel.setBackground(new Color(30, 30, 40));
        add(drawingPanel, BorderLayout.CENTER);

// Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(new Color(30, 30, 40));
        logArea.setForeground(Color.WHITE);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(400, 150)); // Adjust height as needed
        add(scrollPane, BorderLayout.SOUTH);
    }

    public void startVisualization(ExpressionVisualizer gui, String expression, boolean postfix) {
        this.gui = gui;
        if(isRunning) return;

        reset();
        this.isPostfix = postfix;
        tokens = new ArrayList<>();
        for (String token : expression.split("\s+")) {
            if (!token.isEmpty()) tokens.add(token);
        }
        currentIndex = isPostfix ? 0 : tokens.size() - 1;
        status = "Evaluating " + (isPostfix ? "Postfix" : "Prefix");
        isRunning = true;
        timer = new Timer(gui.delay, e -> step());
        timer.start();
        repaint();
    }

    public void reset() {
        if (timer != null) timer.stop();
        stack.clear();
        tokens.clear();
        currentIndex = 0;
        status = "Ready";
        logArea.setText("");
        isRunning = false;
        repaint();
    }

    private void step() {
        if ((isPostfix && currentIndex >= tokens.size()) ||
                (!isPostfix && currentIndex < 0)) {
            finishEvaluation();
            return;
        }
        String token = tokens.get(currentIndex);
        if (isOperator(token)) {
            if (stack.size() < 2) {
                status = "Error: Not enough operands!";
                timer.stop();
                repaint();
                return;
            }

            double op1 = stack.pop();
            double op2 = stack.pop();
            double result;
            if(isPostfix) {
                result = calculate(op2, op1, token.charAt(0));
            } else{
                result = calculate(op1, op2, token.charAt(0));
            }
            stack.push(result);
            status = String.format("Calc: %.2f %s %.2f = %.2f", op1, token, op2, result);

        } else {
            try {
                double value = Double.parseDouble(token);
                stack.push(value);
                status = "Push: " + token;
            } catch (NumberFormatException e) {
                status = "Error: Invalid number '" + token + "'";
                timer.stop();
                repaint();
                return;
            }
        }
// Append to log
        logArea.append("Step " + (currentIndex + 1) + ": " + status + "\n");
        logArea.append("Stack: " + stack.toList() + "\n\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
        currentIndex += isPostfix ? 1 : -1;
        repaint();
    }

    private void finishEvaluation() {
        status = stack.size() == 1 ?
                String.format("Done! Result = %.4f", stack.peek()) :
                "Error: Invalid expression!";
        timer.stop();
        isRunning = false;
// Append to log
        logArea.append(status + "\n\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
        repaint();
    }

    private boolean isOperator(String token) {
        return token.length() == 1 && "+-*/%^".contains(token);
    }

    public void pause(){
        if(timer != null && isRunning){
            timer.stop();
        }
    }

    public void resume(){
        if(timer != null && isRunning){
            timer.start();
        }
    }

    private double calculate(double a, double b, char op) {
        if (op == '+') {
            return a + b;
        } else if (op == '-') {
            return a - b;
        } else if (op == '*') {
            return a * b;
        } else if (op == '/') {
            if (b == 0) return 0;
            return a / b;
        } else if (op == '^') {
            return Math.pow(a, b);
        }
        return 0;
    }
}
