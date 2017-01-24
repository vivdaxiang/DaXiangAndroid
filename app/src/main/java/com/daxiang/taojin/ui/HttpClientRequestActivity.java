package com.daxiang.taojin.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.daxiang.android.http.httpclient.request.HttpGetRequest;
import com.daxiang.android.http.httpclient.request.HttpPostRequest;
import com.daxiang.android.http.httpclient.request.HttpRequest;
import com.daxiang.android.ui.BaseHttpClientActivity;
import com.daxiang.taojin.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class HttpClientRequestActivity extends BaseHttpClientActivity {

    private TextView tv_url;
    private TextView tv_result;
    private final int mRequestCode = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        initViews();

        getDatas();
    }

    private void getDatas() {
        String url = getIntent().getStringExtra("url");
        if (getIntent().getStringExtra("method").equals("GET")) {
            HttpRequest request = new HttpGetRequest(this);
            request.path = url;
            request.requestCode = mRequestCode;
            sendRequest(request);
        } else {
            HttpPostRequest request = new HttpPostRequest(this);
            request.path = url;
            request.requestCode = mRequestCode;
            List<NameValuePair> body = new ArrayList<NameValuePair>();
            NameValuePair pair1 = new BasicNameValuePair("search", "JurassicPark");
            body.add(pair1);
            request.bodyParams = body;
            sendRequest(request);
        }

        tv_url.setText(url);
    }

    private void initViews() {
        tv_url = (TextView) findViewById(R.id.tv_url);
        tv_result = (TextView) findViewById(R.id.tv_result);
    }

    @Override
    protected void onRequestSuccess(int requestCode, String data) {
        switch (requestCode) {
            case mRequestCode:
                tv_result.setText(data);
                break;
        }
    }

    @Override
    protected void onRequestFailed(int requestCode, String errorMessage) {
        tv_result.setText(errorMessage);
    }

}
