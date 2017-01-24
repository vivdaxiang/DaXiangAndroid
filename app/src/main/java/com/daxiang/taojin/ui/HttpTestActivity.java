package com.daxiang.taojin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.daxiang.taojin.R;

public class HttpTestActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_test);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.httpClientGet).setOnClickListener(this);
        findViewById(R.id.httpClientPost).setOnClickListener(this);

        findViewById(R.id.okGet).setOnClickListener(this);
        findViewById(R.id.okHttps).setOnClickListener(this);
        findViewById(R.id.okHttpPostString).setOnClickListener(this);
        findViewById(R.id.okPostStream).setOnClickListener(this);
        findViewById(R.id.okPostFile).setOnClickListener(this);
        findViewById(R.id.okPostForm).setOnClickListener(this);
        findViewById(R.id.okPostMultipart).setOnClickListener(this);
        findViewById(R.id.okGetParseGson).setOnClickListener(this);
        findViewById(R.id.testCache).setOnClickListener(this);
        findViewById(R.id.testTimeout).setOnClickListener(this);
        findViewById(R.id.testSingleCall).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.httpClientGet:
                intent = new Intent(this, HttpClientRequestActivity.class);
                intent.putExtra("url",
                        "http://api.91touba.com/api/search/report?pageSize=5&keyword=20");
                intent.putExtra("method", "GET");
                startActivity(intent);
                break;

            case R.id.httpClientPost:
                intent = new Intent(this, HttpClientRequestActivity.class);
                intent.putExtra("url", "http://en.wikipedia.org/w/index.php");
                intent.putExtra("method", "POST");
                startActivity(intent);
                break;

            case R.id.okGet:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url",
                        "http://api.91touba.com/api/search/report?pageSize=5&keyword=20");
                intent.putExtra("method", "GET");
                startActivity(intent);
                break;

            case R.id.okHttps:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url",
                        "https://192.168.1.22:8443/HttpsServer/loginHttpServlet");
                intent.putExtra("method", OkHttpRequestActivity.HTTPS_POST);
                startActivity(intent);
                break;

            case R.id.okHttpPostString:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url", "https://api.github.com/markdown/raw");
                intent.putExtra("method", OkHttpRequestActivity.POST_STRING);
                startActivity(intent);
                break;

            case R.id.okPostStream:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url", "https://api.github.com/markdown/raw");
                intent.putExtra("method", OkHttpRequestActivity.POST_STREAM);
                startActivity(intent);
                break;

            case R.id.okPostFile:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url", "https://api.github.com/markdown/raw");
                intent.putExtra("method", OkHttpRequestActivity.POST_FILE);
                startActivity(intent);
                break;

            case R.id.okPostForm:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url", "https://www.baidu.com/s");
                intent.putExtra("method", OkHttpRequestActivity.POST_FORM);
                startActivity(intent);
                break;

            case R.id.okPostMultipart:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url", "https://api.imgur.com/3/image");
                intent.putExtra("method", OkHttpRequestActivity.POST_MultiPart);
                startActivity(intent);
                break;

            case R.id.okGetParseGson:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url",
                        "https://api.github.com/gists/c2a7c39532239ff261be");
                intent.putExtra("method", OkHttpRequestActivity.Parse_Gson);
                startActivity(intent);
                break;

            case R.id.testCache:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url", "http://publicobject.com/helloworld.txt");
                intent.putExtra("method", OkHttpRequestActivity.Test_Cache);
                startActivity(intent);
                break;

            case R.id.testTimeout:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url", "http://httpbin.org/delay/12");
                intent.putExtra("method", OkHttpRequestActivity.Test_Timeout);
                startActivity(intent);
                break;

            case R.id.testSingleCall:
                intent = new Intent(this, OkHttpRequestActivity.class);
                intent.putExtra("url", "http://httpbin.org/delay/12");
                intent.putExtra("method", OkHttpRequestActivity.Test_Single_Call);
                startActivity(intent);
                break;
        }
    }
}

