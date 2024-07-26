package com.prunny.auth.service;

import com.prunny.auth.dto.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendHtmlEmail(EmailDetails emailDetails) throws MessagingException;
     void sendEmailWithThymeleaf(EmailDetails emailDetails);
    void sendEmails(EmailDetails emailDetails);

}
