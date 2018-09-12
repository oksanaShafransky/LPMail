package com.mail.com.mail.utils;

import com.mail.operation.LpMailHandler;
import com.mail.structure.LpMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Date;
import java.util.Properties;

/**
 * Created by oksanas on 09/05/18.
 */
public class LpMailBodyUtil {
    private static Logger logger = LoggerFactory.getLogger(LpMailBodyUtil.class);

    public static void send(LpMail mail) throws Exception {

        //String smtpHost = smtpHost;//"tlvfortimail.tlv.lpnet.com";//ServiceLocator.getInstance().getService(ServiceType.SMTP.toString(), new SmtpServerResolver());

        logger.debug("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", LpMailHandler.smtpServer.trim()); //SMTP Host
        props.put("mail.smtp.port", "25"); //TLS Port
        props.put("mail.smtp.auth", "false"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);
        //set message headers
        msg.addHeader("Content-type", "text/plain");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");

        msg.setFrom(new InternetAddress(mail.getSenderEmail()));

        //msg.setReplyTo(InternetAddress.parse(mail.getRecipientEmail()));

        msg.setSubject(mail.getSubject());

        if(mail.getAttachmentName() != null && !mail.getAttachmentName().isEmpty()) {
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mail.getHtmlBody(), "text/html");

            //add attachment
            BodyPart messageAttachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(mail.getAttachmentName());
            //ByteArrayDataSource bds = new ByteArrayDataSource(mail.getAttachment(), mail.getAttachmentName());
            messageAttachmentPart.setDataHandler(new DataHandler(source));
            messageAttachmentPart.setFileName(mail.getAttachmentName());

            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(messageAttachmentPart);
            msg.setContent(multipart);
        } else {
            msg.setContent(mail.getHtmlBody(), "text/html");
        }

        msg.setSentDate(new Date());
        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(mail.getRecipientEmail(), false));
        Transport.send(msg);

        logger.info("LpMailBodyUtilMessage: Successfully sent e-mail from " +
                mail.getSenderEmail() + " to " + mail.getRecipientEmail() + " for site " + mail.getSite());

    }

}
