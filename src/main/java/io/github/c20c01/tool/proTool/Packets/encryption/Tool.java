package io.github.c20c01.tool.proTool.Packets.encryption;

import com.google.gson.Gson;
import io.github.c20c01.Main;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Tool {
    private static String accessToken;
    private static String uuid;

    public static void setAccessToken(String accessToken) {
        Tool.accessToken = accessToken;
    }

    public static void setUuid(String uuid) {
        Tool.uuid = uuid;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static String getUuid() {
        return uuid;
    }

    public static void login(String Id) throws IOException {
        Gson gson = new Gson();
        JoinMinecraftServerRequest request = new JoinMinecraftServerRequest();
        request.accessToken = accessToken;
        request.selectedProfile = uuid;
        request.serverId = Id;
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/join");
        Main.output("server has authentication enabled.", true);
        Main.output("sending the verification data.", true);
        Main.output(performPostRequest(url, gson.toJson(request), "application/json") ? "verification succeeded." : "verification failed.", true);
    }

    public static boolean performPostRequest(final URL url, final String post, final String contentType) {
        try {
            HttpURLConnection connection = createUrlConnection(url);
            byte[] postAsBytes = post.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
            connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(postAsBytes);
            return (connection.getResponseCode() + "").startsWith("2");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static HttpURLConnection createUrlConnection(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setUseCaches(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static PublicKey byteToPublicKey(byte[] p_13601_) {
        EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(p_13601_);
        try {
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(encodedkeyspec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKey generateSecretKey() {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(128);
            return keygenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] cipherData(int i, Key key, byte[] bytes) {
        try {
            return setupCipher(i, key.getAlgorithm(), key).doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Cipher getCipher(int i, Key key) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(i, key, new IvParameterSpec(key.getEncoded()));
        return cipher;
    }

    private static Cipher setupCipher(int i, String s, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(s);
        cipher.init(i, key);
        return cipher;
    }

    public static byte[] digestData(String s, PublicKey publicKey, SecretKey secretKey) throws Exception {

        return digestData(s.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());

    }

    private static byte[] digestData(byte[]... bytes) throws Exception {
        MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");

        for (byte[] aByte : bytes) {
            messagedigest.update(aByte);
        }
        return messagedigest.digest();
    }

    public static byte[] encryptUsingKey(Key key, byte[] bytes) {
        return cipherData(1, key, bytes);
    }

    public static byte[] decryptUsingKey(Key key, byte[] bytes) {
        return cipherData(2, key, bytes);
    }
}
