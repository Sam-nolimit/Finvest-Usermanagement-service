package com.prunny.auth.service.impl;
//import com.example.ministering_conference.utils.EmailBody;
import com.prunny.auth.dto.EmailDetails;
import com.prunny.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.*;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sendEmail;

    @Override
    public void sendHtmlEmail(EmailDetails emailDetails) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        try {
            helper.setTo(emailDetails.getRecipient());
            helper.setSubject(emailDetails.getSubject());

            // Process the Thymeleaf template with the provided model
            Context context = new Context();
            context.setVariables(emailDetails.getModel());
            String htmlContent = templateEngine.process(emailDetails.getTemplateName(), context);

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }


    public void sendEmailWithThymeleaf(EmailDetails emailDetails) {
        Context context = new Context();
        context.setVariables(emailDetails.getModel());

        String emailContent = templateEngine.process(emailDetails.getTemplateName(), context);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(sendEmail);
            helper.setTo(emailDetails.getRecipient());
            helper.setSubject(emailDetails.getSubject());
            helper.setText(emailContent, true);  // Set email content as HTML

            javaMailSender.send(mimeMessage);
            System.out.println("Email sent successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendEmails(EmailDetails emailDetails) {
        try {
            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(emailDetails.getModel());

            String emailContent = templateEngine.process(emailDetails.getTemplateName(), thymeleafContext);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(sendEmail);
            messageHelper.setTo(emailDetails.getRecipient());
            messageHelper.setSubject(emailDetails.getSubject());
            messageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);

            System.out.println("Email sent successfully.");
        } catch (TemplateInputException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process email template: " + e.getMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}