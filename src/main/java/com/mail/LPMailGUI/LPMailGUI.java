package com.mail.LPMailGUI;

import com.mail.dao.SaveMailsDao;
import com.mail.operation.LpMailHandler;
import com.mail.operation.LpMailOperation;
import com.mail.structure.LpMail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class LPMailGUI extends JPanel implements ActionListener {
    private JButton closeButton;
    private JButton sendMail;
    private JButton verifyMail;
    private JFormattedTextField sendTo;
    private static JFrame frame;
    private static LpMailHandler lpMailHandler;


    public void actionPerformed(ActionEvent e) {
        if ("close".equals(e.getActionCommand())) {
            closeButton.setEnabled(false);
            frame.dispose();
            lpMailHandler.interrupt();
            System.exit(0);
        } if("send".equals(e.getActionCommand())) {
            String filename1 = "/Users/oksanas/Documents/LPMail/src/test/java/resources/pdf1.pdf";
            LpMail mail1= new LpMail("site12345", "gui_sender@sender.com",
                    sendTo.getText().trim(),
                    "gui_filename", filename1, filename1);
            lpMailHandler.addToMailQueue(new LpMailOperation(mail1));
        } if("verify".equals(e.getActionCommand())) {
            LpMail mail1= new LpMail("site12345", "gui_sender@sender.com",
                    sendTo.getText().trim(), "gui_filename", "");
            SaveMailsDao.getInstance().verifyDBEntryExists(mail1);
        }
    }

    public LPMailGUI() {
        ImageIcon sendIcon  = createImageIcon("/Users/oksanas/Documents/LPMail/src/main/java/com/mail/LPMailGUI/icons/sendMail.png");
        sendMail = new JButton("send", sendIcon);
        sendMail.setVerticalTextPosition(AbstractButton.CENTER);
        sendMail.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        sendMail.setMnemonic(KeyEvent.VK_S);
        sendMail.setActionCommand("send");
        sendMail.setToolTipText("Click this button to send a mail.");
        sendMail.addActionListener(this);
        add(sendMail);

        sendTo = new JFormattedTextField();
        sendTo.setValue("oksanas@liveperson.com");
        sendTo.setColumns(20);
        add(sendTo);

        verifyMail = new JButton("Verify");
        verifyMail.setVerticalTextPosition(AbstractButton.BOTTOM);
        verifyMail.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        verifyMail.setActionCommand("verify");
        verifyMail.setToolTipText("Click this button to verify the mail");
        verifyMail.addActionListener(this);
        add(verifyMail);

        ImageIcon closeIcon  = createImageIcon("/Users/oksanas/Documents/LPMail/src/main/java/com/mail/LPMailGUI/icons/closeIcon.jpeg");
        closeButton = new JButton("Close", closeIcon);
        closeButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        closeButton.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        closeButton.setActionCommand("close");
        closeButton.setToolTipText("Click this button to disable the middle button.");
        closeButton.addActionListener(this);
        add(closeButton);


    }

    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = LPMailGUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static void createAndShowGUI() {
        lpMailHandler = LpMailHandler.getInstance("smtp.gmail.com");//"tlvfortimail.tlv.lpnet.com");

        //Create and set up the window.
        frame = new JFrame("LPMailDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        LPMailGUI newContentPanel = new LPMailGUI();
        newContentPanel.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPanel);
        frame.setBounds(0, 0, 50, 50);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

}
