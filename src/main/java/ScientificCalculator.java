import java.util.*;

/**
 * ScientificCalculator: calculation engine for GUI
 * - Build expression via input(token)
 * - Evaluate via evaluate()
 */
public class ScientificCalculator {

    private final StringBuilder expr = new StringBuilder();

    // ========= public API =========

    public String getExpression() {
        return expr.toString();
    }

    public void clear() {
        expr.setLength(0);
    }

    public void deleteOne() {
        if (expr.length() > 0) expr.deleteCharAt(expr.length() - 1);
    }

    /**
     * Accept token from GUI buttons.
     * Examples: "7", "+", "sin", "(", "π", "x²", "xʸ", "1/x", "√", "n!", "+/-"
     */
    public void input(String token) {
        if (token == null || token.isBlank()) return;

        switch (token) {
            case "×" -> appendOperator("*");
            case "÷" -> appendOperator("/");
            case "π" -> appendConstant("pi");
            case "e" -> appendConstant("e");

            case "sin", "cos", "tan", "ln", "log", "abs" -> appendFunction(token);
            case "√" -> appendFunction("sqrt");

            case "(" , ")" -> expr.append(token);

            case "." -> appendDot();
            case "%" -> appendOperator("%");
            case "+" , "-" -> appendOperator(token);

            case "x²" -> appendPostfix("^2");
            case "xʸ" -> appendOperator("^");

            case "1/x" -> applyReciprocal();
            case "n!" -> appendPostfix("!");

            case "+/-" -> toggleUnarySign();

            case "Clear" -> clear();
            case "DEL" -> deleteOne();
            case "Enter" -> { /* handled in GUI: call evaluate() */ }

            default -> { // numbers like 0-9
                if (token.matches("\\d+")) {
                    expr.append(token);
                }
                // ignore unknown tokens
            }
        }
    }

    /**
     * Evaluate current expression and return result string.
     * Also replaces expression with result (so you can keep calculating).
     */
    public String evaluate() {
        String s = expr.toString().trim();
        if (s.isEmpty()) return "0";

        double val = new Parser(s).parse();

        // format: remove .0 if integer-like
        String out = formatDouble(val);

        // set expression to result
        expr.setLength(0);
        expr.append(out);

        return out;
    }

    // ========= helpers for building expr =========

    private void appendOperator(String op) {
        // allow unary minus at beginning or after '(' or operator
        if (op.equals("-")) {
            if (expr.length() == 0) { expr.append("-"); return; }
            char last = lastChar();
            if (isOpChar(last) || last == '(') { expr.append("-"); return; }
        }

        if (expr.length() == 0) return;

        char last = lastChar();
        if (isOpChar(last)) {
            // replace last operator
            expr.setCharAt(expr.length() - 1, op.charAt(0));
            return;
        }
        if (last == '(') return; // don't put binary op right after '('

        expr.append(op);
    }

    private void appendFunction(String fn) {
        // add implicit multiply if needed: "2sin(" -> "2*sin("
        if (expr.length() > 0) {
            char last = lastChar();
            if (Character.isDigit(last) || last == ')' || last == 'e' || last == 'i' || last == '!') {
                expr.append("*");
            }
        }
        expr.append(fn).append("(");
    }

    private void appendConstant(String c) {
        if (expr.length() > 0) {
            char last = lastChar();
            if (Character.isDigit(last) || last == ')' || last == 'e' || last == 'i' || last == '!') {
                expr.append("*");
            }
        }
        expr.append(c);
    }

    private void appendDot() {
        // prevent multiple dots in same number
        if (expr.length() == 0) { expr.append("0."); return; }
        char last = lastChar();
        if (!Character.isDigit(last)) {
            expr.append("0.");
            return;
        }
        // scan back until non-digit/non-dot
        for (int i = expr.length() - 1; i >= 0; i--) {
            char ch = expr.charAt(i);
            if (ch == '.') return; // already has dot
            if (!Character.isDigit(ch)) break;
        }
        expr.append('.');
    }

    private void appendPostfix(String postfix) {
        if (expr.length() == 0) return;
        char last = lastChar();
        if (isOpChar(last) || last == '(') return;
        expr.append(postfix);
    }

    private void applyReciprocal() {
        // turn current "A" into "1/(A)" roughly by wrapping last term
        if (expr.length() == 0) {
            expr.append("1/(");
            return;
        }
        wrapLastAtomWith("1/(", ")");
    }

    private void toggleUnarySign() {
        // Simple behavior:
        // - If expression ends with a number/)/constant/! => wrap last atom with (-1)*atom
        // - If expression is empty or ends with operator/( => just append "-"
        if (expr.length() == 0) { expr.append("-"); return; }
        char last = lastChar();
        if (isOpChar(last) || last == '(') {
            expr.append("-");
            return;
        }
        wrapLastAtomWith("(-1)*(", ")");
    }

    /**
     * Wrap the last "atom" (number, constant, parenthesized group, function call result) with prefix+ (atom)+suffix.
     */
    private void wrapLastAtomWith(String prefix, String suffix) {
        int end = expr.length();

        // If ends with factorial or exponent, include them as part of atom
        int i = end - 1;

        // Case: expression ends with ')': find matching '('
        if (expr.charAt(i) == ')') {
            int open = findMatchingOpenParen(i);
            expr.insert(open, prefix);
            expr.insert(end + prefix.length(), suffix);
            return;
        }

        // Case: ends with letters (pi/e) or digits/dot
        int start = i;

        // include postfix '!' or exponent like ^2 already part of string, but here we wrap whole tail token
        while (start >= 0) {
            char ch = expr.charAt(start);
            if (Character.isLetterOrDigit(ch) || ch == '.' ) {
                start--;
                continue;
            }
            // allow "pi" (letters) already included
            break;
        }
        start++;

        if (start < 0 || start >= end) return;

        expr.insert(start, prefix);
        expr.insert(end + prefix.length(), suffix);
    }

    private int findMatchingOpenParen(int closeIndex) {
        int depth = 0;
        for (int i = closeIndex; i >= 0; i--) {
            char ch = expr.charAt(i);
            if (ch == ')') depth++;
            else if (ch == '(') {
                depth--;
                if (depth == 0) return i;
            }
        }
        // if not found, return 0 as fallback
        return 0;
    }

    private char lastChar() {
        return expr.charAt(expr.length() - 1);
    }

    private boolean isOpChar(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^';
    }

    private String formatDouble(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "Error";
        long asLong = (long) v;
        if (Math.abs(v - asLong) < 1e-10) return Long.toString(asLong);
        // limit long decimals
        return String.format(java.util.Locale.US, "%.10f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    // ========= Parser (recursive descent) =========
    // Grammar:
    // expr    := term ((+|-) term)*
    // term    := power ((*|/|%) power)*
    // power   := unary (^ power)?        (right associative)
    // unary   := (+|-) unary | postfix
    // postfix := primary (!)*            (factorial)
    // primary := number | constant | func '(' expr ')' | '(' expr ')'

    private static class Parser {
        private final String s;
        private int pos;

        Parser(String s) {
            this.s = s.replaceAll("\\s+", "");
            this.pos = 0;
        }

        double parse() {
            double v = parseExpr();
            if (pos != s.length()) throw new IllegalArgumentException("Invalid expression");
            return v;
        }

        private double parseExpr() {
            double v = parseTerm();
            while (match('+') || match('-')) {
                char op = s.charAt(pos - 1);
                double rhs = parseTerm();
                v = (op == '+') ? (v + rhs) : (v - rhs);
            }
            return v;
        }

        private double parseTerm() {
            double v = parsePower();
            while (match('*') || match('/') || match('%')) {
                char op = s.charAt(pos - 1);
                double rhs = parsePower();
                if (op == '*') v *= rhs;
                else if (op == '/') v /= rhs;
                else v %= rhs;
            }
            return v;
        }

        private double parsePower() {
            double v = parseUnary();
            if (match('^')) {
                double rhs = parsePower(); // right-associative
                v = Math.pow(v, rhs);
            }
            return v;
        }

        private double parseUnary() {
            if (match('+')) return parseUnary();
            if (match('-')) return -parseUnary();
            return parsePostfix();
        }

        private double parsePostfix() {
            double v = parsePrimary();
            while (match('!')) {
                v = factorial(v);
            }
            return v;
        }

        private double parsePrimary() {
            if (match('(')) {
                double v = parseExpr();
                expect(')');
                return v;
            }

            // function name or constants
            String name = readIdentifier();
            if (!name.isEmpty()) {
                if (name.equals("pi")) return Math.PI;
                if (name.equals("e")) return Math.E;

                // function call must be like fn(expr)
                expect('(');
                double arg = parseExpr();
                expect(')');

                return applyFunc(name, arg);
            }

            // number
            String num = readNumber();
            if (!num.isEmpty()) {
                return Double.parseDouble(num);
            }

            throw new IllegalArgumentException("Invalid token");
        }

        private String readIdentifier() {
            int start = pos;
            while (pos < s.length() && Character.isLetter(s.charAt(pos))) pos++;
            return s.substring(start, pos);
        }

        private String readNumber() {
            int start = pos;
            boolean seenDot = false;
            while (pos < s.length()) {
                char ch = s.charAt(pos);
                if (Character.isDigit(ch)) {
                    pos++;
                } else if (ch == '.' && !seenDot) {
                    seenDot = true;
                    pos++;
                } else {
                    break;
                }
            }
            return s.substring(start, pos);
        }

        private boolean match(char c) {
            if (pos < s.length() && s.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }

        private void expect(char c) {
            if (!match(c)) throw new IllegalArgumentException("Expected '" + c + "'");
        }

        private double applyFunc(String fn, double x) {
            return switch (fn) {
                case "sin" -> Math.sin(Math.toRadians(x)); // degrees
                case "cos" -> Math.cos(Math.toRadians(x));
                case "tan" -> Math.tan(Math.toRadians(x));
                case "ln"  -> Math.log(x);
                case "log" -> Math.log10(x);
                case "sqrt"-> Math.sqrt(x);
                case "abs" -> Math.abs(x);
                default -> throw new IllegalArgumentException("Unknown function: " + fn);
            };
        }

        private double factorial(double x) {
            // allow factorial only for non-negative integers
            long n = Math.round(x);
            if (Math.abs(x - n) > 1e-10 || n < 0) throw new IllegalArgumentException("factorial domain");
            double res = 1.0;
            for (long i = 2; i <= n; i++) res *= i;
            return res;
        }
    }
}