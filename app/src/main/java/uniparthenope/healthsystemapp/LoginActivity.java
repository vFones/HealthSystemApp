package uniparthenope.healthsystemapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity{

    private EditText valUser;
    private EditText valPasswd;
    private String url;
    private String endpointresponse;
    private String token;
    private Button btnLogin;
    private ProgressBar pgsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.new_login_gradients);

        pgsBar = findViewById(R.id.progressBar);
        valUser = findViewById(R.id.USERNAME_FORM);
        valPasswd = findViewById(R.id.PASSWD_FORM);
        btnLogin = findViewById(R.id.BTNLOGIN);

        url = "http://34.211.204.250";

        AndroidNetworking.initialize(getApplicationContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = valUser.getText().toString();
                final String password = valPasswd.getText().toString();
                pgsBar.setVisibility(View.VISIBLE);
                AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
                AndroidNetworking.post(url+"/api/login")
                        .addBodyParameter("form-username", username)
                        .addBodyParameter("form-password", password)
                        .build()
                        .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                            @Override
                            public void onResponse(Response okHttpResponse, JSONObject response) {
                                try {
                                    endpointresponse = response.getString("url");
                                    token = response.getString("token");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("url", endpointresponse);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onError(ANError anError) {
                                if (anError.getErrorCode() != 200) {
                                    pgsBar.setVisibility(View.GONE);
                                    Toast toast = Toast.makeText(getApplicationContext(), "Control username and password", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        });
            }
        });
    }
}
