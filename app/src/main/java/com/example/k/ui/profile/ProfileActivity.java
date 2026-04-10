package com.example.k.ui.profile;

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
import com.example.k.ui.auth.LoginActivity;
import com.example.k.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername;
    private EditText etName, etEmail, etPhone, etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnSave, btnLogout;
    private AppDatabase database;
    private SessionManager sessionManager;
    private ExecutorService executor;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executor = Executors.newSingleThreadExecutor();

        tvUsername = findViewById(R.id.tvUsername);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);

        loadUserInfo();

        btnSave.setOnClickListener(v -> saveUserInfo());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserInfo() {
        executor.execute(() -> {
            int userId = sessionManager.getUserId();
            currentUser = database.userDao().getUserById(userId);
            runOnUiThread(() -> {
                if (currentUser != null) {
                    tvUsername.setText(currentUser.getUsername());
                    etName.setText(currentUser.getName());
                    etEmail.setText(currentUser.getEmail());
                    etPhone.setText(currentUser.getPhone());
                }
            });
        });
    }

    private void saveUserInfo() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            if (!oldPassword.isEmpty() || !newPassword.isEmpty()) {
                if (!oldPassword.equals(currentUser.getPassword())) {
                    runOnUiThread(() -> Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (newPassword.length() < 6) {
                    runOnUiThread(() -> Toast.makeText(this, "新密码长度至少为6位", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    runOnUiThread(() -> Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show());
                    return;
                }
                currentUser.setPassword(newPassword);
            }

            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);

            database.userDao().update(currentUser);
            runOnUiThread(() -> {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                etOldPassword.setText("");
                etNewPassword.setText("");
                etConfirmPassword.setText("");
            });
        });
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
