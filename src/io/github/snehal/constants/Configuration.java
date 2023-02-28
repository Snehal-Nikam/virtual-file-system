

package io.github.snehal.constants;

import io.github.snehal.utils.NativeHelperUtils;

import java.io.Serializable;

public class Configuration implements Serializable {
    int size;
    int maxDirectoryName;
    int maxFileName;
    String nativeFilepath;
    String ownerUsername;

    public Configuration() {
        // MegaBytes
        size = 64;
        maxDirectoryName = 16;
        maxFileName = 16;
        nativeFilepath = NativeHelperUtils.getUserHomeDirectory();
        ownerUsername = GlobalConstants.defaultUsername;
    }

    public Configuration(int size, int maxDirectoryName, int maxFileName, String path, String userName) {
        this.size = size;
        this.maxDirectoryName = maxDirectoryName;
        this.maxFileName = maxFileName;
        this.nativeFilepath = path;
        this.ownerUsername = userName;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getNativeFilepath() {
        return nativeFilepath;
    }

    public int getSize() {
        return size;
    }

    public int getMaxDirectoryName() {
        return maxDirectoryName;
    }

    public int getMaxFileName() {
        return maxFileName;
    }
}
