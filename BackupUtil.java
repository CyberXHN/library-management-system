package com.library.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class BackupUtil {
    //备份
    public static void backup(String file, List<?> data) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(data);
        oos.close();
    }
    //恢复
    @SuppressWarnings("unchecked")
    public static List<Object> restore(String file) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        List<Object> res = (List<Object>) ois.readObject();
        ois.close();
        return res;
    }
}
