package com.taoyiyi.chat.util;


import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;


public class RandomUtil {

    public static final Integer HUNDRED_THOUSAND = 100000;

    private static final AtomicInteger POINT = new AtomicInteger(0);

    private static final String STR_0 = "0";

    private static final String STR_1 = "1";

    private static final String STR_9 = "9";
    private static final int LEN_9 = 9;

    private static final String PREFIX_DEFAULT = "X";

    private static final Integer MIN = 1;

    private static final Integer MAX = 999999999;

    private static final String INFIX_COVER = "%016d";

    private static final String SUFFIX_COVER = "%09d";

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; // 包含 1-9、a-z 和 A-Z 的字符集合


    public static String generate(int length) {

        String l = STR_1;
        String r = STR_9;

        for (int i = 1; i < length; i++) {
            l = l + STR_0;
            r = r + STR_9;
        }

        return String.valueOf(ThreadLocalRandom.current().nextLong(Long.valueOf(l), Long.valueOf(r)));

    }

    public static int generate(int left, int right) {

        if (left == right) return left;

        return ThreadLocalRandom.current().nextInt(left, right);

    }


    public static String generate(String prefix) {

        if (!StringUtils.hasLength(prefix)) {
            prefix = PREFIX_DEFAULT;
        }

        String sb = prefix + infix() + suffix();

        return sb;

    }

    private static String infix() {

        Long value = System.currentTimeMillis();

        return String.format(INFIX_COVER, value);

    }

    public static String genOutTradeNo() {
        return generateRandomString("OTN");
    }

    public static String generateRandomStringLen(String prefix, Integer len) {
        String hexTimestamp = Long.toHexString(System.currentTimeMillis()); // 获取系统毫秒值，并转换为 16 进制字符串
        StringBuilder sb = new StringBuilder(prefix);
        int remainingLength = len; // 剩余需要生成的字符串长度
        int timestampLength = hexTimestamp.length();
        if (timestampLength > remainingLength) {
            sb.append(hexTimestamp.substring(timestampLength - remainingLength));
        } else {
            sb.append(hexTimestamp);
            remainingLength -= timestampLength;
            while (remainingLength > 0) {
                sb.append(generateRandomCharacter());
                remainingLength--;
            }
        }
        return sb.toString();
    }

    public static String generateRandomString(String prefix, Integer length) {
        if (length == null) {
            length = LEN_9;
        }
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(prefix);

        // 生成至少一个数字
        sb.append(Character.forDigit(rand.nextInt(10), 10));

        // 生成至少一个大写字母
        sb.append((char) (rand.nextInt(26) + 'A'));

        // 生成至少一个小写字母
        sb.append((char) (rand.nextInt(26) + 'a'));

        // 生成其余随机字符
        for (int i = 3 + prefix.length(); i < length; i++) {
            sb.append(CHARACTERS.charAt(rand.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }


    public static String generateRandomString(String prefix) {
        String hexTimestamp = Long.toHexString(System.currentTimeMillis()); // 获取系统毫秒值，并转换为 16 进制字符串
        StringBuilder sb = new StringBuilder(prefix);
        int remainingLength = 32 - prefix.length(); // 剩余需要生成的字符串长度
        int timestampLength = hexTimestamp.length();
        if (timestampLength > remainingLength) {
            sb.append(hexTimestamp.substring(timestampLength - remainingLength));
        } else {
            sb.append(hexTimestamp);
            remainingLength -= timestampLength;
            while (remainingLength > 0) {
                sb.append(generateRandomCharacter());
                remainingLength--;
            }
        }
        return sb.toString();
    }


    private static char generateRandomCharacter() {
        int index = (int) (Math.random() * CHARACTERS.length());
        return CHARACTERS.charAt(index);
    }

    private static String suffix() {

        Integer l = ThreadLocalRandom.current().nextInt(MIN, MAX);
        Integer r = POINT.incrementAndGet();

        if (r.compareTo(MIN) <= 0) {
            POINT.set(l);
            r = l;
        }

        if (r.compareTo(MAX) > 0) {
            POINT.set(l);
            r = POINT.get();
        }

        return String.format(SUFFIX_COVER, l) + String.format(SUFFIX_COVER, r);

    }


}
