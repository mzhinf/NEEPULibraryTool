package neepu.edu.neepulibrarytool.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import neepu.edu.neepulibrarytool.entity.BookInfo;

/**
 * Created by 小呓的欧尼酱 on 2017/4/13.
 */

public class JSONTool {
    /**
     * 豆瓣json to BookInfo
     */
    public static BookInfo DBJsonToBookInfo(JSONObject jsonObject) {
        BookInfo bookInfo = new BookInfo();

        try {
            bookInfo.setTitle(jsonObject.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("author");
            String author = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i == 0) {
                    author = jsonArray.get(i).toString();
                } else {
                    author = author + "/" + jsonArray.get(i).toString();
                }
            }
            bookInfo.setAuthor(author);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bookInfo.setPublishing(jsonObject.getString("publisher"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bookInfo.setISBN(jsonObject.getString("isbn13"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bookInfo.setSummary(jsonObject.getString("summary"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bookInfo;
    }

}
