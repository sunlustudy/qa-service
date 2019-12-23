package io.choerodon.infra.utils;

import java.util.Random;

/*
 * @description:
 * @program: springboot
 * @author: syun
 * @create: 2019-08-28 15:59
 */
public class RandomUtils {


    private final static int CODE_LEN = 5;

    public static String generateCode() {
        char[] arr = new char[5];
        for (int i = 0; i < CODE_LEN; i++) {
            arr[i] = 0 == (int) (Math.random() * 2) ? //随机生成大写或小写字母
                    (char) ('A' + (int) (Math.random() * 26)) :
                    (char) ('a' + (int) (Math.random() * 26));
        }

        return new String(arr);
    }

    public static String generateCodeNum() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }


    public static String cutString(String token) {
        return token.substring(token.length() - 10);
    }

}
