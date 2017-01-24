package com.daxiang.taojin.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.daxiang.android.http.HttpConstants.HttpMethod;
import com.daxiang.android.http.okhttp.OkHttpManager;
import com.daxiang.android.http.okhttp.OkHttpRequest;
import com.daxiang.android.http.okhttp.OkHttpResponse;
import com.daxiang.android.ui.BaseOkHttpActivity;
import com.daxiang.android.utils.BitmapUtils;
import com.daxiang.android.utils.FileUtils;
import com.daxiang.taojin.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class OkHttpRequestActivity extends BaseOkHttpActivity {

    private TextView tv_url;
    private TextView tv_result;
    private final int mRequestCode = 1000;
    private final int httpsRequestCode = 1001;
    private final int rc_post_string = 1002;
    private final int rc_post_stream = 1003;
    private final int rc_post_file = 1004;
    private final int rc_post_form = 1005;
    private final int rc_post_multipart = 1006;
    private final int rc_parse_gson = 1007;
    private final int rc_test_cache = 1008;
    private final int rc_test_cache2 = 1009;
    private final int rc_test_timeout = 1010;
    private final int rc_test_single_call = 1011;
    private final int rc_test_single_call2 = 1012;

    public static final String HTTPS_POST = "https_post";
    public static final String POST_STRING = "post_string";
    public static final String POST_STREAM = "post_stream";
    public static final String POST_FILE = "post_file";
    public static final String POST_FORM = "post_form";
    public static final String POST_MultiPart = "post_multipart";
    public static final String Parse_Gson = "parse_gson";
    public static final String Test_Cache = "test_cache";
    public static final String Test_Timeout = "test_timeout";
    public static final String Test_Single_Call = "test_single_call";

    private static MediaType MEDIA_TYPE_MARKDOWN = MediaType
            .parse("text/x-markdown; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request);

        initViews();
        try {
            getDatas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        tv_url = (TextView) findViewById(R.id.tv_url);
        tv_result = (TextView) findViewById(R.id.tv_result);
    }

    private void getDatas() throws Exception {
        String url = getIntent().getStringExtra("url");
        tv_url.setText(url);

        OkHttpRequest request = null;
        String requestMethod = getIntent().getStringExtra("method");

        if (requestMethod.equals("GET")) {
            request = new OkHttpRequest.Builder().url(url)
                    .requestCode(mRequestCode).build();

        } else if (requestMethod.equals(HTTPS_POST)) {
            // String url =
            // "https://127.0.0.1:8443/HttpsServer/loginHttpServlet";

            RequestBody formBody = new FormBody.Builder()
                    .add("userName", "daxiang").add("userPwd", "222222")
                    .build();
            request = new OkHttpRequest.Builder().url(url)
                    .requestMethod(HttpMethod.POST)
                    .requestCode(httpsRequestCode).requestBody(formBody)
                    .build();
        } else if (requestMethod.equals(POST_STRING)) {
            request = postString(url);
        } else if (requestMethod.equals(POST_STREAM)) {
            request = postStream(url);
        } else if (requestMethod.equals(POST_FILE)) {
            request = postFile(url);
        } else if (requestMethod.equals(POST_FORM)) {
            request = postForm(url);
        } else if (requestMethod.equals(POST_MultiPart)) {
            request = postMultiPart(url);
        } else if (requestMethod.equals(Parse_Gson)) {
            request = parseGson(url);
        } else if (requestMethod.equals(Test_Cache)) {
            request = testCache(url);
        } else if (requestMethod.equals(Test_Timeout)) {
            request = testTimeout(url);
        } else if (requestMethod.equals(Test_Single_Call)) {
            request = testSingleCall(url);
        }
        sendRequest(request);
    }

    private OkHttpRequest testSingleCall(String url) {
        OkHttpRequest request = new OkHttpRequest.Builder()
                // This URL is served with a 12 second delay.
                .url("http://httpbin.org/delay/12")
                .requestCode(rc_test_single_call).build();
        return request;
    }

    private OkHttpRequest testTimeout(String url) {
        // url = http://httpbin.org/delay/12 --This URL is served with a 12
        // second delay;
        return new OkHttpRequest.Builder().url(url)
                .requestCode(rc_test_timeout).build();
    }

    private OkHttpRequest testCache(String url) {

        return new OkHttpRequest.Builder().url(url).requestCode(rc_test_cache)
                .cacheControl(CacheControl.FORCE_NETWORK).build();
    }

    private OkHttpRequest parseGson(String url) {

        return new OkHttpRequest.Builder().url(url).requestCode(rc_parse_gson)
                .build();

    }

    static class Gist {
        Map<String, GistFile> files;
    }

    static class GistFile {
        String content;
    }

    private OkHttpRequest postMultiPart(String url) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);

        // Use the imgur image upload API as documented at
        // https://api.imgur.com/endpoints/image
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart(
                        "image",
                        "logo-square.png",
                        RequestBody.create(MediaType.parse("image/png"),
                                BitmapUtils.bmpToByteArray(bitmap, true)))
                .build();
        return new OkHttpRequest.Builder().url(url)
                .requestCode(rc_post_multipart).requestBody(requestBody)
                .requestMethod(HttpMethod.POST).build();
    }

    private OkHttpRequest postForm(String url) {
        RequestBody formBody = new FormBody.Builder().add("wd", "github")
                .addEncoded("oq", "github").build();
        return new OkHttpRequest.Builder().url(url).requestCode(rc_post_form)
                .requestBody(formBody).requestMethod(HttpMethod.POST).build();
    }

    private OkHttpRequest postFile(String url) {

        File file = new File(FileUtils.getExternalFileDirs(this), "README.md");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);

        return new OkHttpRequest.Builder().url(url)
                .requestMethod(HttpMethod.POST).requestCode(rc_post_file)
                .requestBody(requestBody).build();
    }

    public OkHttpRequest postStream(String url) throws Exception {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Numbers\n");
                sink.writeUtf8("-------\n");
                for (int i = 2; i <= 200; i++) {
                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
                }
            }

            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n)
                        return factor(x) + " × " + i;
                }
                return Integer.toString(n);
            }
        };

        return new OkHttpRequest.Builder().url(url)
                .requestMethod(HttpMethod.POST).requestCode(rc_post_string)
                .requestBody(requestBody).build();
    }

    private OkHttpRequest postString(String url) {
        String postBody = "" + "Releases\n" + "--------\n" + "\n"
                + " * _1.0_ May 6, 2013\n" + " * _1.1_ June 15, 2013\n"
                + " * _1.2_ August 11, 2013\n";
        RequestBody stringBody = RequestBody.create(
                MediaType.parse("text/x-markdown; charset=utf-8"), postBody);

        return new OkHttpRequest.Builder().url(url)
                .requestMethod(HttpMethod.POST).requestCode(rc_post_string)
                .requestBody(stringBody).build();
    }

    @Override
    protected void onRequestFailed(Call call, IOException e, int requestCode) {
        tv_result.setText("oh,no,出错了:\n" + e.toString());
    }

    @Override
    protected void onRequestSuccess(Call call, final OkHttpResponse response,
                                    int requestCode) {

        Toast.makeText(this, "哇哈哈，对了", Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case mRequestCode:
                tv_result.setText(response.getResponseStr());
                break;
            case httpsRequestCode:
                Headers responseHeaders = response.getHeaders();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    builder.append(responseHeaders.name(i) + ": "
                            + responseHeaders.value(i) + "\n");
                }
                builder.append(response.getResponseStr());
                System.out.println(builder.toString());
                tv_result.setText(builder.toString());

                break;

            case rc_post_string:
            case rc_post_stream:
            case rc_post_file:
            case rc_post_form:
            case rc_post_multipart:
                tv_result.setText(response.getResponseStr());
                break;

            case rc_parse_gson:
                Gson gson = new Gson();
                Gist gist = gson.fromJson(response.getResponseStr(), Gist.class);
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
                    sb.append(entry.getKey() + "\n");
                    sb.append(entry.getValue().content + "\n");
                }
                tv_result.setText(sb.toString());
                break;

            case rc_test_cache:
                this.sb.append("network response : " + response.getResponseStr()
                        + "\n");
                OkHttpRequest request = new OkHttpRequest.Builder()
                        .url(call.request().url().toString())
                        .requestCode(rc_test_cache2)
                        .cacheControl(CacheControl.FORCE_CACHE).build();
                sendRequest(request);
                break;
            case rc_test_cache2:
                this.sb.append("cache response : " + response.getResponseStr()
                        + "\n");
                tv_result.setText(this.sb.toString());
                break;

            case rc_test_timeout:
                tv_result.setText(response.getResponseStr());
                break;

            case rc_test_single_call:
                tv_result.setText(response.getResponseStr());
                // client.newBuilder()--This returns a
                // builder that shares the same connection pool, dispatcher, and
                // configuration with the original client.个性化配置。
                OkHttpClient newClient = OkHttpManager.getInstance().newBuilder()
                        .readTimeout(5, TimeUnit.SECONDS).build();
                OkHttpRequest request2 = new OkHttpRequest.Builder()
                        // This URL is served with a 6 second delay.
                        .url("http://httpbin.org/delay/6").client(newClient)
                        .requestCode(rc_test_single_call2).build();
                tv_url.setText("http://httpbin.org/delay/6");
                sendRequest(request2);
                break;
        }
    }

    StringBuilder sb = new StringBuilder();

}
