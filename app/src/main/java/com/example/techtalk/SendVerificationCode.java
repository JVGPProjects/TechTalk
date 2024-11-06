package com.example.techtalk;

import android.util.Log;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendVerificationCode {

    // Generate a random 6-digit code
    public static String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    // Send email method (can use any email API, here it's using JavaMail API)
    public static void sendEmail(String toEmail) {
        String fromEmail = "your-email@example.com"; // Your sender email
        String password = "your-email-password"; // Sender email password

        // Generate a verification code
        String verificationCode = generateVerificationCode();

        // Prepare the email subject and body
        String subject = "Your Verification Code";
        String body = "Hello,\n\nYour verification code is: " + verificationCode +
                "\n\nIf you didn’t ask to verify this address, you can ignore this email." +
                "\n\nThanks,\nYour Team"; // Custom message

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body); // Set the email body with the verification code

            Transport.send(message);
            Log.d("SendVerificationCode", "Email sent successfully to " + toEmail + " with code: " + verificationCode);

        } catch (MessagingException e) {
            Log.e("SendVerificationCode", "Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
