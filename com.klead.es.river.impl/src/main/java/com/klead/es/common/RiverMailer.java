package com.klead.es.common;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by kafi on 23/02/2016.
 */

public class RiverMailer {
    private MailSender mailSender;

    private SimpleMailMessage preConfiguredSuccessMessage;
    private SimpleMailMessage preConfiguredFailMessage;



    /**
     * This method will send compose and send the message
     * */
    public void sendMail(String to, String subject, String body)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    /**
     * This method will send a pre-configured message
     * */
    public void sendPreConfiguredSuccessMail(String message)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage(preConfiguredSuccessMessage);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    /**
     * This method will send a pre-configured message
     * */
    public void sendPreConfiguredFailMail(String message)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage(preConfiguredFailMessage);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setPreConfiguredSuccessMessage(SimpleMailMessage preConfiguredSuccessMessage) {
        this.preConfiguredSuccessMessage = preConfiguredSuccessMessage;
    }

    public void setPreConfiguredFailMessage(SimpleMailMessage preConfiguredFailMessage) {
        this.preConfiguredFailMessage = preConfiguredFailMessage;
    }
}
