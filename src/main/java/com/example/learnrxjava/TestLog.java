package com.example.learnrxjava;

import android.util.Log;

import com.example.learnrxjava.logger.Settings;

public class TestLog {

    public static boolean isTest = true;
    static boolean isDebug = true;

    private static Settings settings = new Settings();

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String msg) {
        settings.getLogAdapter().e(getModule(), msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void d(String msg) {
        settings.getLogAdapter().d(getModule(), msg);
    }

    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    public static void p(String msg) {
        settings.getLogAdapter().e(getModule(), msg);
    }

    private static String getModule() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int stackOffset = -1;
        int methodCount = 2;
        for (int i = 3; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(TestLog.class.getName())) {
                stackOffset = i;
                break;
            }
        }
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = stackOffset;
            String simpleClassName = getSimpleClassName(trace[stackIndex].getClassName());
            if (simpleClassName.startsWith("TLog")) {
                continue;
            } else {
                i = 0;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(") [")
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName()).append("]");
            return builder.toString();
        }
        return "-----";
    }

//    public static AppContext context() {
//        return AppContext.getInstance();
//    }
}
