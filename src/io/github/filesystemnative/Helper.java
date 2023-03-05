

package io.github.filesystemnative;

import io.github.logging.Logger;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.io.File;

public class Helper {
    public static byte[] readFileSystemFromNativeFileSystem(String path) {
        File f = new File(path);
        byte[] _filebuffer = new byte[(int) f.length()];
        FileInputStream _fInputStream = null;
        try {
            _fInputStream = new FileInputStream(f);
            _fInputStream.read(_filebuffer);
        } catch (Exception e) {
            Logger.getInstance().LogError(e.getMessage());
        }
        return _filebuffer;
    }

    public static void writeFileSystemToNativeFileSystem(byte[] _buffer, String path) {

        // FileUtils.writeByteArrayToFile(new FileInfo(path), _buffer);
        ByteArrayOutputStream bArrOutStream = new ByteArrayOutputStream();
        ObjectOutput objOut = null;
        try {
            objOut = new ObjectOutputStream(bArrOutStream);
            objOut.writeObject(_buffer);
            objOut.flush();
            byte[] bytesToWrite = bArrOutStream.toByteArray();
            FileUtils.writeByteArrayToFile(new File(path), bytesToWrite);
        } catch (IOException e) {
            Logger.getInstance().LogError(e.getMessage());
        } finally {
            try {
                bArrOutStream.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void saveFileSystemState(vFileSystem vfs) throws Exception {
        ObjectOutputStream objOutStream = new ObjectOutputStream(new FileOutputStream("temp.bin"));
        objOutStream.writeObject(vfs);
        objOutStream.close();
    }
}