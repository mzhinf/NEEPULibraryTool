package neepu.edu.neepulibrarytool.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

/**
 * Created by 小呓的欧尼酱 on 2017/3/12.
 */

public class ToolUtil {

    public static final int BORROWED_MAX_DAY = 30;
    //192.168.214.52
    public static String url = "http://192.168.155.1:8080/NEDULibrary";

    /**
     * 检测网络是否连接
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 判断是否有外网连接
     * 可能需要root权限
     * @ping
     */
    public static void ping(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                try {
                    String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
                    Process process = null;// ping网址3次
                    process = Runtime.getRuntime().exec("/system/bin/ping -c 3 " + ip);
                    if (process != null){
                        int status = process.waitFor();
                        if (status == 0) {
                            flag = true;
                            Log.d("ping","exec cmd success.");
                        } else {
                            Log.d("ping", status + " exec cmd fail.");
                        }
                    } else {
                        Log.d("ping","ping fail:process is null.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.obj = flag;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
