package uniparthenope.healthsystemapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uniparthenope.healthsystemapp.writeinterceptingwebview.WriteHandlingWebResourceRequest;
import uniparthenope.healthsystemapp.writeinterceptingwebview.WriteHandlingWebViewClient;

import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Map<String, String> header = new HashMap<>();
    private WebView myWebView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar2);

        Intent in = getIntent();
        final String homepage = "http://34.211.204.250";
        header.put("token", in.getStringExtra("token"));
        String urlresponse = in.getStringExtra("url");

        myWebView = findViewById(R.id.web);
        myWebView.setWebViewClient(new WriteHandlingWebViewClient(myWebView){
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WriteHandlingWebResourceRequest request) {
                if(request.getUrl().toString().startsWith(homepage)){
                    try {
                        OkHttpClient httpClient = new OkHttpClient();
                        Request mRequest;
                        if(request.getUrl().toString().equals("GET")) {
                            mRequest = new Request.Builder()
                                    .url(request.getUrl().toString())
                                    .addHeader("token", Objects.requireNonNull(header.get("token")))
                                    .build();
                        }
                        else {
                            mRequest = new Request.Builder()
                                    .url(request.getUrl().toString())
                                    .addHeader("token", Objects.requireNonNull(header.get("token")))
                                    .method("POST", RequestBody.create(null, request.getAjaxData() ) )
                                    .build();
                        }
                        Response response = httpClient.newCall(mRequest).execute();
                        assert response.body() != null;
                        return new WebResourceResponse(
                                null, // set content-type
                                response.header("content-encoding", "utf-8"),
                                response.body().byteStream()
                        );
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(homepage +urlresponse, header);
    }
    @Override
    public void onBackPressed() {
        if (myWebView.isFocused() && myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setMessage("Exiting app you must log in again. Sure?")
                    .setCancelable(false)
                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Nope", null)
                    .show();
        }
    }
}

