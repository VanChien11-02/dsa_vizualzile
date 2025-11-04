package org.cuoi_ki.bai3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
// Xử lý logic hash table (create, insert, delete, search)
public class HashTable {
    private Object[] table;
    private int size;
    private String method;
    private HashingVisualizer parent;
    private List<Integer> insertedValues; // Danh sách lưu các giá trị đã chèn

    public HashTable(int size, String method, HashingVisualizer parent) {
        this.size = size;
        this.method = method;
        this.parent = parent;
        this.insertedValues = new ArrayList<>();

        if (method.equals("Chaining")) {
            table = new LinkedList[size];
            for (int i = 0; i < size; i++) {
                table[i] = new LinkedList<Integer>();
            }
        } else {
            table = new Integer[size];
        }
    }

    private int hash(int value) { return value % size; }

    public void addValue(int value) {
        LogPanel log = parent.getLogPanel();
        log.append("Thêm phần tử: " + value + "\n");
        int index = hash(value);

        if (method.equals("Chaining")) {
            LinkedList<Integer> chain = (LinkedList<Integer>) table[index];
            if (!chain.contains(value)) { // Tránh thêm trùng lặp
                chain.add(value);
                insertedValues.add(value);
                log.append(" -> Đã thêm số vào tại " + index + "\n");
            } else {
                log.append(" -> Số " + value + " đã tồn tại trong " + index + "\n");
            }
            updateHashTableView();
            return;
        }

        // Linear Probing
        int step = 0;
        while (step < size) {
            int i = (index + step) % size;
            if (table[i] == null) {
                table[i] = value;
                insertedValues.add(value);
                log.append(" -> Đã thêm số vào tại " + i + " sau " + step + " tham dò(s)\n");
                updateHashTableView();
                return;
            }
            if (table[i].equals(value)) {
                log.append(" -> Số " + value + " đã tồn tại trong " + i + "\n");
                return;
            }
            step++;
        }
        log.append(" -> Table đã full, không thể cho thêm vào " + value + ".\n");
    }

    public void deleteValue(int value) {
        LogPanel log = parent.getLogPanel();
        log.append("Đang xóa chữ số: " + value + "\n");
        int index = hash(value);

        if (method.equals("Chaining")) {
            LinkedList<Integer> chain = (LinkedList<Integer>) table[index];
            if (chain.remove((Integer) value)) {
                insertedValues.remove((Integer) value);
                log.append(" -> Đã xóa " + value + " từ " + index + "\n");
            } else {
                log.append(" -> Không tìm thấy số.\n");
            }
            updateHashTableView();
            return;
        }

        // Linear Probing
        int step = 0;
        while (step < size) {
            int i = (index + step) % size;
            if (table[i] == null) {
                log.append(" -> Không tìm thấy số.\n");
                return;
            }
            if (table[i].equals(value)) {
                table[i] = null;
                insertedValues.remove((Integer) value);
                log.append(" -> Đã xóa " + value + " từ " + i + "\n");
                updateHashTableView();
                return;
            }
            step++;
        }
        log.append(" -> Không tìm thấy số.\n");
    }

    public void searchValue(int value) {
        LogPanel log = parent.getLogPanel();
        StringBuilder result = new StringBuilder();
        result.append("--- Đang tìm kiếm số: ").append(value).append(" ---\n");

        // 1. Hashing Search
        int hashComparisons = 0;
        boolean foundInHash = false;
        int foundAtIndex = -1;

        int index = hash(value);
        if (method.equals("Chaining")) {
            LinkedList<Integer> chain = (LinkedList<Integer>) table[index];
            for (Integer valInChain : chain) {
                hashComparisons++;
                if (valInChain.equals(value)) {
                    foundInHash = true;
                    foundAtIndex = index;
                    break;
                }
            }
        } else { // Linear Probing
            int step = 0;
            while (step < size) {
                int i = (index + step) % size;
                hashComparisons++;
                if (table[i] == null) {
                    break;
                }
                if (table[i].equals(value)) {
                    foundInHash = true;
                    foundAtIndex = i;
                    break;
                }
                step++;
            }
        }

        result.append("1. Tìm kiếm bằng Hashing (").append(method).append("):\n");
        result.append("   - Trạng thái: ").append(foundInHash ? "Tìm thấy ở vị trí " + foundAtIndex : "Không tìm thấy").append("\n");
        result.append("   - Số lần tìm kiếm: ").append(hashComparisons).append("\n\n");

        // 2. Sequential Search
        int sequentialComparisons = 0;
        boolean foundInSequential = false;
        if (insertedValues.isEmpty()) {
            sequentialComparisons = 0;
        } else {
            for (Integer val : insertedValues) {
                sequentialComparisons++;
                if (val.equals(value)) {
                    foundInSequential = true;
                    break;
                }
            }
        }

        result.append("2. Tìm kiếm tuần tự:\n");
        result.append("   - Trạng thái: ").append(foundInSequential ? "Tìm thấy" : "Không tìm thấy").append("\n");
        result.append("   - Số lần tìm kiếm: ").append(sequentialComparisons).append("\n");
        result.append("------------------------------------------\n");

        log.append(result.toString());

        // THÊM MỚI: Highlight ô được tìm thấy
        if (foundInHash) {
            parent.getHashPanel().highlightFound(foundAtIndex, value);
        } else {
            parent.getHashPanel().clearHighlight();
        }
    }

    public void updateHashTableView() {
        // Lấy HashPanel từ frame chính và yêu cầu nó cập nhật view
        parent.getHashPanel().updateView(table, method);
    }
}