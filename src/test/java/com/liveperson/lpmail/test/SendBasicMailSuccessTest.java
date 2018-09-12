package com.liveperson.lpmail.test;

import com.mail.operation.LpMailHandler;
import com.mail.operation.LpMailOperation;
import com.mail.structure.LpMail;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by oksanas on 09/05/18.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SendBasicMailSuccessTest {
    int numberOfMailToSend = 3;
    String smtpServer = "tlvfortimail.tlv.lpnet.com";
    LpMailHandler lpMailHandler;
    LpMail mail1;

    @Before
    public void setTest() {
        lpMailHandler = LpMailHandler.getInstance(smtpServer);
        mail1= new LpMail("site12345", "sender@sender.com",
                "oksanas@livperson.com",
                "subjectLpMail", "stam stam");
    }
    @After
    public void endTest(){
        lpMailHandler.stop();
    }

    @Test
    public void Test1_sendMultipleMailsSuccess(){
        for(int i=0; i< numberOfMailToSend; i++) {
            lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
        }
        try {
            System.out.println("ACTUAL queue size is " + lpMailHandler.getQueueSize());
            assertEquals(numberOfMailToSend, lpMailHandler.getQueueSize());
            Thread.sleep(5000);
            assertEquals(0, lpMailHandler.getQueueSize());
            assertEquals(numberOfMailToSend, lpMailHandler.getSuccessMonitor());
        } catch (Exception e) {
            System.out.println("Exception on test: " + e.getMessage());
        }
    }

    //3 mails wont be sent - max queue size is reached
    @Test
    public void Test2_sendMultipleMailsMaxQueueSizeSuccess(){
        numberOfMailToSend  = 13;
        for(int i=0; i< numberOfMailToSend; i++) {
            lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
        }
        try {
            System.out.println("ACTUAL queue size is " + lpMailHandler.getQueueSize());
            assertEquals(10, lpMailHandler.getQueueSize());
            Thread.sleep(5000);
            assertEquals(0, lpMailHandler.getQueueSize());
            assertEquals(10, lpMailHandler.getSuccessMonitor());
            assertEquals(3, lpMailHandler.getFullQueueMonitor());
        } catch (Exception e) {
            System.out.println("Exception on test: " + e.getMessage());
        }
    }

    //send more mail then max queue size by 2 bulks - all will be sent
    @Test
    public void Test3_sendMultipleBulksSuccess(){
        numberOfMailToSend = 14;
        try {
            for(int i=0; i< numberOfMailToSend/2; i++) {
                lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
            }
            System.out.println("ACTUAL queue size is " + lpMailHandler.getQueueSize());
            assertEquals(numberOfMailToSend/2, lpMailHandler.getQueueSize());
            Thread.sleep(5000);
            for(int i=0; i< numberOfMailToSend/2; i++) {
                lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
            }
            assertEquals(numberOfMailToSend/2, lpMailHandler.getQueueSize());
            Thread.sleep(5000);
            assertEquals(0, lpMailHandler.getQueueSize());
            assertEquals(numberOfMailToSend, lpMailHandler.getSuccessMonitor());
        } catch (Exception e) {
            System.out.println("Exception on test: " + e.getMessage());
        }
    }

    @Test
    public void Test4_sendAttachmentsSuccess(){
        try {
            String filename1 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/pdf1.pdf";
            String filename2 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/txt1.txt";
            String filename3 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/pic1.jpg";

            LpMail mail1= new LpMail("site12345", "sender1@sender.com",
                    "oksanas@livperson.com",
                    "filename1", filename1, filename1);
            LpMail mail2= new LpMail("site12345", "sender2@sender.com",
                    "oksanas@livperson.com",
                    "filename2", filename2, filename2);
            LpMail mail3= new LpMail("site12345", "sender3@sender.com",
                    "oksanas@livperson.com",
                    "filename3", filename3, filename3);
            lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
            lpMailHandler.addToMailQueue(new LpMailOperation(mail2));
            lpMailHandler.addToMailQueue(new LpMailOperation(mail3));
            assertEquals(3, lpMailHandler.getQueueSize());
            Thread.sleep(5000);
            assertEquals(0, lpMailHandler.getQueueSize());
            assertEquals(3, lpMailHandler.getSuccessMonitor());
        } catch (Exception e) {
            System.out.println("Exception on test: " + e.getMessage());
        }
    }
}