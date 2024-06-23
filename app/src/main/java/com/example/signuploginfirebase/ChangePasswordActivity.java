package com.example.signuploginfirebase;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPassword, newPassword, confirmNewPassword;
    private Button changePasswordButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmNewPassword = findViewById(R.id.confirm_new_password);
        changePasswordButton = findViewById(R.id.change_password_button);
        auth = FirebaseAuth.getInstance();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPass = currentPassword.getText().toString().trim();
                String newPass = newPassword.getText().toString().trim();
                String confirmNewPass = confirmNewPassword.getText().toString().trim();

                if (TextUtils.isEmpty(currentPass)) {
                    currentPassword.setError("Mật khẩu hiện tại không được để trống");
                    return;
                }
                if (TextUtils.isEmpty(newPass)) {
                    newPassword.setError("Mật khẩu mới không được để trống");
                    return;
                }
                if (!newPass.equals(confirmNewPass)) {
                    confirmNewPassword.setError("Mật khẩu mới và xác nhận mật khẩu không khớp");
                    return;
                }

                FirebaseUser user = auth.getCurrentUser();
                if (user != null && user.getEmail() != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                            finish(); // Quay trở lại màn hình trước
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Xác thực thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
