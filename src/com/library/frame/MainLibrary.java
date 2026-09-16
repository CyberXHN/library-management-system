package com.library.frame;

import com.library.util.OverdueTimer;

/**
 * 程序入口（负责人：A）
 *
 * 契约约定的启动顺序：先启动 OverdueTimer 逾期检测线程（每 60 秒扫描一次），
 * 再打开登录窗口。运行前需先在本地 MySQL 执行 sql/library.sql。
 */
public class MainLibrary {

    public static void main(String[] args) {
        // 集成（E）：启动逾期检测后台线程（每 60 秒扫描一次未还记录）
        OverdueTimer.start();
        new LoginFrame();
    }
}
