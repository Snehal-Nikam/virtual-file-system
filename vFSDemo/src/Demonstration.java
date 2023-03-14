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
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Demonstration {
    public static void main(String[] args) throws Exception {
        //System.out.println("^^^^ : "+System.getProperty("user.home"));
        File file = new File("/home/snehal/vfs.bin");
        file.delete();

        FileSystemConfiguration config = new FileSystemConfiguration(1800, 16, 16, "/home/snehal/vfs.bin", "abe");
        vFileSystem fileSystem = new vFileSystem(config);
        long startTime = 0;
        //create
        for (File f : new File("/home/snehal/Desktop/git/vfs_data1").listFiles()) {
            if (f.isDirectory())
                continue;
            //System.out.println(f.getPath());
            startTime = System.currentTimeMillis();
            byte[] data = Files.readAllBytes(Paths.get(f.getPath()));
            try {
                fileSystem.createFile(data, f.getPath().substring(f.getPath().lastIndexOf('/')));
            } catch (vFSDiskFullException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Total time to load files: " + ((double)System.currentTimeMillis() - startTime)/1000);
        //end : create
        //Start : list files
        System.out.println("\n\nFileSystem after adding files");
        for (String s : fileSystem.listAllFiles()) {
            if(s!=null) System.out.println(s);
        }
        //end : list files

        //Start : delete file
        fileSystem.deleteFile("/index.html");
        //End : delete file

        System.out.println("\n\nAfter Deleting index.html :");
        for (String s : fileSystem.listAllFiles()) {
            if(s!=null) System.out.println(s);
        }

        //Start : Create Directory
        System.out.println("\n\nDIRECTORIES");
        fileSystem.createDirectory("/Semicoln_Dir", "/Semicoln_Dir");
        for(String s : fileSystem.listAllDirectories())
        {
            if(s!=null) System.out.println(s);
        }
        //End : Create Directory
        // Start : Add files to directory
        System.out.println("ADDING FILES TO ABOVE DIRECTORY");
        for (File f : new File("/home/snehal/Desktop/git/vfs_data1").listFiles()) {
            if (f.isDirectory())
                continue;
            //System.out.println(f.getPath());
            byte[] data = Files.readAllBytes(Paths.get(f.getPath()));
            try {
                fileSystem.createFile(data, "/Semicoln_Dir" +
                        f.getPath().substring(f.getPath().lastIndexOf('/')));
            } catch (vFSDiskFullException e) {
                System.out.println(e.getMessage());
            }
        }
        //End  : Add files
        for (String s : fileSystem.listAllFiles()) {
            if (s!=null) System.out.println("** : "+s);
        }

        //Start : Delete Directory
        System.out.println("\n\nDelete Directory");
        try {
            fileSystem.deleteDirectory("/Semicoln_Dir");
        } catch (vFSDirectoryNotEmptyException e) {
            System.out.println(e.getMessage());
        } catch (vFSFileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        fileSystem.deleteFile("/Semicoln_Dir/TextFile.txt");
        fileSystem.deleteFile("/Semicoln_Dir/restricted.txt");
        fileSystem.deleteFile("/Semicoln_Dir/index.html");

        System.out.println("After deleting files individually");



        for (String s : fileSystem.listAllFiles()) {
            if(s!=null) System.out.println(s);
        }

        try {
            fileSystem.deleteDirectory("/Semicoln_Dir");
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

        //Start : Read File
        System.out.println("\nReading text file : ");
        try {
            byte[] readFile = fileSystem.readFile("/TextFile.txt");
            System.out.println(new String(readFile));
        } catch (DataFormatException e) {
            System.out.println(e.getMessage());
        }
        //End : Read File

        //Finish File
        System.out.println("\n\n\nfinish");
        fileSystem.finishFileSystem();
        Helper.saveFileSystemState(fileSystem);
        //System.out.println("fin");
        //Logger.getInstance().Finish();
        System.out.println("\nRe-opning the same file system :: ");
        FileInputStream file1 = new FileInputStream("temp.bin");
        ObjectInputStream in = new ObjectInputStream(file1);
        vFileSystem fs2 = (vFileSystem) in.readObject();
        for (String s : fs2.listAllFiles()) {
            System.out.println("existing ** : "+s);
        }

        /*
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("Select an option:");
            System.out.println("1. Add Files to file system.");
            System.out.println("2. List All Files");
            System.out.println("3. Delete file");
            System.out.println("4. Create Directory");
            System.out.println("5. List all directories");
            System.out.println("6. Add files to directory");
            System.out.println("7. Read File");
            System.out.println("7. Delete Directory");
            System.out.println("9. Quit");

            int choice = input.nextInt();

            switch (choice) {
                case 1:
                    for (File f : new File("/home/snehal/Desktop/git/vfs_data1").listFiles()) {
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
                    break;
                case 2:
                    System.out.println("\n\nFileSystem after adding files");
                    for (String s : fileSystem.listAllFiles()) {
                        System.out.println(s);
                    }
                    break;
                case 3:
                    System.out.println("Enter file to be deleted :");
                    String path = input.nextLine();
                    fileSystem.deleteFile(path);
                    break;
                case 4:
                    System.out.println("Enter name of directory:");
                    String directoryName = input.nextLine();
                    fileSystem.createDirectory("/"+directoryName, "/"+directoryName);
                    break;
                case 5:
                    for(String s : fileSystem.listAllDirectories())
                    {
                        System.out.println(s);
                    }
                    break;
                case 6:
                    System.out.println("Enter name of directory:");
                    String dirName = input.nextLine();
                    for (File f : new File("/home/snehal/Desktop/git/vfs_data1").listFiles()) {
                        if (f.isDirectory())
                            continue;
                        //System.out.println(f.getPath());
                        byte[] data = Files.readAllBytes(Paths.get(f.getPath()));
                        try {
                            fileSystem.createFile(data, "/"+dirName +
                                    f.getPath().substring(f.getPath().lastIndexOf('/')));
                        } catch (vFSDiskFullException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case 7:
                    try {
                        System.out.println("Enter name of file:");
                        String filePath = input.nextLine();
                        byte[] readFile = fileSystem.readFile("/"+filePath);
                        System.out.println(new String(readFile));
                    } catch (DataFormatException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 8:
                    fileSystem.finishFileSystem();
                    Helper.saveFileSystemState(fileSystem);
                case 9:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }*/

    }

}
