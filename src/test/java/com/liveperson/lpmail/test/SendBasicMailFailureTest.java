package com.liveperson.lpmail.test;

import com.mail.operation.LpMailHandler;
import com.mail.operation.LpMailOperation;
import com.mail.structure.LpMail;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

/**
 * Created by oksanas on 09/05/18.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SendBasicMailFailureTest {
    int numberOfMailToSend = 3;
    String smtpServer = null;
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
    public void Test2_sendMultipleMailsFailure() {
        LpMailHandler lpMailHandler = LpMailHandler.getInstance(null);
        for(int i=0; i< numberOfMailToSend; i++) {
            lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
        }
        try {
            System.out.println("ACTUAL queue size is " + lpMailHandler.getQueueSize());
            assertEquals(numberOfMailToSend, lpMailHandler.getQueueSize());
            Thread.sleep(5000);
            assertEquals(0, lpMailHandler.getQueueSize());
            assertEquals(12, lpMailHandler.getFailureMonitor());
        } catch (Exception e) {
            System.out.println("Exception on test: " + e.getMessage());
        }
    }

    @Test
    public void Test3_sendAttachmentsFailure(){
        try {
            String filename1 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/xxxpdf1.pdf";
            String filename2 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/xxxtxt1.txt";
            String filename3 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/xxxpic1.jpg";

            LpMail mail1= new LpMail("site12345", "sender1@sender.com",
                    "oksanas@liveperson.com",
                    "filename1", filename1, filename1);
            LpMail mail2= new LpMail("site12345", "sender2@sender.com",
                    "oksanas@liveperson.com",
                    "filename2", filename2, filename2);
            LpMail mail3= new LpMail("site12345", "sender3@sender.com",
                    "oksanas@liveperson.com",
                    "filename3", filename3, filename3);
            lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
            lpMailHandler.addToMailQueue(new LpMailOperation(mail2));
            lpMailHandler.addToMailQueue(new LpMailOperation(mail3));
            assertEquals(3, lpMailHandler.getQueueSize());
            Thread.sleep(5000);
            assertEquals(0, lpMailHandler.getQueueSize());
            assertEquals(12, lpMailHandler.getFailureMonitor());
        } catch (Exception e) {
            System.out.println("Exception on test: " + e.getMessage());
        }
    }
}