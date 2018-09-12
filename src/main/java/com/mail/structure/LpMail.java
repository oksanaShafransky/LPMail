package com.mail.structure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by oksanas on 09/04/18.
 * The class holds the mail structure
 *
 */
public class LpMail {
    private static Logger logger = LoggerFactory.getLogger(LpMail.class);
    private String site;
    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private String attachmentName;
    private String htmlBody;

    //mail with only html body
    public LpMail(String site, String senderEmail,
                  String recipientEmail,
                  String subject, String htmlText) {
        this.site = site;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.htmlBody = htmlText;
    }

    //mail with attachment and html body
    public LpMail(String site, String senderEmail,
                  String recipientEmail,
                  String subject, String htmlText,
                  String attachmentName) {
        this.site = site;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.htmlBody = htmlText;
        this.attachmentName = attachmentName;
    }

    public String getSite() {
        return site;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

}
