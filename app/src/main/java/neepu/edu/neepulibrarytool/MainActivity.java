package neepu.edu.neepulibrarytool;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.json.JSONException;
import org.json.JSONObject;

import neepu.edu.neepulibrarytool.entity.BookInfo;
import neepu.edu.neepulibrarytool.net.LibraryToolHttpUtil;
import neepu.edu.neepulibrarytool.util.JSONTool;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edTitle;
    private EditText edAuthor;
    private EditText edPublishing;
    private EditText edISBN;
    private EditText edSubject;
    private EditText edSearchNumber;
    private EditText edAmount;
    private EditText edSummary;

    private Button btnScanning;
    private Button btnSubmit;
    private Button btnClear;

    private Context mContext;
    private BookInfo bookInfo;
    private String ISBN;
    private int REQUEST_CODE = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initView();     //初始化控件
        initEvent();    //初始化事件
        initData();     //初始化数据


    }

    private void initView() {
        edTitle = (EditText) findViewById(R.id.edTitle);
        edAuthor = (EditText) findViewById(R.id.edAuthor);
        edPublishing = (EditText) findViewById(R.id.edPublishing);
        edISBN = (EditText) findViewById(R.id.edISBN);
        edSubject = (EditText) findViewById(R.id.edSubject);
        edSearchNumber = (EditText) findViewById(R.id.edSearchNumber);
        edAmount = (EditText) findViewById(R.id.edAmount);
        edSummary = (EditText) findViewById(R.id.edSummary);
        btnScanning = (Button) findViewById(R.id.btnScanning);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnClear = (Button) findViewById(R.id.btnClear);
    }

    private void initEvent() {
        btnScanning.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        //初始化
        ZXingLibrary.initDisplayOpinion(this);
    }

    private void initData() {
        ISBN = "";
        //初始化 bookInfo
        bookInfo = new BookInfo();
        setData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnScanning:
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btnSubmit:
                if (checkRight()) {
                    getInfo();
                    LibraryToolHttpUtil.submitBookInfo(submitBookInfo,bookInfo.toString());
                    bookInfo = new BookInfo();
                    setData();
                }
                break;
            case R.id.btnClear:
                bookInfo = new BookInfo();
                setData();
                break;
        }
    }

    /**
     * 扫描获得ISBN
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    ISBN = bundle.getString(CodeUtils.RESULT_STRING);
                    LibraryToolHttpUtil.getBookInfoHttpUtil(getBookInfoHandler, ISBN);
                    Toast.makeText(mContext, "ISBN:" + ISBN, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(mContext, "解析失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    Handler getBookInfoHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            //Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
            if (str == null) {
                Toast.makeText(mContext, "未找到该书", Toast.LENGTH_SHORT).show();
            } else {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(str);
                    bookInfo = JSONTool.DBJsonToBookInfo(jsonObject);
                    setData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Handler submitBookInfo = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str);
                boolean flag = jsonObject.getBoolean("result");
                Toast.makeText(mContext, flag ? "图书信息插入成功" : "图书信息插入失败", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void setData() {
        edTitle.setText(bookInfo.getTitle());
        edAuthor.setText(bookInfo.getAuthor());

        if (bookInfo.getPublishing() != null && !bookInfo.getPublishing().equals("") && !bookInfo.getPublishing().contains("出版社")) {
            edPublishing.setText(bookInfo.getPublishing() + "出版社");
        } else {
            edPublishing.setText(bookInfo.getPublishing());
        }

        edISBN.setText(bookInfo.getISBN());
        edSubject.setText(bookInfo.getSubject());
        edSearchNumber.setText(bookInfo.getSearchNumber());

        String temp = bookInfo.getSummary();
        if (temp != null && temp.length() > 30) {
            char[] t = temp.toCharArray();
            temp = new String(t, 0, 27);
            temp = temp + "...";
        }

        if (bookInfo.getAmount() == 0) edAmount.setText("");
        else edAmount.setText(bookInfo.getAmount());

        edSummary.setText(temp);
    }

    private boolean checkRight() {
        boolean flag = false;
        if (edTitle.getText().toString().equals("")) {
            Toast.makeText(mContext, "书名不能为空", Toast.LENGTH_SHORT).show();
        } else if (edAuthor.getText().toString().equals("")) {
            Toast.makeText(mContext, "作者不能为空", Toast.LENGTH_SHORT).show();
        } else if (edISBN.getText().toString().equals("")) {
            Toast.makeText(mContext, "ISBN不能为空", Toast.LENGTH_SHORT).show();
        } else if (edISBN.getText().toString().length() != 11 &&
                edISBN.getText().toString().length() != 13) {
            Toast.makeText(mContext, "ISBN格式不标准(11或13位)", Toast.LENGTH_SHORT).show();
        } else if (edAmount.getText().toString().equals("")) {
            Toast.makeText(mContext, "馆藏数量不能为空", Toast.LENGTH_SHORT).show();
        } else {
            flag = true;
        }
        return flag;
    }

    private void getInfo(){
        bookInfo.setTitle(edTitle.getText().toString());
        bookInfo.setAuthor(edAuthor.getText().toString());
        bookInfo.setPublishing(edPublishing.getText().toString());
        bookInfo.setISBN(edISBN.getText().toString());
        bookInfo.setSubject(edSubject.getText().toString());
        bookInfo.setSearchNumber(edSearchNumber.getText().toString());
        bookInfo.setAmount(Integer.parseInt(edAmount.getText().toString()));
        if (bookInfo.getSummary().equals("")) {
            bookInfo.setSummary(edSummary.getText().toString());
        }
    }

}
