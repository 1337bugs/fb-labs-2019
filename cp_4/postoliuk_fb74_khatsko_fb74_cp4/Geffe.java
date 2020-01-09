package crypto;

        import java.nio.file.Files;
        import java.nio.file.Paths;
        import java.time.Duration;
        import java.time.LocalDateTime;
        import java.util.Set;
        import java.util.stream.Collectors;
        import java.util.stream.IntStream;

public class Geffe {

    private static final int I30 = 1 << 30;
    // Same as (1 << 31) - 1
    private static final int I31 = Integer.MAX_VALUE;
    private static final int C = 73;
    private static final int N = 8;
    private static int[] Z = new int[N];

    public static void main(String[] args) throws Exception {
        String zString = Files.readString(Paths.get("resources/lab4-11.txt"));

        for (int i = 0, tmp = 0; i < N; i++, tmp += 32) {
            Z[i] = Integer.reverse(Integer.parseUnsignedInt(zString.substring(tmp, tmp + 32), 2));
        }

        LocalDateTime start = LocalDateTime.now();

        Set<Integer> xSeeds = IntStream.range(1, I30)
                .parallel()
                .filter(x -> countStatisticL1(x) < C)
                .boxed()
                .collect(Collectors.toSet());

        System.out.println(Duration.between(start, LocalDateTime.now()));

        Set<Integer> ySeeds = IntStream.range(1, I31)
                .parallel()
                .filter(y -> countStatisticL2(y) < C)
                .boxed()
                .collect(Collectors.toSet());

        System.out.println(Duration.between(start, LocalDateTime.now()));

        for (final Integer potentialX : xSeeds) {
            for (final Integer potentialY : ySeeds) {
                IntStream.range(Integer.MIN_VALUE, Integer.MAX_VALUE)
                        .parallel()
                        .filter(s -> {
                            int x = potentialX;
                            // fill 30-th and 31-th bits
                            x = (((x ^ (x >>> 1) ^ (x >>> 4) ^ (x >>> 6)) & 3) << 30) | x;
                            int y = potentialY;
                            // fill 31-th bit
                            y = (((y ^ (y >>> 3)) & 1) << 31) | y;
                            if ((s & x ^ (~s & y)) != Z[0]) {
                                return false;
                            }
                            // ordered
                            for (int j = 1; j < N; j++) {
                                // shift all registers on 32 bits (integer size)
                                for (int k = 0, bit; k < Integer.SIZE; k++) {
                                    bit = 1 & ((x >>> 2) ^ (x >>> 3) ^ (x >>> 6) ^ (x >>> 8));
                                    x >>>= 1;
                                    x |= bit << 31;
                                    bit = 1 & ((y >>> 1) ^ (y >>> 4));
                                    y >>>= 1;
                                    y |= bit << 31;
                                    bit = 1 & (s ^ (s >>> 1) ^ (s >>> 2) ^ (s >>> 3) ^ (s >>> 5) ^ (s >>> 7));
                                    s >>>= 1;
                                    s |= bit << 31;
                                }
                                if (((s & x) ^ (~s & y)) != Z[j]) {
                                    return false;
                                }
                            }
                            return true;
                        })
                        .findAny()
                        .ifPresent(s -> {
                            String s1 = String.format("%32s", Integer.toBinaryString(Integer.reverse(potentialX << 2)))
                                    .replaceAll(" ", "0");
                            System.out.println(s1);
                            String s2 = String.format("%32s", Integer.toBinaryString(Integer.reverse(potentialY << 1)))
                                    .replaceAll(" ", "0");
                            System.out.println("L2: " + s2);
                            String s3 = String.format("%32s", Integer.toBinaryString(Integer.reverse(s)))
                                    .replaceAll(" ", "0");
                            System.out.println("L3: " + s3);
                            System.out.println(Duration.between(start, LocalDateTime.now()));
                            System.exit(0);
                        });
            }
        }
    }

    private static int countStatisticL1(int x) {
        int R = 0;
        // fill 30-th and 31-th bits
        int bits = (x ^ (x >>> 1) ^ (x >>> 4) ^ (x >>> 6)) & 3;
        x = (bits << 30) | x;
        // Ordered
        for (int z : Z) {
            R = R + Integer.bitCount(x ^ z);
            // shift register on 32 bits (integer size)
            for (int j = 0; j < Integer.SIZE; j++) {
                x = ((((x >>> 2) ^ (x >>> 3) ^ (x >>> 6) ^ (x >>> 8)) & 1) << 31) | (x >>> 1);
            }
        }
        return R;
    }

    private static int countStatisticL2(int y) {
        int R = 0;
        // fill 31-th bit
        y = (((y ^ (y >>> 3)) & 1) << 31) | y;
        // Ordered
        for (int z : Z) {
            R = R + Integer.bitCount(y ^ z);
            // shift register on 32 bits (integer size)
            for (int j = 0; j < 32; j++) {
                y = ((((y >>> 1) ^ (y >>> 4)) & 1) << 31) | (y >>> 1);
            }
        }
        return R;
    }
}
