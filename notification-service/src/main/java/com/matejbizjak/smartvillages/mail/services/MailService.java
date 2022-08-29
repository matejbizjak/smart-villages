package com.matejbizjak.smartvillages.mail.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamListener;
import com.kumuluz.ee.nats.jetstream.util.JetStreamMessage;
import com.matejbizjak.smartvillages.central.lib.v1.TotalUserEnergyInterval;
import com.matejbizjak.smartvillages.mail.config.EmailProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;

@ApplicationScoped
public class MailService {

    @Inject
    private EmailProperties config;

    private final Logger LOG = LogManager.getLogger(MailService.class.getSimpleName());

    private void sendEmail(String to, String subject, String body) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", config.getHost());
        properties.put("mail.smtp.port", config.getPort());
        properties.put("mail.smtp.ssl.trust", config.getHost());

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(body, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JetStreamListener(connection = "secure", subject = "mail.dailyUsage", queue = "mail")
    public void receiveDailyEnergyUsageData(TotalUserEnergyInterval data, JetStreamMessage msg) {
        // TODO create graphs from data and add them to the mail body
        BigDecimal energyWh = data.getSum().divide(new BigDecimal(3600), 4, RoundingMode.HALF_UP);
//        sendEmail(
//                data.getUser().getEmail()
//                , "Daily energy report"
//                , data.getSum().compareTo(new BigDecimal(0)) > 0 ?
//                        "Great! Yesterday you produced more energy that you have spend. " + String.format("The amount is %s kWh.", energyWh)) :
//                        "Yesterday you have spend more energy that you have produced. \n" + String.format("The amount is %s kWh.", energyWh);

        sendDummyMail(data.getUser().getEmail(), energyWh);
    }

    private void sendDummyMail(String email, BigDecimal energyKwh) {
        LOG.info(String.format("Sending an email to %s with subject %s and the following content: \n%s%s"
                        , email
                        , "Daily energy report"
                        , energyKwh.compareTo(new BigDecimal(0)) > 0 ?
                        "Great! Yesterday you produced more energy that you have spend. " :
                        "Yesterday you have spend more energy that you have produced. "
                        , String.format("The amount is %s Wh.", energyKwh.toString())
                )
        );
    }
}
