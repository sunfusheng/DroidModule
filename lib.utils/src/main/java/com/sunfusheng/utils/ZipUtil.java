package com.sunfusheng.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipUtil {
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
    private static final String ENCODE_UTF_8 = "UTF-8";
    private static final String DIRECTORY_LEVEL_UP = "../";

    public static boolean unZipFile(File zipFile, String folderPath) throws IOException {
        if (zipFile == null || !zipFile.exists() || TextUtils.isEmpty(folderPath)) {
            return false;
        }
        File desDir = new File(folderPath);
        mkDirs(desDir);
        InputStream in = null;
        OutputStream out = null;
        ZipFile zf = null;
        try {
            zf = new ZipFile(zipFile);
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                if (entry.getName().contains(DIRECTORY_LEVEL_UP)) {
                    return false;
                }
                in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                str = new String(str.getBytes(ENCODE_UTF_8), ENCODE_UTF_8);
                File desFile = new File(str);
                if (!desFile.exists()) {
                    if (entry.isDirectory()) {
                        mkDirs(desFile);
                    } else {
                        File pFile = desFile.getParentFile();
                        if (!pFile.exists()) {
                            mkDirs(pFile);
                        }
                    }
                }
                if (!entry.isDirectory()) {
                    out = new FileOutputStream(desFile);
                    byte buffer[] = new byte[BUFF_SIZE];
                    int realLength;
                    while ((realLength = in.read(buffer)) > 0) {
                        out.write(buffer, 0, realLength);
                    }
                    out.close();
                }
                in.close();
            }
        } catch (ZipException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (zf != null) {
                zf.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return true;
    }

    private static boolean mkDirs(File dir) {
        return dir != null && !dir.exists() && dir.mkdirs();
    }
}
