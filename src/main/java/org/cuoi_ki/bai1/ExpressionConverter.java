package org.cuoi_ki.bai1;

// Lớp chuyển đổi biểu thức Infix sang Postfix và Prefix

import java.util.ArrayList;
import java.util.List;

public class ExpressionConverter {

    public static class ConversionStep {
        public String stackState; // Trạng thái hiện tại của stack "[+]"
        public String currentChar; // Ký tự (token) đang xử lý "B"
        public String partialOutput; // Biểu thức output đã tạo được tới thời điểm này "A B"

        public ConversionStep(String stackState, String currentChar, String partialOutput) {
            this.stackState = stackState;
            this.currentChar = currentChar;
            this.partialOutput = partialOutput;
        }
    }

    public boolean isValidInfix(String infix) {
        if (infix == null || infix.isEmpty()) return false;

        // Loại bỏ khoảng trắng
        infix = infix.replaceAll("\\s+", "");

        MyStack<Character> stack = new MyStack<>();
        boolean lastWasOperator = true; // bắt đầu không được là toán tử
        boolean lastWasOpeningParenthesis = false;

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            // 🔹 Nếu là toán hạng (số hoặc chữ)
            if (Character.isLetterOrDigit(c)) {
                lastWasOperator = false;
            }

            // 🔹 Nếu là dấu mở ngoặc
            else if (isOpeningParentheses(c)) {
                stack.push(c);
                lastWasOperator = true; // sau ngoặc mở có thể là toán hạng
                lastWasOpeningParenthesis = true;
            }

            // 🔹 Nếu là dấu đóng ngoặc
            else if (isClosingParentheses(c)) {
                if (stack.isEmpty()) return false; // dư ngoặc đóng
                char open = stack.pop();
                if (!matchingParentheses(open, c)) return false;
                lastWasOperator = false; // sau ngoặc đóng có thể là toán tử
            }

            // 🔹 Nếu là toán tử
            else if (isOperation(c)) {
                if (lastWasOperator && !lastWasOpeningParenthesis) {
                    return false; // 2 toán tử liền nhau hoặc toán tử ngay đầu
                }
                lastWasOperator = true;
                lastWasOpeningParenthesis = false;
            }

            // 🔹 Ký tự lạ
            else if(c != '.'){
                return false;
            }
        }

        // 🔹 Kiểm tra điều kiện cuối cùng
        if (lastWasOperator) return false; // kết thúc bằng toán tử
        if (!stack.isEmpty()) return false; // còn ngoặc chưa đóng

        return true;
    }

    // Kiểm tra cặp ngoặc hợp lệ
    private boolean matchingParentheses(char open, char close) {
        return (open == '(' && close == ')') ||
                (open == '[' && close == ']') ||
                (open == '{' && close == '}');
    }

    public static class ConversionResult {
        public String result; //postfix, prefix
        public List<ConversionStep> steps; // Danh sách các bước chuyển đổi

        public ConversionResult(String result, List<ConversionStep> steps) {
            this.result = result;
            this.steps = steps;
        }
    }

    //infix to postfix
    public ConversionResult InfixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        MyStack<Character> st = new MyStack<>();
        List<ConversionStep> steps = new ArrayList<>();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            String current = String.valueOf(c);
            if (Character.isDigit(c)) { // c is number: 100, 10, 1
                StringBuilder num = new StringBuilder();
                while (i < infix.length() && (Character.isDigit(infix.charAt(i))
                        || infix.charAt(i) == '.')) {
                    num.append(infix.charAt(i));
                    i++;
                }
                i--; // lùi lại vì for loop cũng tăng i
                postfix.append(num).append(" ");
                steps.add(new ConversionStep(st.toString(), num.toString(), postfix.toString().trim()));
            } else if (isOperation(c)) { // c is +, -, *, /
                while (!st.isEmpty() && hasHigherPrec(st.peek(), c) //ưu tiên
                        && !isOpeningParentheses(st.peek())) {
                    postfix.append(st.pop());
                    postfix.append(" ");
                    steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
                }
                st.push(c);
                steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
            } else if (isOpeningParentheses(c)) {
                st.push(c);
                steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
            } else if (isClosingParentheses(c)) {
                while (!st.isEmpty() && !isOpeningParentheses(st.peek())) {
                    postfix.append(st.pop());
                    postfix.append(" ");
                    steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
                }
                st.pop();
                steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
            }
        }
        while (!st.isEmpty()) {
            postfix.append(st.pop());
            postfix.append(" ");
            steps.add(new ConversionStep(st.toString(), "", postfix.toString().trim()));
        }
        return new ConversionResult(postfix.toString().trim(), steps);
    }

    public String reverseInfix(String infix){

        MyStack<String> st = new MyStack<>();
        StringBuilder num = new StringBuilder();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else {
                if (!num.isEmpty()) {
                    st.push(num.toString());
                    num.setLength(0);
                }
                if (c == '(') st.push(")");
                else if (c == ')') st.push("(");
                else if (c == '{') st.push("}");
                else if (c == '}') st.push("{");
                else if (c == '[') st.push("]");
                else if (c == ']') st.push("[");
                else st.push(String.valueOf(c));
            }
        }
        if (!num.isEmpty()) st.push(num.toString());

        StringBuilder reverseS = new StringBuilder();
        while (!st.isEmpty()) reverseS.append(st.pop());
        return reverseS.toString();
    }

    public ConversionResult infixToPrefix(String infix) {
        String reverseS = reverseInfix(infix);
        StringBuilder postfix = new StringBuilder();
        MyStack<Character> st = new MyStack<>();
        List<ConversionStep> steps = new ArrayList<>();
        steps.add(new ConversionStep(st.toString(), reverseS, postfix.toString()));
        //tìm postfix xong đảo postfix -> prefix
        for (int i = 0; i < reverseS.length(); i++) {
            char c = reverseS.charAt(i);
            String current = String.valueOf(c);
            if (Character.isDigit(c)) { // c is number: 100, 10, 1
                StringBuilder num = new StringBuilder();
                while (i < reverseS.length() && (Character.isDigit(reverseS.charAt(i))
                        || reverseS.charAt(i) == '.')) {
                    num.append(reverseS.charAt(i));
                    i++;
                }
                i--; // lùi lại vì for loop cũng tăng i
                postfix.append(num).append(" ");
                steps.add(new ConversionStep(st.toString(), num.toString(), postfix.toString().trim()));
            } else if (isOperation(c)) { // c is +, -, *, /
                while (!st.isEmpty() && hasHigherPrec(st.peek(), c) //ưu tiên
                        && !isOpeningParentheses(st.peek())) {
                    postfix.append(st.pop());
                    postfix.append(" ");
                    steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
                }
                st.push(c);
                steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
            } else if (isOpeningParentheses(c)) {
                st.push(c);
                steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
            } else if (isClosingParentheses(c)) {
                while (!st.isEmpty() && !isOpeningParentheses(st.peek())) {
                    postfix.append(st.pop());
                    postfix.append(" ");
                    steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
                }
                st.pop();
                steps.add(new ConversionStep(st.toString(), current, postfix.toString().trim()));
            }
        }
        while (!st.isEmpty()) {
            postfix.append(st.pop());
            postfix.append(" ");
            steps.add(new ConversionStep(st.toString(), "", postfix.toString().trim()));
        }
        String prefix = postfix.toString();
        String cur = reverseInfix(prefix);
        return new ConversionResult(cur, steps);
    }

    public boolean isOperation(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    public boolean hasHigherPrec(char op1, char op2) {
        int op1_priority = getPriority(op1);
        int op2_priority = getPriority(op2);
        return op1_priority >= op2_priority;
    }

    private int getPriority(char op) {
        if (op == '+' || op == '-') {
            return 1;
        } else if (op == '*' || op == '/') {
            return 2;
        } else if (op == '^') {
            return 3;
        }
        return 0;
    }

    public boolean isOpeningParentheses(char c) {
        return c == '(' || c == '{' || c == '[';
    }

    public boolean isClosingParentheses(char c) {
        return c == ')' || c == '}' || c == ']';
    }
}