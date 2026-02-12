package net.mint.commands.impl;

import net.mint.commands.Command;
import net.mint.commands.CommandInfo;
import net.mint.services.Services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@CommandInfo(name = "calc", desc = "Evaluate a mathematical expression.")
public class CalculatorCommand extends Command {

    private static final MathContext MC = new MathContext(50, RoundingMode.HALF_UP);
    private static final BigDecimal MAX_PLAIN_OUTPUT = new BigDecimal("10000000000"); // 10^10

    @Override
    public void onCommand(String[] args) {
        if (args.length == 0) {
            Services.CHAT.sendRaw("§cUsage: §7.calc <expression>");
            return;
        }

        StringBuilder exprBuilder = new StringBuilder();
        for (String arg : args) {
            exprBuilder.append(arg).append(" ");
        }
        String expression = exprBuilder.toString().trim();

        try {
            BigDecimal result = evaluate(expression);

            BigDecimal stripped = result.stripTrailingZeros();
            String output;

            if (stripped.scale() <= 0 && stripped.compareTo(MAX_PLAIN_OUTPUT) <= 0) {
                output = stripped.toBigIntegerExact().toString();
            } else {
                output = stripped.toPlainString();
            }

            Services.CHAT.sendRaw("§aResult: §f" + output);
        } catch (Exception e) {
            Services.CHAT.sendRaw("§cInvalid expression: §7" + expression);
        }
    }

    private BigDecimal evaluate(String expression) throws Exception {
        expression = expression.replaceAll("\\s+", "");
        if (expression.isEmpty()) throw new Exception("Empty expression");
        return parseAddSub(expression);
    }

    private BigDecimal parseAddSub(String expr) throws Exception {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            else if (c == '(') level--;
            else if (level == 0 && (c == '+' || c == '-')) {
                BigDecimal left = parseAddSub(expr.substring(0, i));
                BigDecimal right = parseMulDiv(expr.substring(i + 1));
                return c == '+' ? left.add(right, MC) : left.subtract(right, MC);
            }
        }
        return parseMulDiv(expr);
    }

    private BigDecimal parseMulDiv(String expr) throws Exception {
        int level = 0;
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == ')') level++;
            else if (c == '(') level--;
            else if (level == 0 && (c == '*' || c == '/')) {
                BigDecimal left = parseMulDiv(expr.substring(0, i));
                BigDecimal right = parsePrimary(expr.substring(i + 1));
                if (c == '*') return left.multiply(right, MC);
                if (right.compareTo(BigDecimal.ZERO) == 0)
                    throw new Exception("Division by zero");
                return left.divide(right, MC);
            }
        }
        return parsePrimary(expr);
    }

    private BigDecimal parsePrimary(String expr) throws Exception {
        if (expr.startsWith("(") && expr.endsWith(")")) {
            return evaluate(expr.substring(1, expr.length() - 1));
        }
        try {
            return new BigDecimal(expr);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number: " + expr);
        }
    }
}