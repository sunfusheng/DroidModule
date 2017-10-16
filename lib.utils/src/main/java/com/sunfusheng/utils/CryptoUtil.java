package com.sunfusheng.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class CryptoUtil {

    public static byte[] des3EncodeECB(byte[] key, byte[] data) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        return cipher.doFinal(data);
    }

    public static byte[] des3DecodeECB(byte[] key, byte[] data) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, deskey);

        return cipher.doFinal(data);
    }

    public static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        return cipher.doFinal(data);
    }

    public static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        return cipher.doFinal(data);
    }

    public static String MD5Hash(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            return CodecUtil.toHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String MD5Hash(String string) {
        try {
            return MD5Hash(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String MD5Hash(File file) {
        InputStream fileInputStream = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];

            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) > 0) {
                md.update(buffer, 0, byteRead);
            }
            return CodecUtil.toHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String generateSalt() {
        double r = Math.random();
        String md5String = MD5Hash(String.valueOf(r));
        String salt = md5String;
        try {
            if (!TextUtils.isEmpty(salt) && salt.length() > 8) {
                salt = md5String.substring(0, 8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salt;
    }
}
