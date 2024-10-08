package com.securenotes.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    JavaMailSender javaMailSender;

    public void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP to activate your account");
        mimeMessageHelper.setText(
        """
                <div>
                <h2>Dear Sir / Madam,</h2>
                <p>Your One Time Password(OTP) is : <strong>%s</strong></p>
                <p>Your OTP will expire in 1 min.</p>
                       <em> Warm Regards,<br />
                        Secure Notes (e-mail ID : securenotes.webapp@gmail.com) <br />
                        Visit us at <a href="https://www.securenotes.com" target="_blank">www.securenotes.com</a>
                       </em>
                </div>
                """.formatted(otp), true);
        javaMailSender.send(mimeMessage);
    }
}
