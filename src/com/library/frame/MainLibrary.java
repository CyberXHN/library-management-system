package com.library.frame;

/**
 * 程序入口（负责人：A）
 *
 * 契约约定的启动顺序：先启动 OverdueTimer 逾期检测线程（每 60 秒扫描一次），
 * 再打开登录窗口。运行前需先在本地 MySQL 执行 sql/library.sql。
 */
public class MainLibrary {

    public static void main(String[] args) {
        // TODO 集成（E）：OverdueTimer 交付后取消注释（成员 E 负责，第 3 周）
        // OverdueTimer.start();
        new LoginFrame();
    }
}
