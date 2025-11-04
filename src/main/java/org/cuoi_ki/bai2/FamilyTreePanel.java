package org.cuoi_ki.bai2;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FamilyTreePanel extends JPanel {
    private FamilyTree familyTree;
    //Lưu tọa độ và kích thước (vùng chữ nhật)
    // của từng người → để xác định người nào được click vào.
    private Map<Person, Rectangle> personBounds;

    // Kích thước node(people)
    private static final int nodeWidth = 140;
    private static final int nodeHeight = 70;

    // Khoảng cách giữa các node - TĂNG LÊN để tránh dính nhau
    private static final int horizonalGap = 80;
    private static final int verticalGap = 120;

    // Margin cho toàn bộ canvas
    private static final int MARGIN = 50;

    // Biến lưu kích thước canvas
    private int maxWidth = 0;
    private int maxHeight = 0;

    public FamilyTreePanel() {
        this.personBounds = new HashMap<>();
        setBackground(Color.WHITE);
    }

    public void setFamilyTree(FamilyTree familyTree) {
        this.familyTree = familyTree;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Xóa danh sách vị trí cũ (personBounds.clear())
        personBounds.clear();
        maxWidth = 0;
        maxHeight = 0;

        //Nếu cây có người gốc (root) → gọi hàm drawTree() để bắt đầu vẽ từ người đó
        if (familyTree != null && familyTree.getRoot() != null) {
            // Tính toán width cần thiết cho cây
            int treeWidth = calculateTreeWidth(familyTree.getRoot());
            int startX = Math.max(treeWidth / 2 + MARGIN, getWidth() / 2);

            drawTree(g2d, familyTree.getRoot(), startX, MARGIN, treeWidth / 2);

            // Update preferred size để scroll hoạt động
            maxWidth = Math.max(maxWidth, treeWidth + 2 * MARGIN);
            maxHeight += MARGIN;
            setPreferredSize(new Dimension(maxWidth, maxHeight));
            revalidate();
        }
    }

    // Tính toán chiều rộng cần thiết cho cây con
    private int calculateTreeWidth(Person person) {
        if (person.getChildren().isEmpty()) {
            // Node lá: chiều rộng = width của node + khoảng cho spouse nếu có
            return person.getSpouse() != null ?
                    nodeWidth * 2 + horizonalGap : nodeWidth;
        }

        // Node có con: tổng chiều rộng của tất cả các cây con
        int totalChildWidth = 0;
        for (Person child : person.getChildren()) {
            totalChildWidth += calculateTreeWidth(child);
        }

        // Thêm khoảng cách giữa các con
        totalChildWidth += (person.getChildren().size() - 1) * horizonalGap;

        // Chiều rộng của node cha (bao gồm spouse nếu có)
        int parentWidth = person.getSpouse() != null ?
                nodeWidth * 2 + horizonalGap : nodeWidth;

        return Math.max(totalChildWidth, parentWidth);
    }

    private void drawTree(Graphics2D g2d, Person person, int x, int y, int xOffset) {
        // Cập nhật maxHeight
        maxHeight = Math.max(maxHeight, y + nodeHeight);
        maxWidth = Math.max(maxWidth, x + nodeWidth);

        // Vẽ node chính
        drawPersonNode(g2d, person, x, y);

        // Vẽ spouse nếu có
        int spouseX = x;
        if (person.getSpouse() != null) {
            spouseX = x + nodeWidth + 30; // Khoảng cách giữa vợ chồng
            drawSpouseConnection(g2d, person, x, y, spouseX);
            maxWidth = Math.max(maxWidth, spouseX + nodeWidth);
        }

        // Vẽ các con
        java.util.List<Person> children = person.getChildren();
        if (!children.isEmpty()) {
            int childY = y + nodeHeight + verticalGap;

            // Tính toán vị trí của các con
            int[] childWidths = new int[children.size()];
            int totalChildWidth = 0;

            for (int i = 0; i < children.size(); i++) {
                childWidths[i] = calculateTreeWidth(children.get(i));
                totalChildWidth += childWidths[i];
            }
            totalChildWidth += (children.size() - 1) * horizonalGap;

            // Tính vị trí bắt đầu để căn giữa các con
            int centerX = (x + spouseX) / 2;
            int startX = centerX - totalChildWidth / 2;

            // Vẽ đường nối từ cha mẹ
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));

            // Đường dọc từ cha mẹ xuống
            int parentCenterY = y + nodeHeight;
            int connectionY = childY - 40;
            g2d.drawLine(centerX, parentCenterY, centerX, connectionY);

            // Vẽ từng con và đường nối
            int currentX = startX;
            for (int i = 0; i < children.size(); i++) {
                int childCenterX = currentX + childWidths[i] / 2;

                // Đường ngang nối các con
                if (i == 0) {
                    g2d.drawLine(centerX, connectionY, childCenterX, connectionY);
                } else {
                    int prevChildCenterX = currentX - horizonalGap - childWidths[i-1] / 2;
                    g2d.drawLine(prevChildCenterX, connectionY, childCenterX, connectionY);
                }

                // Đường dọc từ đường ngang xuống con
                g2d.drawLine(childCenterX, connectionY, childCenterX, childY);

                // Vẽ đệ quy cây con
                drawTree(g2d, children.get(i), childCenterX, childY, childWidths[i] / 2);

                currentX += childWidths[i] + horizonalGap;
            }

            g2d.setStroke(new BasicStroke(1));
        }
    }

    private void drawPersonNode(Graphics2D g2d, Person person, int centerX, int y) {
        int x = centerX - nodeWidth / 2;

        // Vẽ hình chữ nhật với màu theo giới tính
        Color nodeColor = person.getGender().equals("Nam") ?
                new Color(173, 216, 230) : new Color(255, 182, 193);
        g2d.setColor(nodeColor);
        g2d.fillRoundRect(x, y, nodeWidth, nodeHeight, 10, 10);

        // Viền đen
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, nodeWidth, nodeHeight, 10, 10);
        g2d.setStroke(new BasicStroke(1));

        // Vẽ thông tin - Font nhỏ hơn để vừa khung
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics fm = g2d.getFontMetrics();

        String[] lines = {
                truncateText(person.getName(), fm, nodeWidth - 10),
                "(" + person.getBirthYear() + ")",
                "ID: " + person.getId()
        };

        int lineHeight = fm.getHeight();
        int textY = y + (nodeHeight - lines.length * lineHeight) / 2 + fm.getAscent();

        for (String line : lines) {
            int textX = centerX - fm.stringWidth(line) / 2;
            g2d.drawString(line, textX, textY);
            textY += lineHeight;
        }

        // Lưu vị trí cho click detection
        personBounds.put(person, new Rectangle(x, y, nodeWidth, nodeHeight));
    }

    private void drawSpouseConnection(Graphics2D g2d, Person person, int x, int y, int spouseX) {
        Person spouse = person.getSpouse();
        if (spouse != null) {
            // Vẽ node spouse
            drawPersonNode(g2d, spouse, spouseX, y);

            // Vẽ đường nối màu đỏ giữa vợ chồng
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3));
            int y1 = y + nodeHeight / 2;
            g2d.drawLine(x + nodeWidth / 2, y1, spouseX - nodeWidth / 2, y1);

            // Vẽ trái tim nhỏ ở giữa
            int heartX = (x + nodeWidth / 2 + spouseX - nodeWidth / 2) / 2;
            g2d.fillOval(heartX - 3, y1 - 3, 6, 6);

            g2d.setStroke(new BasicStroke(1));
        }
    }

    // Cắt text nếu quá dài
    private String truncateText(String text, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        int ellipsisWidth = fm.stringWidth(ellipsis);

        for (int i = text.length() - 1; i > 0; i--) {
            String truncated = text.substring(0, i) + ellipsis;
            if (fm.stringWidth(truncated) <= maxWidth) {
                return truncated;
            }
        }
        return ellipsis;
    }

    public Person getPersonAt(int x, int y) {
        for (Map.Entry<Person, Rectangle> entry : personBounds.entrySet()) {
            if (entry.getValue().contains(x, y)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Dimension getPreferredSize() {
        // Trả về kích thước động dựa trên nội dung
        if (maxWidth > 0 && maxHeight > 0) {
            return new Dimension(maxWidth, maxHeight);
        }
        return new Dimension(1200, 800);
    }
}