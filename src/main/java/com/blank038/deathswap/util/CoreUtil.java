package com.blank038.deathswap.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CoreUtil {

    public static void outputFile(InputStream in, File file) {
        if (in == null) return;
        try {
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = in.read(b)) != -1) {
                out.write(b, 0, length);
            }
            out.close();
            in.close();
        } catch (Exception ignored) {
        }
    }
}
