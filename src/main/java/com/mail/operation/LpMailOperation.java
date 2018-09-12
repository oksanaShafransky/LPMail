package com.mail.operation;

import com.mail.com.mail.utils.LpMailBodyUtil;
import com.mail.com.mail.utils.LpMailStatus;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by oksanas on 09/05/18.
 */
public class LpMailOperation implements Callable<LpMailStatus> {
    private static Logger logger = LoggerFactory.getLogger(LpMailOperation.class);
    private LpMail lpMail;

    private int retries = 0;
    private static int MAX_NUMBER_OF_RETRIES = 3;

    public LpMailOperation(LpMail lpMail) {
        this.lpMail = lpMail;
    }

    public LpMailStatus call() throws Exception {
        return sendMail();
    }

    private LpMailStatus sendMail() throws Exception {
        try {
            System.out.println("Sending new mail...");
            send(lpMail);
            return LpMailStatus.SUCCESS;
        } catch(Exception e) {
            throw new Exception("Exception on send mail", e);
        }
    }

    private void send(LpMail mail) throws Exception {

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
        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(mail.getRecipientEmail(), false));
        msg.setSubject(mail.getSubject());
        msg.setSentDate(new Date());

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


        Transport.send(msg);

        logger.info("LpMailBodyUtilMessage: Successfully sent e-mail from " +
                mail.getSenderEmail() + " to " + mail.getRecipientEmail() + " for site " + mail.getSite());

    }

    public boolean ifGetMaxNumberOfRetries() {
        return retries == MAX_NUMBER_OF_RETRIES;
    }

    public LpMail getLpMail() {
        return lpMail;
    }

    public int getRetries() {
        return retries;
    }

    public void incrementRetries() {
        retries = retries + 1;
    }
}
