package com.library.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupUtil {

    private static final String BACKUP_DIR = "backup/";
  

    static {
        try {
            Files.createDirectories(Paths.get(BACKUP_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void backupFile(String srcPath) {
        File src = new File(srcPath);
        if (!src.exists()) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String time = sdf.format(new Date());
        String destPath = BACKUP_DIR + src.getName() + "_" + time;

        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(destPath)) {

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
