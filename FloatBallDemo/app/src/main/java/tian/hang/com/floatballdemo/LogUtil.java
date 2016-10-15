package tian.hang.com.floatballdemo;

import android.text.TextUtils;

/**
 * Created by thm on 2016/8/18.
 */
public class LogUtil {

    protected static final String TAG = "tcyapp";
    protected static final boolean SHOWLOG = true;

    private LogUtil() {
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        if (SHOWLOG)
            android.util.Log.v(TAG, buildMessage(msg));
    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void v(String msg, Throwable thr) {
        if (SHOWLOG)
            android.util.Log.v(TAG, buildMessage(msg), thr);
    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param thr An exception to log
     */
    public static void v(Throwable thr) {
        if (SHOWLOG)
            android.util.Log.v(TAG, buildMessage(""), thr);
    }

    /**
     * Send a DEBUG log message.
     *
     * @param msg
     */
    public static void d(String msg) {
        if (SHOWLOG)
            android.util.Log.d(TAG, buildMessage(msg));
    }

    /**
     * Send a DEBUG log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void d(String msg, Throwable thr) {
        if (SHOWLOG)
            android.util.Log.d(TAG, buildMessage(msg), thr);
    }

    /**
     * Send a DEBUG log message and log the exception.
     *
     * @param thr An exception to log
     */
    public static void d(Throwable thr) {
        if (SHOWLOG)
            android.util.Log.d(TAG, buildMessage(""), thr);
    }

    /**
     * Send an INFO log message.
     *
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
        if (SHOWLOG)
            android.util.Log.i(TAG, buildMessage(msg));
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void i(String msg, Throwable thr) {
        if (SHOWLOG)
            android.util.Log.i(TAG, buildMessage(msg), thr);
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param thr An exception to log
     */
    public static void i(Throwable thr) {
        if (SHOWLOG)
            android.util.Log.i(TAG, buildMessage(""), thr);
    }

    public static void zzh(String msg) {
        if (SHOWLOG)
            android.util.Log.e(TAG, "zzh: " + buildMessage(msg));
    }

    /**
     * Send an ERROR log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        if (SHOWLOG)
            android.util.Log.e(TAG, buildMessage(msg));
    }

    /**
     * Send an ERROR log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void e(String msg, Throwable thr) {
        if (SHOWLOG)
            android.util.Log.e(TAG, buildMessage(msg), thr);
    }

    /**
     * Send an ERROR log message and log the exception.
     *
     * @param thr An exception to log
     */
    public static void e(Throwable thr) {
        if (SHOWLOG)
            android.util.Log.e(TAG, buildMessage(""), thr);
    }

    /**
     * Send a WARN log message
     *
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
        if (SHOWLOG)
            android.util.Log.w(TAG, buildMessage(msg));
    }

    /**
     * Send a WARN log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void w(String msg, Throwable thr) {
        if (SHOWLOG)
            android.util.Log.w(TAG, buildMessage(msg), thr);
    }

    /**
     * Send an empty WARN log message and log the exception.
     *
     * @param thr An exception to log
     */
    public static void w(Throwable thr) {
        if (SHOWLOG)
            android.util.Log.w(TAG, buildMessage(""), thr);
    }

    /**
     * Building Message
     *
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace()
                .getStackTrace()[2];

        return new StringBuilder().append(caller.getClassName()).append(".")
                .append(caller.getMethodName()).append("(): ").append(msg)
                .toString();
    }

    public static void logTrace() {

        StackTraceElement[] caller = new Throwable().fillInStackTrace()
                .getStackTrace();
        for (int i = 0; i < caller.length; i++) {
            e(caller[i].getClassName() + ":" + caller[i].getMethodName() + ":" + caller[i].getLineNumber());
        }
    }
    public static void i(String tag , String message){
        if (SHOWLOG){
            if(!TextUtils.isEmpty(tag)){
                android.util.Log.i(tag, buildMessage(message));
            }else{
                android.util.Log.i(TAG, buildMessage(message));
            }
        }
    }
}
