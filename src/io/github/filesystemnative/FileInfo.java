package io.github.filesystemnative;

import java.io.Serializable;
import java.util.LinkedList;

class FileInfo implements Comparable<FileInfo>, Serializable {
    int _iNode;
    LinkedList<Integer> _fileAllocationTable;
    String _fileName;
    String _internalPath;
    int _fileSize;

    String _lastAccessedTimeStamp;
    String _createdTimeStamp;
    String _modifiedTimeStamp;

    String md5;

    public FileInfo() {
        _fileAllocationTable = new LinkedList<>();
    }

    @Override
    public int compareTo(FileInfo o) {
        if (o == null)
            return 0;
        return this._internalPath.compareTo(o._internalPath);
    }
}