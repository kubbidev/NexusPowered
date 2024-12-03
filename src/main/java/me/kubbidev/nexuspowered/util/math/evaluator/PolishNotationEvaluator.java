package me.kubbidev.nexuspowered.util.math.evaluator;

import java.util.Collections;
import java.util.Map;

public final class PolishNotationEvaluator {

    public static double eval(String exp) {
        return eval(exp, Collections.emptyMap());
    }

    public static double eval(String exp, Map<String, Double> vars) {
        return new Object() {
            private final String[] tokens = exp.split("\\s+");
            private int pos;
            private boolean expectOperator = true;

            double parse() {
                if (this.pos >= this.tokens.length) {
                    throw new RuntimeException(
                            "Empty or invalid expression");
                }
                return parseNext();
            }

            double parseNext() {
                if (this.pos >= this.tokens.length) {
                    throw new RuntimeException(
                            "Unexpected end of expression");
                }

                String token = this.tokens[this.pos++];
                try {
                    // If it's a number, return it directly
                    double x = Double.parseDouble(token);
                    if (this.tokens.length > 1 && this.expectOperator) {
                        throw new RuntimeException(
                                "Invalid expression: numbers must be prefixed with an operator in PN");
                    }
                    return x;
                } catch (NumberFormatException e) {
                    return vars.getOrDefault(token,
                            // Otherwise, it's an operator or function, apply it
                            applyOperatorOrFunction(token));
                }
            }

            double applyOperatorOrFunction(String token) {
                this.expectOperator = false;
                switch (token) {
                    // Basic operators
                    case "+":
                        return parseNext() + parseNext();
                    case "-":
                        return parseNext() - parseNext();
                    case "*":
                        return parseNext() * parseNext();
                    case "/":
                        return parseNext() / parseNext();
                    case "^":
                        return Math.pow(parseNext(), parseNext());

                    // Unary operators and functions
                    case "sqrt":
                        return Math.sqrt(parseNext());
                    case "sin":
                        return Math.sin(Math.toRadians(parseNext()));
                    case "cos":
                        return Math.cos(Math.toRadians(parseNext()));
                    case "tan":
                        return Math.tan(Math.toRadians(parseNext()));

                    // Invalid token handling
                    default:
                        throw new RuntimeException("Unknown operator or function: " + token);
                }
            }
        }.parse();
    }

    private PolishNotationEvaluator() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
