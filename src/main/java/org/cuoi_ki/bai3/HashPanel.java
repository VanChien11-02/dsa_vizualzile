package org.cuoi_ki.bai3;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
// Panel vẽ các ô trong hash table để dễ nhìn
public class HashPanel extends JPanel {
    private static final int BOX_SIZE = 40;
    private static final int HORIZONTAL_GAP = 15;
    private static final int VERTICAL_GAP = 5;
    private static final int ARROW_SIZE = 8;
    private static final int PADDING = 20;
    private static final int INDEX_WIDTH = 40;

    private Object[] table;
    private String method;

    // THÊM MỚI: Biến để lưu vị trí và giá trị cần highlight
    private int highlightIndex = -1;
    private Integer highlightValue = null;

    public HashPanel() {
        setBackground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 14));
    }

    // Phương thức nhận dữ liệu từ HashTable để vẽ
    public void updateView(Object[] table, String method) {
        this.table = table;
        this.method = method;
        // Reset highlight khi update view bình thường
        this.highlightIndex = -1;
        this.highlightValue = null;
        updatePreferredSize();
        this.repaint();
    }

    // THÊM MỚI: Phương thức để highlight một phần tử tìm thấy
    public void highlightFound(int index, Integer value) {
        this.highlightIndex = index;
        this.highlightValue = value;
        this.repaint();
    }

    // THÊM MỚI: Phương thức để clear highlight
    public void clearHighlight() {
        this.highlightIndex = -1;
        this.highlightValue = null;
        this.repaint();
    }

    // Tính toán kích thước preferred dựa trên nội dung
    private void updatePreferredSize() {
        if (table == null) return;

        int maxWidth = PADDING + INDEX_WIDTH + BOX_SIZE;

        // Tìm chiều rộng tối đa cần thiết
        if (method != null && method.equals("Chaining")) {
            for (Object obj : table) {
                LinkedList<Integer> chain = (LinkedList<Integer>) obj;
                if (chain != null && !chain.isEmpty()) {
                    int chainWidth = PADDING + INDEX_WIDTH +
                            (chain.size() * (BOX_SIZE + HORIZONTAL_GAP));
                    maxWidth = Math.max(maxWidth, chainWidth);
                }
            }
        } else {
            maxWidth = PADDING + INDEX_WIDTH + BOX_SIZE + PADDING;
        }

        int totalHeight = PADDING + table.length * (BOX_SIZE + VERTICAL_GAP) + PADDING;
        setPreferredSize(new Dimension(maxWidth + PADDING, totalHeight));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (table == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < table.length; i++) {
            int y = PADDING + i * (BOX_SIZE + VERTICAL_GAP);

            // Vẽ chỉ số index
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String indexStr = String.valueOf(i);
            FontMetrics fm = g2d.getFontMetrics();
            int indexX = PADDING + (INDEX_WIDTH - fm.stringWidth(indexStr)) / 2;
            g2d.drawString(indexStr, indexX, y + BOX_SIZE / 2 + fm.getAscent() / 2);

            // Vẽ ô đầu tiên của mỗi index
            int startX = PADDING + INDEX_WIDTH;

            if (method.equals("Chaining")) {
                drawChainingList(g2d, startX, y, (LinkedList<Integer>) table[i], i);
            } else { // Linear Probing
                Integer value = (Integer) table[i];
                boolean shouldHighlight = (i == highlightIndex && value != null && value.equals(highlightValue));
                drawBoxWithValue(g2d, startX, y, value != null ? String.valueOf(value) : "", shouldHighlight);
            }
        }
    }

    private void drawChainingList(Graphics2D g2d, int x, int y, LinkedList<Integer> chain, int index) {
        if (chain == null || chain.isEmpty()) {
            // Vẽ ô trống cho index không có giá trị
            drawEmptyBox(g2d, x, y);
            return;
        }

        int currentX = x;
        int arrowY = y + BOX_SIZE / 2;

        for (int i = 0; i < chain.size(); i++) {
            Integer value = chain.get(i);
            // Kiểm tra xem có phải ô cần highlight không
            boolean shouldHighlight = (index == highlightIndex && value.equals(highlightValue));
            // Vẽ giá trị vào ô
            drawBoxWithValue(g2d, currentX, y, String.valueOf(value), shouldHighlight);

            // Vẽ mũi tên -> đến ô tiếp theo (nếu không phải phần tử cuối)
            if (i < chain.size() - 1) {
                int arrowStartX = currentX + BOX_SIZE;
                g2d.setColor(Color.BLACK);
                g2d.drawLine(arrowStartX, arrowY, arrowStartX + HORIZONTAL_GAP, arrowY);
                drawArrowHead(g2d, new Point(arrowStartX + HORIZONTAL_GAP, arrowY),
                        new Point(arrowStartX, arrowY));
            }

            // Cập nhật vị trí X cho ô tiếp theo
            currentX += BOX_SIZE + HORIZONTAL_GAP;
        }
    }

    private void drawEmptyBox(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(245, 245, 245)); // Màu xám nhạt
        g2d.fillRect(x, y, BOX_SIZE, BOX_SIZE);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(x, y, BOX_SIZE, BOX_SIZE);
    }

    // THAY ĐỔI: Thêm parameter shouldHighlight
    private void drawBoxWithValue(Graphics2D g2d, int x, int y, String text, boolean shouldHighlight) {
        // Chọn màu nền dựa trên trạng thái
        if (shouldHighlight) {
            // Màu vàng/xanh lá sáng cho ô được tìm thấy
            g2d.setColor(new Color(144, 238, 144)); // Light Green
        } else if (text != null && !text.isEmpty()) {
            g2d.setColor(new Color(220, 240, 255)); // Màu xanh nhạt bình thường
        } else {
            g2d.setColor(new Color(245, 245, 245)); // Màu xám nhạt
        }

        g2d.fillRect(x, y, BOX_SIZE, BOX_SIZE);

        // Viền đậm hơn nếu được highlight
        if (shouldHighlight) {
            g2d.setColor(new Color(252, 0, 38));
            g2d.setStroke(new BasicStroke(2.5f));
        } else {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.0f));
        }
        g2d.drawRect(x, y, BOX_SIZE, BOX_SIZE);

        // Reset stroke
        g2d.setStroke(new BasicStroke(1.0f));

        if (text != null && !text.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textX = x + (BOX_SIZE - textWidth) / 2;
            int textY = y + BOX_SIZE / 2 + fm.getAscent() / 2 - 2;
            g2d.drawString(text, textX, textY);
        }
    }

    // Hàm tiện ích để vẽ đầu mũi tên
    private void drawArrowHead(Graphics2D g2, Point tip, Point tail) {
        double phi = Math.toRadians(30);
        int barb = ARROW_SIZE;
        double dy = tip.y - tail.y;
        double dx = tip.x - tail.x;
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + phi;
        for (int j = 0; j < 2; j++) {
            x = tip.x - barb * Math.cos(rho);
            y = tip.y - barb * Math.sin(rho);
            g2.drawLine(tip.x, tip.y, (int) x, (int) y);
            rho = theta - phi;
        }
    }
}