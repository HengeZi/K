package com.example.k.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.k.R;
import com.example.k.database.AppDatabase;
import com.example.k.model.User;
import com.example.k.ui.home.HomeActivity;
import com.example.k.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AppDatabase database;
    private SessionManager sessionManager;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executor = Executors.newSingleThreadExecutor();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            User user = database.userDao().validateUser(username, password);
            runOnUiThread(() -> {
                if (user != null) {
                    sessionManager.setLoggedIn(true, user.getId(), user.getUsername());
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                } else {
                    Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
