package crypto;

import javafx.util.Pair;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;

import static java.math.BigInteger.*;

public class RSA {

    // e = 2^16 + 1
    private static final BigInteger PUBLIC_EXPONENT = new BigInteger("10001", 16);

    public static void main(String[] args) {
        int bitLength = 256;
        Pair<BigInteger, BigInteger> pq1 = generateSimpleNumberPair(bitLength);
        BigInteger p1 = pq1.getKey();
        BigInteger q1 = pq1.getValue();
        // n1 = p1 * q1
        BigInteger n1 = pq1.getKey().multiply(pq1.getValue());
        System.out.println("p1=" + p1.toString(16));
        System.out.println("q1=" + q1.toString(16));
        System.out.println("n1=" + n1.toString(16).toUpperCase());

        Pair<BigInteger, BigInteger> pq2 = generateSimpleNumberPair(bitLength);
        BigInteger p2 = pq2.getKey();
        BigInteger q2 = pq2.getValue();
        // n2 = p2 * q2
        BigInteger n2 = p2.multiply(q2);
        // n1 > n2
        while (n1.compareTo(n2) > 0) {
            pq2 = generateSimpleNumberPair(bitLength);
            p2 = pq2.getKey();
            q2 = pq2.getValue();
            // n2 = p2 * q2
            n2 = p2.multiply(q2);
        }
        System.out.println("p2=" + p2.toString(16));
        System.out.println("q2=" + q2.toString(16));
        System.out.println("n2=" + n2.toString(16));

        BigInteger d1 = getD(p1, q1, PUBLIC_EXPONENT);
        System.out.println("A secret key");
        System.out.println("d1 = " + d1.toString(16));
        System.out.println("n1 = " + n1.toString(16));
        BigInteger d2 = getD(p2, q2, PUBLIC_EXPONENT);
        System.out.println("B secret key");
        System.out.println("d2 = " + d2.toString(16));
        System.out.println("n2 = " + n2.toString(16));


        BigInteger message = new BigInteger("ABCDE", 16);

        BigInteger encryptedMessage = encrypt(message, PUBLIC_EXPONENT, n1);
        BigInteger decryptedMessage = decrypt(encryptedMessage, d1, n1);
        System.out.println("Message: " + message.toString(16));
        System.out.println("encryptedMessage: " + encryptedMessage.toString(16));
        System.out.println("decryptedMessage: " + decryptedMessage.toString(16));


        Pair<BigInteger, BigInteger> notification = sendKey(d1, n1, n2, PUBLIC_EXPONENT, message);
        BigInteger k1 = notification.getKey();
        BigInteger s1 = notification.getValue();
        System.out.println("k1=" + k1);
        System.out.println("s1=" + s1);
        System.out.println(receiveKey(n1, PUBLIC_EXPONENT, d2, n2, k1, s1));

        // Public key server
        BigInteger modulus = new BigInteger("CF82A550D01C1361928BDAEBCD195FCDC296ACF7C2EB24E32EF07B8674EA1157DFACAD3776DABE682936ECBB8D1283620E71907A05570740DF3FBBAD2775CB367401E1321038814F595659506FC17E6CEB09700AAA9E5F2EE13000BC9E81B359", 16);
        BigInteger exp = new BigInteger("10001", 16);
        BigInteger key = new BigInteger("123456ABCDE", 16);

        if (modulus.compareTo(n1) < 0) {
            throw new IllegalArgumentException("n1 < n");
        }
        Pair<BigInteger, BigInteger> sendKeyPair = sendKey(
                // private key
                d1, n1,
                // public key server
                modulus, exp,
                // message
                key);
        BigInteger k2 = sendKeyPair.getKey();
        BigInteger s2 = sendKeyPair.getValue();
        System.out.println("k2=" + k2.toString(16).toUpperCase());
        System.out.println("s2=" + s2.toString(16).toUpperCase());
    }

    private static Pair<BigInteger, BigInteger> generateSimpleNumberPair(int bitLength) {
        BigInteger coreP, coreQ; // p = 2*i*coreP + 1, q = 2*i*coreQ + 1; i = 1, 2...
        coreP = generatePrime(bitLength);
        coreQ = generatePrime(bitLength);
        while (Objects.equals(coreP, coreQ)) {
            coreQ = generatePrime(bitLength);
        }
        int i = 1;
        BigInteger p, q;
        do {
            // p = 2 * i * coreP + 1;
            p = TWO.multiply(valueOf(i)).multiply(coreP).add(ONE);
            i++;
        } while (!isPrime(p, 10));
        i = 1;
        do {
            // q = 2 * i * coreQ + 1;
            q = TWO.multiply(valueOf(i)).multiply(coreQ).add(ONE);
            i++;
        } while (!isPrime(q, 10));
        return new Pair<>(p, q);
    }

    private static BigInteger generatePrime(int bitLength) {
        BigInteger number;
        do {
            number = gen(bitLength);
        } while (!isPrime(number, 20));
        return number;
    }

    private static BigInteger gen(int bitLength) {
        return BigInteger.probablePrime(bitLength, new Random());
    }

    private static boolean isPrime(BigInteger numberToCheck, int rounds) {
        // numberToCheckCopy = numberToCheck - 1
        BigInteger numberToCheckCopy = numberToCheck.subtract(ONE);
        int s = 0;
        // numberToCheck % 2 == 0
        while (numberToCheckCopy.remainder(TWO).equals(ZERO)) {
            s++;
            // numberToCheckCopy = numberToCheckCopy / 2
            numberToCheckCopy = numberToCheckCopy.divide(TWO);
        }
        BigInteger d = numberToCheckCopy; // d is odd

        BigInteger temp, X;
        for (int i = 0; i < rounds; i++) {
            // temp = (1 + gen()) % numberToCheck - 2;
            temp = ONE.add(gen(255)).remainder(numberToCheck).subtract(TWO);
            // X = (temp ^ d) mod numberToCheck
            X = temp.modPow(d, numberToCheck);
            // (numberToCheck == 1 || numberToCheck == (numberToCheck - 1))
            if (X.equals(ONE) || X.equals(numberToCheck.subtract(ONE))) {
                continue;
            }
            for (int j = 0; j < s - 1; j++) {
                // X = (X ^ 2) mod numberToCheck
                X = X.modPow(TWO, numberToCheck);
                // (X == 1)
                if (X.equals(ONE)) {
                    return false;
                // (X == numberToCheck - 1)
                } else if (X.equals(numberToCheck.subtract(ONE))) {
                    break;
                }
            }
            return false;
        }
        return true;
    }

    private static BigInteger getD(BigInteger p, BigInteger q, BigInteger e) {
        // Euler function; fi(n) = (p-1)*(q-1)
        BigInteger fi = p.subtract(ONE).multiply( q.subtract(ONE) );
        System.out.println("gcd(e,fi)=" + e.gcd(fi));
        // d = e^(-1) mod fi
        BigInteger d = e.modInverse(fi);
        System.out.println("e*d mod fi = " + e.multiply(d).remainder(fi));
        return d;
    }

    private static BigInteger encrypt(BigInteger message,
                                      BigInteger e,
                                      BigInteger n) {
        // C = M^e mod n
        return message.modPow(e, n);
    }

    private static BigInteger decrypt(BigInteger cipherText,
                                      BigInteger d,
                                      BigInteger n) {
        // M = C^d mod n
        return cipherText.modPow(d, n);
    }

    private static Pair<BigInteger, BigInteger> sendKey(BigInteger d,
                                                        BigInteger n,
                                                        BigInteger n1,
                                                        BigInteger e,
                                                        BigInteger k) {
        // s = k(message)^d mod n;
        BigInteger s = k.modPow(d, n);
        // s1 = s^e mod n1;
        BigInteger s1 = s.modPow(e, n1);
        // k1 = k(message)^e mod n1;
        BigInteger k1 = k.modPow(e, n1);
        return new Pair<>(k1, s1);
    }


    private static boolean receiveKey(BigInteger n,
                                      BigInteger e,
                                      BigInteger d1,
                                      BigInteger n1,
                                      BigInteger k1,
                                      BigInteger s1) {
        // message = k1^d1 mod n1
        BigInteger message = k1.modPow(d1, n1);
        // s = s1^d1 mod n1
        BigInteger s = s1.modPow(d1, n1);
        // message == s^e mod n
        BigInteger x = s.modPow(e, n);
        return message.equals(x);
    }
}
