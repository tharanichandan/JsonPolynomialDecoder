import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.*;

public class LagrangePolynomialSolver {
    public static void main(String[] args) {
        try {
            // Read entire JSON file into a string
            String fileName = (args.length > 0) ? args[0] : "data.json";
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            content = content.replaceAll("\\s+", ""); // remove spaces/newlines

            // Extract n and k
            int n = Integer.parseInt(extractValue(content, "\"n\":", ","));
            int k = Integer.parseInt(extractValue(content, "\"k\":", "}"));

            List<BigInteger> xValues = new ArrayList<>();
            List<BigInteger> yValues = new ArrayList<>();

            // Extract each root (1..n)
            for (int i = 1; i <= n; i++) {
                String key = "\"" + i + "\":{";
                int idx = content.indexOf(key);
                if (idx == -1) continue;

                String obj = content.substring(idx, content.indexOf("}", idx) + 1);

                int base = Integer.parseInt(extractValue(obj, "\"base\":\"", "\""));
                String valueStr = extractValue(obj, "\"value\":\"", "\"");

                BigInteger decimalValue = new BigInteger(valueStr, base);

                xValues.add(BigInteger.valueOf(i));
                yValues.add(decimalValue);
            }

            // Print decoded points
            System.out.println("Decoded points:");
            for (int i = 0; i < xValues.size(); i++) {
                System.out.println("(" + xValues.get(i) + ", " + yValues.get(i) + ")");
            }

            // Use first k points
            List<BigInteger> xSubset = xValues.subList(0, k);
            List<BigInteger> ySubset = yValues.subList(0, k);

            // Lagrange interpolation at x=0 (constant term)
            BigInteger constantC = lagrangeInterpolation(xSubset, ySubset, BigInteger.ZERO);

            System.out.println("\nConstant term (c) = " + constantC);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper to extract value between start and end markers
    private static String extractValue(String text, String start, String end) {
        int s = text.indexOf(start);
        if (s == -1) return "";
        s += start.length();
        int e = text.indexOf(end, s);
        if (e == -1) e = text.length();
        return text.substring(s, e);
    }

    // Lagrange interpolation
    public static BigInteger lagrangeInterpolation(List<BigInteger> x, List<BigInteger> y, BigInteger valueAt) {
        BigInteger result = BigInteger.ZERO;
        int n = x.size();

        for (int i = 0; i < n; i++) {
            BigInteger term = y.get(i);
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < n; j++) {
                if (j != i) {
                    numerator = numerator.multiply(valueAt.subtract(x.get(j)));
                    denominator = denominator.multiply(x.get(i).subtract(x.get(j)));
                }
            }

            term = term.multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        return result;
    }
}
