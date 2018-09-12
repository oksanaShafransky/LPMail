package com.mail.operation;

import com.mail.structure.LpMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.windows.ThemeReader;


/**
 * Created by oksanas on 09/04/18.
 *
 */
public class LpMainRunProducer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(LpMainRunProducer.class);

    public static void main(String a[]) throws InterruptedException {
        LpMainRunProducer thisThread = new LpMainRunProducer();
        thisThread.run();

    }

    @Override
    public void run() {
        try {
            //consumer
            LpMailHandler lpMailHandler = LpMailHandler.getInstance("tlvfortimail.tlv.lpnet.com");

            /*LpMail mail1= new LpMail("site12345", "sender@sender.com",
                    "oksanas@liveperson.com",
                    "subjectLpMail", "stam stam");

            for(int i=0; i< 3; i++) {
                lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
            }
            Thread.sleep(5000);
            System.out.println("SUCCESS monitor = " + lpMailHandler.getSuccessMonitor());
            System.out.println("FAILURE monitor = " + lpMailHandler.getFailureMonitor());*/



            String filename1 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/pdf1.pdf";
            String filename2 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/txt1.txt";
            String filename3 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/pic1.jpg";

            LpMail mail1= new LpMail("site12345", "sender1@sender.com",
                    "oksanas@liveperson.com",
                    "filename1", filename1, filename1);
            lpMailHandler.addToMailQueue(new LpMailOperation(mail1));

            LpMail mail2= new LpMail("site12345", "sender2@sender.com",
                    "oksanas@liveperson.com",
                    "filename2", filename2, filename2);
            lpMailHandler.addToMailQueue(new LpMailOperation(mail2));

            LpMail mail3= new LpMail("site12345", "sender3@sender.com",
                    "oksanas@liveperson.com",
                    "filename3", filename3, filename3);
            lpMailHandler.addToMailQueue(new LpMailOperation(mail3));

            Thread.sleep(5000);
            System.out.println("SUCCESS monitor = " + lpMailHandler.getSuccessMonitor());
            System.out.println("FAILURE monitor = " + lpMailHandler.getFailureMonitor());

        } catch (Exception e){
            System.out.println("Exception on main thread: " + e.getMessage());
        }
    }
}
