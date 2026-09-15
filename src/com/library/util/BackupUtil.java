package com.library.util;

import java.io.*;
import java.util.List;

public class BackupUtil {
    // 契约要求：backup(File file, List<?> list)
    public static void backup(File file, List<?> list) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        }
    }

    // 契约要求：restore(File file) 返回List
    @SuppressWarnings("unchecked")
    public static <T> List<T> restore(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        }
    }
}
