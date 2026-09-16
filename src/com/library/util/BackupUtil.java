package com.library.util;

import java.io.*;
import java.util.List;

public class BackupUtil {
    // 契约要求：入参为文件路径String，不是File对象
    public static void backup(String path, List<?> list) throws IOException {
        File file = new File(path);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        }
    }

    // 契约要求：入参为文件路径String
    @SuppressWarnings("unchecked")
    public static <T> List<T> restore(String path) throws IOException, ClassNotFoundException {
        File file = new File(path);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        }
    }
}
