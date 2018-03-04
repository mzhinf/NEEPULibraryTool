package neepu.edu.neepulibrarytool.net;

import android.os.Handler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import neepu.edu.neepulibrarytool.util.ToolUtil;

/**
 * Created by 小呓的欧尼酱 on 2017/4/13.
 */

public class LibraryToolHttpUtil {

    /**
     * 豆瓣 图书Api V2
     * https://developers.douban.com/wiki/?title=book_v2
     * 根据isbn获取图书信息 https://api.douban.com//v2/book/isbn/:name
     * 例:https://api.douban.com//v2/book/isbn/:9787302153115
     * @param
     */
    public static void getBookInfoHttpUtil(Handler handler, String ISBN){
        //调用BaseHttpUtil
        //设置参数
        String urlStr = "https://api.douban.com//v2/book/isbn/:"+ISBN;
        String body = "";
        //调用BaseHttpUtil
        BaseHttpUtil.requestNetForPost(urlStr,body,handler);
    }

    /**
     *
     */
    public static void submitBookInfo(Handler handler, String str) {
        String urlStr = ToolUtil.url + "/BookInfoServlet";
        String body = null;
        try {
            body = "way=input&" + "bookInfo=" + URLEncoder.encode(str,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //调用BaseHttpUtil
        BaseHttpUtil.requestNetForPost(urlStr,body,handler);
    }

}
