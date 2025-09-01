import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import com.fasterxml.jackson.databind.*;

public class HashiraSecret {
    // Represents a rational number p/q with q>0
    static final class Frac {
        BigInteger p, q;
        Frac(BigInteger p, BigInteger q) {
            if (q.signum() == 0) throw new IllegalArgumentException("denominator 0");
            if (q.signum() < 0) { p = p.negate(); q = q.negate(); }
            BigInteger g = p.gcd(q);
            this.p = p.divide(g);
            this.q = q.divide(g);
        }
        static Frac of(BigInteger x) { return new Frac(x, BigInteger.ONE); }
        Frac add(Frac o) {
            return new Frac(this.p.multiply(o.q).add(o.p.multiply(this.q)), this.q.multiply(o.q));
        }
        Frac mul(Frac o) {
            return new Frac(this.p.multiply(o.p), this.q.multiply(o.q));
        }
    }

    static class Point {
        BigInteger x, y;
        Point(BigInteger x, BigInteger y){ this.x=x; this.y=y; }
    }

    // Convert value string in base 'base' (2..36) to BigInteger
    static BigInteger parseInBase(String val, int base) {
        // Handle bases up to 36, digits 0-9a-z
        return new BigInteger(val.toLowerCase(Locale.ROOT), base);
    }

    // Compute constant term c using first k points
    static BigInteger constantFromPoints(List<Point> pts, int k) {
        // Use first k points (any k distinct x’s work)
        List<Point> a = pts.subList(0, k);
        Frac sum = new Frac(BigInteger.ZERO, BigInteger.ONE);
        for (int i = 0; i < k; i++) {
            BigInteger xi = a.get(i).x;
            BigInteger yi = a.get(i).y;
            Frac Li0 = new Frac(BigInteger.ONE, BigInteger.ONE);
            for (int j = 0; j < k; j++) if (j != i) {
                BigInteger xj = a.get(j).x;
                // factor (-xj)/(xi - xj)
                Frac factor = new Frac(xj.negate(), xi.subtract(xj));
                Li0 = Li0.mul(factor);
            }
            sum = sum.add(Li0.mul(Frac.of(yi)));
        }
        // Result must be integer; divide p by q
        if (!sum.q.equals(BigInteger.ONE)) {
            BigInteger[] div = sum.p.divideAndRemainder(sum.q);
            if (div[1].signum() != 0)
                throw new IllegalStateException("Non-integer constant term");
            return div;
        }
        return sum.p;
    }

    // Read one testcase JSON from a file path, return list of points and k
    static class CaseData {
        int n, k;
        List<Point> points;
    }

    static CaseData readCase(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Files.readAllBytes(Paths.get(path)));
        int n = root.get("keys").get("n").asInt();
        int k = root.get("keys").get("k").asInt();
        List<Point> pts = new ArrayList<>();
        Iterator<String> it = root.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            if (key.equals("keys")) continue;
            JsonNode node = root.get(key);
            if (node != null && node.has("base") && node.has("value")) {
                BigInteger x = new BigInteger(key);
                int base = Integer.parseInt(node.get("base").asText());
                String val = node.get("value").asText();
                BigInteger y = parseInBase(val, base);
                pts.add(new Point(x, y));
            }
        }
        pts.sort(Comparator.comparing(p -> p.x)); // stable order
        if (pts.size() < k) throw new IllegalArgumentException("not enough points");
        CaseData cd = new CaseData();
        cd.n = n; cd.k = k; cd.points = pts;
        return cd;
    }

    public static void main(String[] args) throws Exception {
        // Expect two file paths as arguments: test1.json test2.json
        if (args.length < 2) {
            System.err.println("Usage: java HashiraSecret <test1.json> <test2.json>");
            return;
        }
        CaseData c1 = readCase(args);
        CaseData c2 = readCase(args[1]);
        BigInteger secret1 = constantFromPoints(c1.points, c1.k);
        BigInteger secret2 = constantFromPoints(c2.points, c2.k);
        System.out.println(secret1.toString());
        System.out.println(secret2.toString());
    }
}
