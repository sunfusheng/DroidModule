/**
 *
 */

package com.sunfusheng.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtil {

    public static String computeSize(long cacheSize) {
        String result = "";
        if (cacheSize == 0) {
            result = "";
        } else if (cacheSize > 0 && cacheSize < 1024) {
            result = cacheSize + "B";
        } else if (cacheSize >= 1024 && cacheSize < (1024 * 1024)) {
            result = cacheSize / 1024 + "K";
        } else if (cacheSize >= 1024 * 1024) {
            result = cacheSize / (1024 * 1024) + "M";
        }
        return result;
    }

    /**
     * 统计目录文件大小
     *
     * @param file
     * @return
     */
    public static long countFile(File file) {

        long size = 0L;
        if (file == null || !file.exists()) {
            return size;
        }

        if (file.isFile()) {
            return file.length();
        }

        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list == null) {
                return size;
            }
            for (File childFile : list) {
                size += countFile(childFile);
            }
        }

        return size;
    }

    /**
     * 删除目录下的文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file == null || !file.exists() || !file.canWrite()) {
            return false;
        }

        if (file.isFile()) {
            return file.delete();
        }

        boolean success = true;
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list == null) {
                return true;
            }
            for (File childFile : list) {
                success &= deleteFile(childFile);
            }
        }

        return success;
    }

    // Suppress default constructor for noninstantiability
    protected FileUtil() {
        throw new AssertionError();
    }

    ;

    /*
     * 保存一个Bitmap到文件系统中
     */
    public static synchronized boolean saveBitmapToFileSystem(Context context, String path,
                                                              String name,
                                                              final Bitmap bitmap) {
        // File sdcardDir =Environment.getExternalStorageDirectory();
        // 得到一个路径，内容是sdcard的文件夹路径和名字
        // String path=sdcardDir.getPath()+"/cardImages";
        FileOutputStream out = null;
        //if path is null,it mains bitmap will save in data/data/pkgname/files/
        if (path == null) {
            try {
                out = context.openFileOutput(name, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            File thumbNail = new File(path, name);
            try {
                out = new FileOutputStream(thumbNail);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            int byteCount = bitmap.getRowBytes() * bitmap.getHeight(); //--> 12才有bitmap.getByteCount();
            int quality = 100;
            int kb = 1000000;
            if (byteCount > kb) {// 超过一百万byte就压缩一下
                quality = quality * (kb / byteCount);
            }
            if (out != null && bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush();
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /*
     * 异步保存一个Bitmap到文件系统中
     */
    public static void saveBitmapToFileSystemAsync(final Context context, final String path, final String name, final Bitmap bitmap) {
        /*new Thread(new Runnable() {

			@Override
			public void run() {
				saveBitmapToFileSystem(context, path, name, bitmap);
			}
		}).start();*/
        saveBitmapToFileSystem(context, path, name, bitmap);
    }

    public static void writeFileFromStream(InputStream stream, File file)
            throws IOException {
        copyStream(stream, new FileOutputStream(file));
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int length = 0;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.flush();
        in.close();
        out.close();
    }

    /**
     * 读取源文件字符数组
     *
     * @param file 获取字符数组的文件
     * @return 字符数组
     */
    public static byte[] readFileByte(File file) {
        FileInputStream fis = null;
        FileChannel fc = null;
        byte[] data = null;
        try {
            fis = new FileInputStream(file);
            fc = fis.getChannel();
            data = new byte[(int) (fc.size())];
            fc.read(ByteBuffer.wrap(data));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return data;
    }

    public static String readFile(String path) {
        String result = null;
        InputStreamReader in = null;
        if (!new File(path).exists()) {
            return null;
        }
        try {
            in = new FileReader(path);
            StringBuffer sb = new StringBuffer();
            char[] buffer = new char[1024];
            int length = 0;
            while ((length = in.read(buffer)) > 0) {
                sb.append(buffer, 0, length);
            }
            result = sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 字符数组写入文件
     *
     * @param file 被写入的文件
     * @return 字符数组
     * @parambytes 被写入的字符数组
     */
    public static boolean writeByteFile(byte[] bytes, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                } catch (Exception e) {
                }
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return true;
    }

    static boolean mExternalStorageAvailable = false;
    static boolean mExternalStorageWriteable = false;

    private static void checkStorageState() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public static boolean isSdcardWriteable() {
        checkStorageState();
        return mExternalStorageWriteable;
    }

    public static String loadAssetsFile(Context context, String file) {

        StringBuilder buf = new StringBuilder();
        BufferedReader in = null;
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(file);
            in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return buf.toString();
    }

    // support folder and file
    public static void copyAssetsToFiles(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(context, path);
            } else {
                for (int i = 0; i < assets.length; ++i) {
                    copyAssetsToFiles(context, path + "/" + assets[i]);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private static void copyFile(Context context, String filename) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getAssets().open(filename);
            String filePath = context.getFilesDir().getAbsolutePath();
            String[] split = filename.split("/");
            for (int i = 0; i < split.length - 1; i++) {
                filePath += "/" + split[i];
                File dir = new File(filePath);
                if (!dir.exists())
                    dir.mkdir();
            }
            filePath += "/" + split[split.length - 1];
            String newFileName = filePath;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(in);
            IoUtil.close(out);
        }
    }
}
