import io.github.constants.FileSystemConfiguration;
import io.github.exception.vFSDirectoryNotEmptyException;
import io.github.exception.vFSDiskFullException;
import io.github.exception.vFSFileNotFoundException;
import io.github.filesystemnative.Helper;
import io.github.filesystemnative.vFileSystem;
import io.github.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.DataFormatException;

public class Demonstration {
    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("user.home"));
        File file = new File("/home/snehal/ifs.bin");
        file.delete();

        FileSystemConfiguration config = new FileSystemConfiguration(4, 16, 16, "/home/snehal/ifs.bin", "abe");
        vFileSystem fileSystem = new vFileSystem(config);

        for (File f : new File("/home/snehal/Desktop/git/ifs_data1").listFiles()) {
            if (f.isDirectory())
                continue;
            //System.out.println(f.getPath());
            byte[] data = Files.readAllBytes(Paths.get(f.getPath()));
            try {
                fileSystem.createFile(data, f.getPath().substring(f.getPath().lastIndexOf('/')));
            } catch (vFSDiskFullException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n\nFileSystem after adding files");
        for (String s : fileSystem.listAllFiles()) {
            System.out.println(s);
        }

        //fileSystem.deleteFile("/TextFile.txt");
        //fileSystem.deleteFile("/text.txt");
        fileSystem.deleteFile("/index.html");

        System.out.println("\n\nAfter Deleting");
        for (String s : fileSystem.listAllFiles()) {
            System.out.println(s);
        }

        System.out.println("\n\nDIRECTORIES");
        fileSystem.createDirectory("/Snehal_Dir", "/Snehal_Dir");
        for(String s : fileSystem.listAllDirectories())
        {
            System.out.println(s);
        }

        System.out.println("ADDING FILES TO ABOVE DIRECTORY");
        for (File f : new File("/home/snehal/Desktop/git/ifs_data1").listFiles()) {
            if (f.isDirectory())
                continue;
            //System.out.println(f.getPath());
            byte[] data = Files.readAllBytes(Paths.get(f.getPath()));
            try {
                fileSystem.createFile(data, "/Snehal_Dir" +
                        f.getPath().substring(f.getPath().lastIndexOf('/')));
            } catch (vFSDiskFullException e) {
                System.out.println(e.getMessage());
            }
        }

        for (String s : fileSystem.listAllFiles()) {
            System.out.println("** : "+s);
        }

        System.out.println("\n\nDelete Directory");
        try {
            fileSystem.deleteDirectory("/Snehal_Dir");
        } catch (vFSDirectoryNotEmptyException e) {
            System.out.println(e.getMessage());
        } catch (vFSFileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        fileSystem.deleteFile("/Snehal_Dir/TextFile.txt");
        fileSystem.deleteFile("/Snehal_Dir/restricted.txt");
        fileSystem.deleteFile("/Snehal_Dir/index.html");

        System.out.println("After deleting files individually");



        for (String s : fileSystem.listAllFiles()) {
            System.out.println("** : "+s);
        }

        try {
            fileSystem.deleteDirectory("/Snehal_Dir");
        } catch (vFSDirectoryNotEmptyException e) {
            System.out.println(e.getMessage());
        } catch (vFSFileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("\nList of directories");
        for(String s : fileSystem.listAllDirectories())
        {
            System.out.println("*** : "+s);
        }
        try {
            byte[] readFile = fileSystem.readFile("/TextFile.txt");
            System.out.println(new String(readFile));
        } catch (DataFormatException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n\n\nfinish");
        fileSystem.finishFileSystem();
        Helper.saveFileSystemState(fileSystem);
        //System.out.println("fin");
        Logger.getInstance().Finish();

        FileInputStream file1 = new FileInputStream("temp.bin");
        ObjectInputStream in = new ObjectInputStream(file1);
        vFileSystem fs2 = (vFileSystem) in.readObject();
        for (String s : fs2.listAllFiles()) {
            System.out.println("existing ** : "+s);
        }

    }
}
