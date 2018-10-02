package com.mail.operation;


import com.mail.LPMailGUI.LPMailGUI;
import com.mail.com.mail.utils.LpMailStatus;
import com.mail.dao.SaveMailsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.*;

/**
 * Created by oksanas on 09/04/18.
 * The scheduler runs every X time and empties the queue of mails
 * Mail is dequeued from the queue and runs sendMail method for each mail
 * If sending fails, it return to queue till max number of retries
 */
public class LpMailHandler extends Thread {
    private static Logger logger = LoggerFactory.getLogger(LpMailHandler.class);
    private static LpMailHandler INSTANCE = null;
    private static int QUEUE_SIZE = 10;
    private static int MAX_CONCURRENT_MAILS = 3;

    private static BlockingQueue<LpMailOperation> mailQueue;
    private ExecutorService threadPool;
    private int currentRunningMails = 0;
    private ExecutorService executorService;
    public static String smtpServer;

    //TODO: to add it to the JMX monitor
    int successMonitor = 0;
    int failureMonitor = 0;
    int fullQueueMonitor = 0;

    private LpMailHandler() {
        mailQueue = new ArrayBlockingQueue<LpMailOperation>(QUEUE_SIZE);
        setName("LpMailHandler");
    }

    public static LpMailHandler getInstance(String smtpServerName) {
        if (INSTANCE == null) {
            INSTANCE = new LpMailHandler();
            smtpServer = smtpServerName;
            INSTANCE.start();
        }
        return INSTANCE;
    }

    @Override
    public void run(){
        System.out.println("LpMailHandler is running");
        threadPool = Executors.newFixedThreadPool(MAX_CONCURRENT_MAILS);
        FutureExecutor futureExecutor = new FutureExecutor(threadPool);
        SaveMailsDao saveMailsDao = SaveMailsDao.getInstance();
        LPMailGUI.createAndShowGUI();
        try {
            //loop never ends, listening to queue
            while (true) {
                try {
                    System.out.println("Going to check is there are new mails to send...");
                    if (!mailQueue.isEmpty()) {
                        LpMailOperation mailOperation = mailQueue.take();
                        ListenableFuture<LpMailStatus> future = futureExecutor.submit(mailOperation);
                        future.addCallback(new SendMailResultHandler(mailOperation));
                    } else {
                        System.out.println("No mails, going to sleep...");
                        sleep(1000);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception happened on while loop");
                }
            }
        }
        finally {
            threadPool.shutdown();
            INSTANCE  = null;
            mailQueue = null;
        }
    }

    //add mail to queue
    public void addToMailQueue(LpMailOperation lpMailOperation) {
        boolean result = mailQueue.offer(lpMailOperation);
        if(!result) {
            fullQueueMonitor = fullQueueMonitor + 1;
            System.out.println("WARNING: The mail queue is full, the mail won't be sent");
        }
    }

    public int getQueueSize() {
        return mailQueue.size();
    }

    public class SendMailResultHandler implements FutureCallback<LpMailStatus> {

        LpMailOperation mailOperation;
        SendMailResultHandler(LpMailOperation mailOperation) {
            this.mailOperation = mailOperation;
        }

        @Override
        public void onSuccess(LpMailStatus result) {
            successMonitor = successMonitor + 1;
            System.out.println("SUCCESS: The mail was sent successfully to " + mailOperation.getLpMail().getRecipientEmail() + " from " + mailOperation.getLpMail().getSenderEmail());
            SaveMailsDao.saveToMongoDb(mailOperation.getLpMail(), LpMailStatus.SUCCESS);
        }

        @Override
        public void onFailure(Throwable failure) {
            failureMonitor = failureMonitor + 1;
            logger.info("The mail failed to be sent to " + mailOperation.getLpMail().getRecipientEmail() + " from " + mailOperation.getLpMail().getSenderEmail());
            if(!mailOperation.ifGetMaxNumberOfRetries()) {
                addToMailQueue(mailOperation);
                mailOperation.incrementRetries();
                System.out.println("FAILURE: Retrying to resent the mail for " + mailOperation.getRetries() + " times");
            } else {
                System.out.println("FAILURE: Max number of retries was reached, giving up...");
            }
            SaveMailsDao.saveToMongoDb(mailOperation.getLpMail(), LpMailStatus.SUCCESS);
            //failure.printStackTrace();
        }
    }


    //////////////////////////
    // Implementation starts here!

    public interface FutureCallback<LpMailStatus> {
        void onSuccess(LpMailStatus result);

        void onFailure(Throwable failure);
    }

    public class FutureExecutor {
        private ExecutorService executor;

        public FutureExecutor(ExecutorService executor) {
            this.executor = executor;
        }

        public <LpMailStatus> ListenableFuture<LpMailStatus> submit(final Callable<LpMailStatus> callable) {
            final ListenableFuture<LpMailStatus> future = new ListenableFuture<>();
            executor.submit(new Callable<LpMailStatus>() {
                @Override
                public LpMailStatus call() throws Exception {
                    try {
                        LpMailStatus result = callable.call();
                        future.setResult(result);
                        return result;
                    } catch (Exception e) {
                        future.setFailure(e);
                        throw e;
                    }
                }
            });

            return future;
        }
    }

    public class ListenableFuture<LpMailStatus> {
        private FutureCallback<LpMailStatus> callback;
        private LpMailStatus result;
        private Throwable failure;
        private boolean isCompleted;

        public void addCallback(FutureCallback<LpMailStatus> callback) {
            this.callback = callback;
            resolve();
        }

        public void setResult(LpMailStatus result) {
            this.result = result;
            isCompleted = true;
            resolve();
        }

        public void setFailure(Throwable failure) {
            this.failure = failure;
            isCompleted = true;
            resolve();
        }

        private void resolve() {
            if (callback != null && isCompleted) {
                if (failure == null) {
                    callback.onSuccess(result);
                } else {
                    callback.onFailure(failure);
                }
            }
        }
    }

    public int getSuccessMonitor() {
        return successMonitor;
    }

    public int getFailureMonitor() {
        return failureMonitor;
    }

    public int getFullQueueMonitor() {
        return fullQueueMonitor;
    }

}
