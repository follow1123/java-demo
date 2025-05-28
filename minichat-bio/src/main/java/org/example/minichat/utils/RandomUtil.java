package org.example.minichat.utils;

import java.util.*;

public class RandomUtil {

    private static final Random random = new Random();

    public static int getRandomInteger(int bound) {
        return random.nextInt(bound);
    }

    public static char getRandomChar() {
        int i = getRandomInteger(62);
        if (i < 10) return (char) (48 + i);
        if (i < 36) return (char) (65 + i - 10);
        if (i < 62) return (char) (97 + i - 10 - 26);
        throw new RuntimeException("error num " + i);
    }

    public static String getRandStr(int len) {
        char[] data = new char[len];
        for (int i = 0; i < len; i++) {
            data[i] = getRandomChar();
        }
        return new String(data);
    }

    public static List<Integer> getRandomIntList(int size, int bound) {
        ArrayList<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(getRandomInteger(bound));
        }
        return list;
    }

    public static List<String> getRandomStringList(int size, int maxLength) {
        ArrayList<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int length = getRandomInteger(maxLength) + 1;
            char[] chars = new char[length];
            for (int j = 0; j < length; j++) {
                chars[j] = getRandomChar();
            }

            list.add(new String(chars));
        }
        return list;
    }

    public static Set<Integer> getRandomIntSet(int size, int bound) {
        return new HashSet<>(getRandomIntList(size, bound));
    }

    public static Set<String> getRandomStringSet(int size, int maxLength) {
        return new HashSet<>(getRandomStringList(size, maxLength));
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
