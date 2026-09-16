package com.library.util;

import java.io.*;
import java.util.List;

public class BackupUtil {

    // 保留原有旧接口 File 参数！！给MainFrame继续调用，不能删掉
    public static void backup(File file, List<?> list) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        }
    }

    // ✅新增重载：String路径版本，供后续新业务使用，不修改旧契约
    public static void backup(String path, List<?> list) throws IOException {
        backup(new File(path), list);
    }

    // 保留原有旧接口 File 参数
    @SuppressWarnings("unchecked")
    public static <T> List<T> restore(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        }
    }

    // ✅新增重载：String路径版本
    @SuppressWarnings("unchecked")
    public static <T> List<T> restore(String path) throws IOException, ClassNotFoundException {
        return restore(new File(path));
    }
}
