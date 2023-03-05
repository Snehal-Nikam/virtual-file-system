package io.github.filesystemnative;


import io.github.constants.FileSystemConfiguration;
import io.github.constants.Constants;
import io.github.exception.vFSDirectoryNotEmptyException;
import io.github.exception.vFSDiskFullException;
import io.github.exception.vFSFileNotFoundException;
import io.github.utils.CompressionProvider;
import io.github.utils.EncryptionProvider;
import io.github.utils.NativeHelperUtils;
import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.zip.DataFormatException;


public class vFileSystem implements Serializable {
    private byte[] _fileSystemBuffer;
    private TreeSet<Integer> freeInodes = new TreeSet<Integer>();
    //private Logger logger = Logger.getInstance();
    private FileSystemConfiguration currentConfig;
    private TreeSet<FileInfo> _fileSystem = new TreeSet<FileInfo>();
    private TreeSet<Integer> _freeBlocks = new TreeSet<>();

    public vFileSystem(@NotNull FileSystemConfiguration config) {
        this.currentConfig = config;
        try {
            setupFileSystem(config.getNativeFilepath());
        } catch (IOException e) {
            //logger.LogError(e.getMessage());
        }
    }

    private void setupInodes() {
        for (int i = 0; i < (currentConfig.getSize() * Constants.twoPowerTenVal / Constants.fileBlockSize);
             ++i) {
            freeInodes.add(i);
        }
    }

    private void initStorageSpaceBuffer() {
        _fileSystemBuffer = new byte[currentConfig.getSize() * Constants.twoPowerTenVal
                * Constants.twoPowerTenVal];
    }

    private void setupFreeFileBlocks() {
        for (int i = 0; i < (currentConfig.getSize() * Constants.twoPowerTenVal / Constants.fileBlockSize);
             ++i) {
            _freeBlocks.add(i);
        }
    }

    private void makeFileSystem() {
        setupInodes();
        setupFreeFileBlocks();
        //logger.LogDebug("Setup Inodes and Fileblocks done");
        initStorageSpaceBuffer();
        //logger.LogDebug("Setup storage area done");
    }

    private void openExistingFileSystem(String path) throws IOException {
        byte[] _fileSystemTempBuffer = Helper.readFileSystemFromNativeFileSystem(path);

        throw new NotImplementedException();
    }

    public void finishFileSystem() {
        try {
            System.out.println("currentConfig.getNativeFilepath() : "+currentConfig.getNativeFilepath());
            Helper.writeFileSystemToNativeFileSystem(_fileSystemBuffer, currentConfig.getNativeFilepath());

        } catch (Exception e) {
            //logger.LogError(e.getMessage());
        }
    }

    private void setupFileSystem(String path) throws IOException {
        if (NativeHelperUtils.fileExists(path)) {
            openExistingFileSystem(path);
            //logger.LogError(" Not implemented");
        } else {
            //logger.LogDebug("Creating new filesystem");
            makeFileSystem();
        }
    }

    public void createFile(byte[] _buffer, String path) throws IOException, vFSDiskFullException {
        int noOfFileBlocks;
        byte[] _compressedBuffer = CompressionProvider.CompressByteArray(_buffer);
        float blocks = _compressedBuffer.length / (Constants.fileBlockSize * Constants.twoPowerTenVal);
        noOfFileBlocks = (int) Math.ceil(blocks);
        if (noOfFileBlocks == 0)
            noOfFileBlocks += 1;
        if (noOfFileBlocks > _freeBlocks.size()) {
            throw new vFSDiskFullException(" DISK SPACE FULL");
        }
        // Compress, Add the buffer to the file system buffer, setup iNodes and stuff

        int[] fileBlocks = new int[noOfFileBlocks];
        for (int i = 0; i < noOfFileBlocks; ++i) {
            fileBlocks[i] = _freeBlocks.first();
            _freeBlocks.remove(_freeBlocks.first());
        }
        for (int i = 0; i < noOfFileBlocks; ++i) {
            for (int j = 0; j < Constants.fileBlockSize * Constants.twoPowerTenVal; ++j) {
                if (j >= _compressedBuffer.length) {
                    _fileSystemBuffer[(fileBlocks[i] * Constants.fileBlockSize * Constants.twoPowerTenVal) + j]
                            = ((byte) 0);
                    continue;
                }
                _fileSystemBuffer[(fileBlocks[i] * Constants.fileBlockSize * Constants.twoPowerTenVal) + j]
                        = _compressedBuffer[(i * Constants.fileBlockSize * Constants.twoPowerTenVal) + j];
            }
        }

        int iNode = freeInodes.first();
        freeInodes.remove(freeInodes.first());
        FileInfo newFile = new FileInfo();
        newFile._iNode = iNode;
        newFile._fileSize = _compressedBuffer.length;
        for (int i = 0; i < noOfFileBlocks; ++i) {
            newFile._fileAllocationTable.add(fileBlocks[i]);
        }
        newFile._fileName = path;
        newFile._internalPath = path;
        newFile._createdTimeStamp = NativeHelperUtils.getDateOrTime(false);
        newFile._lastAccessedTimeStamp = NativeHelperUtils.getDateOrTime(false);
        newFile._modifiedTimeStamp = NativeHelperUtils.getDateOrTime(false);
        newFile.md5 = EncryptionProvider.getMD5(_compressedBuffer);
        _fileSystem.add(newFile);
    }

    public byte[] readFile(String path) throws IOException, DataFormatException {
        System.out.println("****** path ****** : "+ path);
        byte[] file;
        FileInfo f = doesFileExist(path);
        Iterator iterator = f._fileAllocationTable.iterator();
        int fileSize = f._fileSize;
        file = new byte[fileSize];
        int seekPointer = 0;
        int bytesRead = 0;
        while (iterator.hasNext()) {
            int currentBlock = (int) iterator.next();
            for (int i = 0; i < Constants.fileBlockSize * Constants.twoPowerTenVal; ++i) {
                if(bytesRead >= f._fileSize)
                {
                    continue;
                }
                file[seekPointer + i] = _fileSystemBuffer[currentBlock *
                        (Constants.fileBlockSize * Constants.twoPowerTenVal) + i];
                bytesRead++;
            }
            seekPointer = seekPointer + (Constants.fileBlockSize * Constants.twoPowerTenVal);
        }
        file = CompressionProvider.DecompressByteArray(file);
        return file;
    }

    public void createDirectory(String dirName, String path) {
        if (dirName.length() > currentConfig.getMaxDirectoryName()) {
            dirName = dirName.substring(0, currentConfig.getMaxDirectoryName() - 1);
        }
        FileInfo newDirectory = new FileInfo();
        newDirectory._iNode = freeInodes.first();
        freeInodes.remove(freeInodes.first());
        newDirectory._fileName = dirName;
        newDirectory._fileSize = -1;
        newDirectory._fileAllocationTable = null;
        newDirectory._internalPath = path;
        newDirectory._createdTimeStamp = NativeHelperUtils.getDateOrTime(false);
        newDirectory._lastAccessedTimeStamp = NativeHelperUtils.getDateOrTime(false);
        newDirectory._modifiedTimeStamp = NativeHelperUtils.getDateOrTime(false);
        _fileSystem.add(newDirectory);
    }

    public void deleteFile(String path) {
        FileInfo fileToDelete = removeFileEntryFromTable(path);
        if (fileToDelete == null) {
            //logger.LogError(" FileInfo not found: " + path);
            return;
        }
        int iNode = fileToDelete._iNode;
        freeUsedFileBlocks(fileToDelete);
        if (fileToDelete != null) {
            _fileSystem.remove(fileToDelete);
        }
        freeInodes.add(iNode);
    }

    public void deleteDirectory(String path) throws vFSDirectoryNotEmptyException, vFSFileNotFoundException {
        // @TODO Delete everything with that directoryname in path

        //path = path.substring(path.lastIndexOf('/'), path.length() - 1);

        // Does not work at this point
        // deleteAllFilesInGivenDirectory(path);

        // Get subfile count
        if(getSubFileCount(path) > 1)
        {
            throw new vFSDirectoryNotEmptyException("Directory Not Empty");
        }

        FileInfo dirToDelete = removeFileEntryFromTable(path);
        if(dirToDelete == null)
        {
            //logger.LogError("Directory not found");
            throw new vFSFileNotFoundException("Directory not found");
        }
        int iNode = dirToDelete._iNode;
        if (dirToDelete != null) {
            _fileSystem.remove(dirToDelete);
        }
        freeInodes.add(iNode);
    }

//    private void deleteAllFilesInGivenDirectory(String path) {
//        for (FileInfo f : _fileSystem) {
//            if (f._internalPath.startsWith(path)) {
//                if (f._fileSize == -1)
//                    deleteDirectory(f._internalPath);
//                else
//                    deleteFile(f._internalPath);
//            }
//        }
//    }

    private FileInfo removeFileEntryFromTable(String path) {
        FileInfo del = null;
        for (FileInfo name : _fileSystem) {
            if (name._internalPath.equals(path)) {
                del = name;
            }
        }
        return del;
    }

    private void freeUsedFileBlocks(FileInfo f) {
        Iterator iterator = f._fileAllocationTable.iterator();
        while (iterator.hasNext()) {
            _freeBlocks.add((Integer) iterator.next());
        }
    }

    private FileInfo doesFileExist(String path) {
        FileInfo file = null;
        for (FileInfo f : _fileSystem) {
            if (f._internalPath.equals(path)) {
                file = f;
            }
        }
        return file;
    }

    public String[] listAllFiles() {
        String[] files = new String[_fileSystem.size()];
        int i = 0;
        for (FileInfo f : _fileSystem) {
            if (f._fileSize == -1)
                continue;
            files[i] = f._fileName;
            i++;
        }
        return files;
    }

    public ArrayList<String> listAllDirectories() {
        ArrayList<String> files = new ArrayList<>();
        int i = 0;
        for (FileInfo f : _fileSystem) {
            if (f._fileSize == -1) {
                files.add(f._fileName);
                i++;
            }
        }
        return files;
    }

    private int getSubFileCount(String path)
    {
        int counter = 0;
        for(FileInfo f : _fileSystem)
        {
            if(f._internalPath.startsWith(path))
            {
                counter++;
            }
        }
        return counter;
    }
}
