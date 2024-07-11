package com.prunny.auth.service;

import com.prunny.auth.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final JavaMailSender mailSender;
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Random random = new Random();

    public String generateOtp(User user) {
        String otp = String.valueOf(100000 + random.nextInt(900000)); // Generate a 6-digit OTP
        otpStorage.put(user.getEmail(), otp);
        return otp;
    }

    public void sendOtp(User user, String otp) {
        // Print OTP to console
//        System.out.println("Sending OTP to " + user.getEmail() + ": " + otp);

        // Send OTP via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
    }

    public boolean verifyOtp(User user, String otp) {
        String storedOtp = otpStorage.get(user.getEmail());
        return storedOtp != null && storedOtp.equals(otp);
    }

    public void invalidateOtp(User user) {
        otpStorage.remove(user.getEmail());
    }

    public void sendPasswordResetConfirmation(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Confirmation");
        message.setText("Your password has been reset successfully.");
        mailSender.send(message);
    }
}
