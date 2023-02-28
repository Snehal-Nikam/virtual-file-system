

package io.github.snehal.utils;

import io.github.snehal.logging.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionProvider {
    //returns MD5 of given byte array
    public static String getMD5(byte[] data) throws IOException {
        Logger logger = Logger.getInstance();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.LogError(e.getMessage());
        }
        md.update(data);
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }
}