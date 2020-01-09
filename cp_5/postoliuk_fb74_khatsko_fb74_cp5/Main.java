package com.company;

import javafx.util.Pair;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;

import static java.math.BigInteger.*;

public class Main {

    private static final BigInteger MIN_BOUND = new BigInteger("35742549198872617291353508656626642567");
    private static final BigInteger MAX_BOUND = new BigInteger("3574254919887261729135350865662664256778680092345");

    // e = 2^16 + 1
    private static final BigInteger E = TWO.pow(16).add(ONE);

    public static void main(String[] args) {
        Pair<BigInteger, BigInteger> pq1 = generateSimpleNumberPair();
        BigInteger p1 = pq1.getKey();
        BigInteger q1 = pq1.getValue();
        // n1 = p1 * q1
        BigInteger n1 = pq1.getKey().multiply(pq1.getValue());
        System.out.println("p1=" + p1);
        System.out.println("q1=" + q1);
        System.out.println("n1=" + n1);

        Pair<BigInteger, BigInteger> pq2 = generateSimpleNumberPair();
        BigInteger p2 = pq2.getKey();
        BigInteger q2 = pq2.getValue();
        // n2 = p2 * q2
        BigInteger n2 = pq2.getKey().multiply(pq2.getValue());
        // n1 < n2
        while (n1.compareTo(n2) < 0) {
            pq2 = generateSimpleNumberPair();
            p2 = pq2.getKey();
            q2 = pq2.getValue();
            // n2 = p2 * q2
            n2 = p2.multiply(q2);
        }
        System.out.println("p2=" + p2);
        System.out.println("q2=" + q2);
        System.out.println("n2=" + n2);

        BigInteger d1 = getD(p1, q1);
        System.out.println("A parameters");
        System.out.println("d = " + d1);
        System.out.println("n = " + n1);
        BigInteger d2 = getD(p2, q2);
        System.out.println("B parameters");
        System.out.println("d = " + d2);
        System.out.println("n = " + n2);

        
        BigInteger message = new BigInteger("8A323E34209BA7068FD7C190C132122F859ECA9F9C6057AADBCE86D58C2395CD", 16);

        BigInteger encryptedMessage = encrypt(message, n1);
        BigInteger decryptedMessage = decrypt(encryptedMessage, d1, n1);
        System.out.println("Message: " + message);
        System.out.println("encryptedMessage: " + encryptedMessage);
        System.out.println("decryptedMessage: " + decryptedMessage);
        
        
        Pair<BigInteger, BigInteger> notification = sendKey(d1, n1, n2, E, message);
        BigInteger k1 = notification.getKey();
        BigInteger s1 = notification.getValue();
        System.out.println("k1=" + k1);
        System.out.println("s1=" + s1);
        System.out.println(receiveKey(n1, d2, n2, k1, s1));

        // Public key server
        BigInteger modulus = new BigInteger("BDA2A6415BE9AD90013121CA74A5431E1ECE0C2A38E8894B22904CE612DA397B4B78894E3ED46398E74F037C0BFF52968F4C19D0D6CCB824D79BFE86BD3DC231", 16);
        BigInteger exp = new BigInteger("10001", 16);
        BigInteger key = new BigInteger("381A125EA7520651FD231565441969916A667DE71108CC0BDCECA8B3C08E3D093F6109890B63927B74E6", 16);

        Pair<BigInteger, BigInteger> sendKeyPair = sendKey(d1, n1, modulus, exp, key);
        System.out.println("Key: " + sendKeyPair.getKey());
        System.out.println("Sign: " + sendKeyPair.getValue());
    }

    private static Pair<BigInteger, BigInteger> generateSimpleNumberPair() {
        BigInteger coreP, coreQ, q; // p = 2*i*coreP + 1, q = 2*i*coreQ + 1; i = 1, 2...
        coreP = generatePrime();
        coreQ = generatePrime();
        while (Objects.equals(coreP, coreQ)) {
            coreQ = generatePrime();
        }
        int i = 1;
        BigInteger p;
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

    private static BigInteger generatePrime() {
        BigInteger number;
        do {
            // number = (gen() % MAX_BOUND + MIN_BOUND) % MAX_BOUND;
            number = gen().remainder(MAX_BOUND).add(MIN_BOUND).remainder(MAX_BOUND);
        } while (!isPrime(number, 15));
        return number;
    }

    private static BigInteger gen() {
        return BigInteger.probablePrime(256, new Random());
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
            temp = ONE.add(gen()).remainder(numberToCheck).subtract(TWO);
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

    private static BigInteger getD(BigInteger p, BigInteger q) {
        // Euler function; fi(n) = (p-1)*(q-1)
        BigInteger fi = p.subtract(ONE).multiply( q.subtract(ONE) );
        System.out.println("gcd(e,fi)=" + E.gcd(fi));
        // d = e^(-1) mod fi
        BigInteger d = E.modInverse(fi);
        System.out.println("e*d mod fi = " + E.multiply(d).remainder(fi));
        return d;
    }

    private static BigInteger encrypt(BigInteger message,
                                      BigInteger n) {
        // C = M^e mod n
        return message.modPow(E, n);
    }

    private static BigInteger decrypt(BigInteger cipherText,
                                      BigInteger d,
                                      BigInteger n) {
        // M = C^d mod n
        return cipherText.modPow(d, n);
    }

    private static Pair<BigInteger, BigInteger> sendKey(BigInteger d1,
                                                        BigInteger n1,
                                                        BigInteger n2,
                                                        BigInteger e,
                                                        BigInteger message) {
        // s = k(message)^d1 mod n1;
        BigInteger s = message.modPow(d1, n1);
        // k1 = k(message)^e mod n1;
        BigInteger k1 = message.modPow(e, n2);
        // s1 = s^e mod n1;
        BigInteger s1 = s.modPow(e, n2);
        return new Pair<>(k1, s1);
    }


    private static boolean receiveKey(BigInteger n1,
                                      BigInteger d2,
                                      BigInteger n2,
                                      BigInteger k1,
                                      BigInteger s1) {
        // message = k1^d2 mod n2
        BigInteger message = k1.modPow(d2, n2);
        // s = s1^d2 mod n2
        BigInteger s = s1.modPow(d2, n2);
        // message == s^e mod n1
        return message.equals(s.modPow(Main.E, n1));
    }
}
