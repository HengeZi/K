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
import com.example.k.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etConfirmPassword, etName, etEmail, etPhone;
    private Button btnRegister;
    private TextView tvLogin;
    private AppDatabase database;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "请填写必填项", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "密码长度至少为6位", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            boolean isTaken = database.userDao().isUsernameTaken(username);
            runOnUiThread(() -> {
                if (isTaken) {
                    Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
                } else {
                    saveUser(username, password, name, email, phone);
                }
            });
        });
    }

    private void saveUser(String username, String password, String name, String email, String phone) {
        executor.execute(() -> {
            User user = new User(username, password, name, email, phone);
            long userId = database.userDao().insert(user);
            runOnUiThread(() -> {
                if (userId > 0) {
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                    
                    SessionManager sessionManager = new SessionManager(this);
                    sessionManager.setLoggedIn(true, (int) userId, username);
                    
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
