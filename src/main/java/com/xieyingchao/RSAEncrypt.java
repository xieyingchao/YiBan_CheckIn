package com.xieyingchao;


import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 -----BEGIN PUBLIC KEY-----
 MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAxbzZk3gEsbSe7A95iCIk
 59Kvhs1fHKE6zRUfOUyTaKd6Rzgh9TB/jAK2ZN7rHzZExMM9kw6lVwmlV0VabSmO
 YL9OOHDCiEjOlsfinlZpMZ4VHg8gkFoOeO4GvaBs7+YjG51Am6DKuJWMG9l1pAge
 96Uhx8xWuDQweIkjWFADcGLpDJJtjTcrh4fy8toE0/0zJMmg8S4RM/ub0q59+VhM
 zBYAfPmCr6YnEZf0QervDcjItr5pTNlkLK9E09HdKI4ynHy7D9lgLTeVmrITdq++
 mCbgsF/z5Rgzpa/tCgIQTFD+EPPB4oXlaOg6yFceu0XUQEaU0DvAlJ7Zn+VwPkkq
 JEoGudklNePHcK+eLRLHcjd9MPgU6NP31dEi/QSCA7lbcU91F3gyoBpSsp5m7bf5
 //OBadjWJDvl2KML7NMQZUr7YXqUQW9AvoNFrH4edn8d5jY5WAxWsCPQlOqNdybM
 vKF2jhjIE1fTWOzK+AvvFyNhxer5bWGU4S5LTr7QNXnvbngXCdkQfrcSn/ydQXP0
 vXfjf3NhpluFXqWe5qUFKXvjY6+PdrE/lvTmX4DdvUIu9NDa2JU9mhwAPPR1yjjp
 4IhgYOTQL69ZQcvy0Ssa6S25Xi3xx2XXbdx8svYcQfHDBF1daK9vca+YRX/DzXxl
 1S4uGt+FUWSwuFdZ122ZCZ0CAwEAAQ==
 -----END PUBLIC KEY-----
 */

/**
 * @author ：XieYingChao
 * @description：TODO
 * @date ：2020/7/25 11:34
 */
public class RSAEncrypt {
    private static String publicString =   "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAxbzZk3gEsbSe7A95iCIk" +
            "59Kvhs1fHKE6zRUfOUyTaKd6Rzgh9TB/jAK2ZN7rHzZExMM9kw6lVwmlV0VabSmO" +
            "YL9OOHDCiEjOlsfinlZpMZ4VHg8gkFoOeO4GvaBs7+YjG51Am6DKuJWMG9l1pAge" +
            "96Uhx8xWuDQweIkjWFADcGLpDJJtjTcrh4fy8toE0/0zJMmg8S4RM/ub0q59+VhM" +
            "zBYAfPmCr6YnEZf0QervDcjItr5pTNlkLK9E09HdKI4ynHy7D9lgLTeVmrITdq++" +
            "mCbgsF/z5Rgzpa/tCgIQTFD+EPPB4oXlaOg6yFceu0XUQEaU0DvAlJ7Zn+VwPkkq" +
            "JEoGudklNePHcK+eLRLHcjd9MPgU6NP31dEi/QSCA7lbcU91F3gyoBpSsp5m7bf5" +
            "//OBadjWJDvl2KML7NMQZUr7YXqUQW9AvoNFrH4edn8d5jY5WAxWsCPQlOqNdybM" +
            "vKF2jhjIE1fTWOzK+AvvFyNhxer5bWGU4S5LTr7QNXnvbngXCdkQfrcSn/ydQXP0" +
            "vXfjf3NhpluFXqWe5qUFKXvjY6+PdrE/lvTmX4DdvUIu9NDa2JU9mhwAPPR1yjjp" +
            "4IhgYOTQL69ZQcvy0Ssa6S25Xi3xx2XXbdx8svYcQfHDBF1daK9vca+YRX/DzXxl" +
            "1S4uGt+FUWSwuFdZ122ZCZ0CAwEAAQ==";

    public static String passwdEncrypt(String passwd) throws Exception {

        PublicKey publicKey = getPublicKey();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(passwd.getBytes());
        return new String(Base64.encodeBase64(encryptedBytes));
    }

    /**
     * @TODO 将公钥字符串转换为公钥
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey() throws Exception {
        byte[] bytes = Base64.decodeBase64(publicString.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
