package com.devcomanda.demospringsecurity.services.impl;

import com.devcomanda.demospringsecurity.model.User;
import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Service
public class MailServiceImpl {

    private final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine templateEngine;

    @Value("${emails.from-user}")
    private String emailFrom;

    @Value("${emails.activation-base-url}")
    private String activationBaseUrl;

    @Autowired
    public MailServiceImpl(
        final JavaMailSender javaMailSender,
        final SpringTemplateEngine templateEngine) {

        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(final String to, final String subject,
        final String content, final boolean isMultipart, final boolean isHtml
    ) {
        this.log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(this.emailFrom);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
            this.log.debug("Sent email to User '{}'", to);
        } catch (final Exception e) {
            if (this.log.isDebugEnabled()) {
                this.log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                this.log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    @Async
    public void sendEmailFromTemplate(
        final String to, final String subject, final String templateName,
        final Map<String, Object> context
    ) {
        final Context templateContext = new Context(Locale.getDefault());
        templateContext.setVariables(context);
        final String content = this.templateEngine.process(templateName, templateContext);
        this.sendEmail(to, subject, content, false, true);
    }

    @Async
    public void sendPasswordReminderEmail(final User user, final String newPassword) {
        this.log.debug("Sending password reminder  email to '{}'", user.getEmail());
        final Map<String, Object> context = new HashMap<>(1);
        context.put("user", user);
        context.put("password", newPassword);
        this.sendEmailFromTemplate(user.getEmail(), "Reminder your password", "mails/passwordReminderEmail", context);
    }

    @Async
    public void sendActivationEmail(User user) {
        this.log.debug("Sending activation email to '{}'", user.getEmail());
        final Map<String, Object> context = new HashMap<>(2);
        context.put("user", user);
        context.put("baseUrl", this.activationBaseUrl);
        this.sendEmailFromTemplate(user.getEmail(), "Activate your Account", "mails/activationEmail", context);
    }
}
