import io.github.snehal.constants.Configuration;
import io.github.snehal.exception.iFSDirectoryNotEmptyException;
import io.github.snehal.exception.iFSDiskFullException;
import io.github.snehal.exception.iFSFileNotFoundException;
import io.github.snehal.filesystemnative.NativeHelper;
import io.github.snehal.filesystemnative.iFileSystem;
import io.github.snehal.logging.Logger;

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

        Configuration config = new Configuration(4, 16, 16, "/home/snehal/ifs.bin", "abe");
        iFileSystem fileSystem = new iFileSystem(config);

        for (File f : new File("/home/snehal/Desktop/git/ifs_data1").listFiles()) {
            if (f.isDirectory())
                continue;
            //System.out.println(f.getPath());
            byte[] data = Files.readAllBytes(Paths.get(f.getPath()));
            try {
                fileSystem.createFile(data, f.getPath().substring(f.getPath().lastIndexOf('/')));
            } catch (iFSDiskFullException e) {
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
            } catch (iFSDiskFullException e) {
                System.out.println(e.getMessage());
            }
        }

        for (String s : fileSystem.listAllFiles()) {
            System.out.println("** : "+s);
        }

        System.out.println("\n\nDelete Directory");
        try {
            fileSystem.deleteDirectory("/Snehal_Dir");
        } catch (iFSDirectoryNotEmptyException e) {
            System.out.println(e.getMessage());
        } catch (iFSFileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        fileSystem.deleteFile("/Snehal_Dir/TextFile.txt");
        fileSystem.deleteFile("/Snehal_Dir/restricted.txt");
        fileSystem.deleteFile("/Snehal_Dir/index.html");
//        fileSystem.deleteFile("/Snehal_Dir/drivers2.ppt");
//        fileSystem.deleteFile("/Snehal_Dir/project.pdf");
//        fileSystem.deleteFile("/Snehal_Dir/text.txt");

        System.out.println("After deleting files individually");



        for (String s : fileSystem.listAllFiles()) {
            System.out.println("** : "+s);
        }

        try {
            fileSystem.deleteDirectory("/Snehal_Dir");
        } catch (iFSDirectoryNotEmptyException e) {
            System.out.println(e.getMessage());
        } catch (iFSFileNotFoundException e) {
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
        NativeHelper.saveFileSystemState(fileSystem);
        //System.out.println("fin");
        Logger.getInstance().Finish();

        FileInputStream file1 = new FileInputStream("temp.bin");
        ObjectInputStream in = new ObjectInputStream(file1);
        iFileSystem fs2 = (iFileSystem) in.readObject();
        for (String s : fs2.listAllFiles()) {
            System.out.println("existing ** : "+s);
        }

    }
}
