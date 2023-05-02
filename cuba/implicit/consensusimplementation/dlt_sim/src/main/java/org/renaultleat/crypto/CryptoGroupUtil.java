package org.renaultleat.crypto;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
//import org.apache.commons.codec.binary.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import com.google.common.hash.Hashing;

import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import net.i2p.crypto.eddsa.EdDSASecurityProvider;

public final class CryptoGroupUtil {
    // https://howtodoinjava.com/java15/java-eddsa-example/
    // https://snipplr.com/view/18368/saveload--private-and-public-key-tofrom-a-file

    private static final String pubkeyfile = "groupkeys/public";

    private static final String privatekeyfile = "groupkeys/private";

    public static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = CryptoUtil.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            // return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    private static InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = CryptoUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    public static KeyPair getKeyPair(int index)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException {
        // Read Public Key of Group
        File pubfile = getFileFromResource(pubkeyfile + "/group" + index);
        FileInputStream publicfis = new FileInputStream(pubfile);
        byte[] encodedPublicKey = new byte[(int) pubfile.length()];
        publicfis.read(encodedPublicKey);
        publicfis.close();
        // Read Private Key of Group
        File privatefile = getFileFromResource(privatekeyfile + "/group" + index);
        FileInputStream privatefis = new FileInputStream(privatefile);
        byte[] encodedPrivateKey = new byte[(int) privatefile.length()];
        privatefis.read(encodedPrivateKey);
        privatefis.close();
        // Generate Key Pair
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);

    }

    public static PublicKey getPublicKey(int index)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException {
        File pubfile = getFileFromResource(pubkeyfile + "/group" + index);
        FileInputStream publicfis = new FileInputStream(pubfile);
        byte[] encodedPublicKey = new byte[(int) pubfile.length()];
        DataInputStream dis = new DataInputStream(publicfis);
        dis.readFully(encodedPublicKey);
        dis.close();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;

    }

    public static String getPublicKeyString(int index)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException {
        File pubfile = getFileFromResource(pubkeyfile + "/group" + index);
        FileInputStream publicfis = new FileInputStream(pubfile);
        byte[] encodedPublicKey = new byte[(int) pubfile.length()];
        publicfis.read(encodedPublicKey);
        publicfis.close();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        String pubkeystring = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return pubkeystring;

    }

    public static PrivateKey getPrivateKey(int index)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException {
        File privatefile = getFileFromResource(privatekeyfile + "/group" + index);
        FileInputStream privatefis = new FileInputStream(privatefile);
        DataInputStream dis = new DataInputStream(privatefis);
        byte[] encodedPrivateKey = new byte[(int) privatefile.length()];
        dis.readFully(encodedPrivateKey);
        // privatefis.read(encodedPrivateKey);
        dis.close();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;

    }

    // Ref: https://www.devglan.com/java8/rsa-encryption-decryption-java
    public static String getEncryptedData(int index, String message)
            throws Exception {
        PrivateKey privateKey = getPrivateKey(2);
        byte[] msg = message.getBytes();
        /////////////////// ENCRYPTION /////////////////////////
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] cypherbytes = cipher.doFinal(msg);
        /////////////////// ENCRYPTION END /////////////////////
        String encodedString = Base64.getMimeEncoder().encodeToString(cypherbytes);
        return encodedString;
    }

    public static String getSignature(int index, String message)
            throws NoSuchAlgorithmException, SignatureException,
            InvalidKeyException, InvalidKeySpecException, IOException, URISyntaxException {
        PrivateKey privateKey = getPrivateKey(index);

        byte[] msg = message.getBytes();
        Signature signature = Signature.getInstance("NONEwithRSA");
        signature.initSign(privateKey);
        signature.update(msg);
        byte[] s = signature.sign();
        // To get singature as String
        String encodedString = Base64.getEncoder().encodeToString(s);
        return encodedString;

    }

    public static String getDecryptedData(int groupId, String encryptedMessage) throws Exception {
        PublicKey publicKey = getPublicKey(2);
        byte[] encryptedData = Base64.getMimeDecoder().decode(encryptedMessage);
        /////////////////// DECRYPTION /////////////////////////
        // System.out.println(encryptedMessage.toString());
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedCypherbytes = cipher.doFinal(encryptedData);
        /////////////////// DECRYPTION END /////////////////////////
        String decryptedString = new String(decryptedCypherbytes);

        return decryptedString;

    }

    // Verify a signature
    // Used for transaction verification
    public static boolean verify(int index, String signatureinput, String message) throws NoSuchAlgorithmException,
            SignatureException, InvalidKeyException, InvalidKeySpecException, IOException, URISyntaxException {
        PublicKey publicKey = getPublicKey(index);
        byte[] msgbytes = message.getBytes();
        byte[] signaturebytes = Base64.getDecoder().decode(signatureinput);
        Signature signature = Signature.getInstance("NONEwithRSA");
        signature.initVerify(publicKey);
        signature.update(msgbytes);
        boolean bool = signature.verify(signaturebytes);
        return bool;
    }

    // Verify a signature by using public key passed as string
    // Used for block verification
    public static boolean verify(String pubKeyStr, String signatureinput, String message)
            throws NoSuchAlgorithmException,
            SignatureException, InvalidKeyException, InvalidKeySpecException, IOException {
        byte[] pubKeyBytes = Base64.getDecoder().decode(pubKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                pubKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        byte[] msgbytes = CryptoUtil.getHash(message).getBytes();
        byte[] signaturebytes = Base64.getDecoder().decode(signatureinput);
        Signature signature = Signature.getInstance("NONEwithRSA");
        signature.initVerify(publicKey);
        signature.update(msgbytes);
        boolean bool = signature.verify(signaturebytes);
        return bool;
    }

    // UUID Version 4 Pseudo Random Generator
    public static String getUniqueIdentifier() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    // Get SHA256 Hash of a String Data in String format
    public static String getHash(String data) {
        String sha256Hex = Hashing.sha256().hashString(data, StandardCharsets.UTF_8).toString();
        return sha256Hex;
    }

    private CryptoGroupUtil() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

}
