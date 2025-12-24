package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText[] otpEditTexts = new EditText[6];
    private Button btnVerifyOtp;
    private TextView tvResendOtp, tvBackToForgot, tvOtpDescription;
    private LinearLayout otpContainer;
    private String email, expectedOtp;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 120000; // 2 phút

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        expectedOtp = intent.getStringExtra("otp");

        // Kiểm tra dữ liệu đầu vào
        if (email == null || expectedOtp == null) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin xác thực", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view - CHỈ ÁNH XẠ CÁC VIEW CÓ TRONG XML
        btnVerifyOtp = findViewById(R.id.verifyOtpButton);
        tvResendOtp = findViewById(R.id.resendOtpText);
        tvBackToForgot = findViewById(R.id.textViewBackToForgot);
        tvOtpDescription = findViewById(R.id.otpDescription);
        otpContainer = findViewById(R.id.otpContainer);

        // KHÔNG TÌM timerTextView VÌ KHÔNG CÓ TRONG XML
        // tvTimer = findViewById(R.id.timerTextView); // XÓA DÒNG NÀY

        // Cập nhật mô tả với email
        tvOtpDescription.setText("Mã xác thực đã được gửi đến\n" + email);

        // Tạo các ô nhập OTP
        createOtpInputFields();

        // Ẩn nút gửi lại ban đầu
        tvResendOtp.setEnabled(false);
        tvResendOtp.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Bắt đầu đếm ngược (sẽ hiển thị bằng Toast)
        startTimer();

        // Sự kiện xác thực OTP
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

        // Sự kiện gửi lại OTP
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp();
            }
        });

        // Sự kiện quay lại màn hình quên mật khẩu
        tvBackToForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createOtpInputFields() {
        // Clear container trước khi tạo mới
        otpContainer.removeAllViews();

        // Tạo 6 EditText cho OTP
        for (int i = 0; i < 6; i++) {
            otpEditTexts[i] = new EditText(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    70, 70
            );
            params.setMargins(10, 0, 10, 0);
            otpEditTexts[i].setLayoutParams(params);
            otpEditTexts[i].setTextSize(22);
            otpEditTexts[i].setGravity(View.TEXT_ALIGNMENT_CENTER);
            otpEditTexts[i].setBackgroundResource(R.drawable.edittext_bg);
            otpEditTexts[i].setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            otpEditTexts[i].setMaxLines(1);
            otpEditTexts[i].setId(View.generateViewId());

            final int index = i;
            otpEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Tự động chuyển sang ô tiếp theo
                    if (s.length() == 1 && index < 5) {
                        otpEditTexts[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpEditTexts[index - 1].requestFocus();
                    }

                    // Tự động xác thực khi nhập đủ 6 số
                    if (getEnteredOtp().length() == 6) {
                        verifyOtp();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpContainer.addView(otpEditTexts[i]);
        }

        // Focus vào ô đầu tiên
        if (otpEditTexts[0] != null) {
            otpEditTexts[0].requestFocus();
        }
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                tvResendOtp.setEnabled(true);
                tvResendOtp.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                Toast.makeText(OtpVerificationActivity.this, "Mã OTP đã hết hạn", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        // Hiển thị thời gian còn lại trong nút resend
        String timeLeft = String.format("Gửi lại (%02d:%02d)", minutes, seconds);

        if (timeLeftInMillis > 0) {
            tvResendOtp.setText(timeLeft);
            tvResendOtp.setEnabled(false);
            tvResendOtp.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            tvResendOtp.setText("Gửi lại");
            tvResendOtp.setEnabled(true);
            tvResendOtp.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    private void verifyOtp() {
        String enteredOtp = getEnteredOtp();

        if (enteredOtp.length() < 6) {
            Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // So sánh OTP nhập với OTP mong đợi
        if (enteredOtp.equals(expectedOtp)) {
            Toast.makeText(this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();

            // Dừng timer
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            // Chuyển sang màn hình đặt lại mật khẩu
            Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
            clearOtpFields();
        }
    }

    private String getEnteredOtp() {
        StringBuilder otp = new StringBuilder();
        for (EditText editText : otpEditTexts) {
            otp.append(editText.getText().toString());
        }
        return otp.toString();
    }

    private void clearOtpFields() {
        for (EditText editText : otpEditTexts) {
            editText.setText("");
        }
        otpEditTexts[0].requestFocus();
    }

    private void resendOtp() {
        // Tạo OTP mới
        expectedOtp = generateOTP();

        // Giả lập gửi OTP mới
        Toast.makeText(this, "Mã OTP mới đã được gửi đến " + email, Toast.LENGTH_SHORT).show();

        // Reset timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = 120000;
        startTimer();

        // Xóa các ô OTP cũ
        clearOtpFields();
    }

    private String generateOTP() {
        int otpNumber = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otpNumber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}