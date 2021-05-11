package com.github.calhounvp.services;

import com.github.calhounvp.entities.BudgetPeriod;
import com.github.calhounvp.entities.SpendRequest;
import com.github.calhounvp.entities.User;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/***
 * With the use of JavaMail and resource bundles a template mail will be mailed to
 * the responsible superior.
 */
public class EmailSender {
    //__________________________________Properties__________________________________

    private final static Properties properties;
    private final static Session session;
    private final static ResourceBundle bundle;

    private final User user;

    static {
        properties = System.getProperties();
        properties.setProperty("mail.smtp.host","localhost");
        session = Session.getDefaultInstance(properties);
        bundle = ResourceBundle.getBundle("EmailResource", Locale.getDefault());
    }

    //_________________________________Constructors_________________________________
    public EmailSender (User user) {
        this.user = user;
    }

    //____________________________________Methods___________________________________
    public void sendEmail (SpendRequest request, String spendingHistoryList) {
        try {
            //prepare message
            MimeMessage message = new MimeMessage(session);

            //prepare email header
            message.setFrom(new InternetAddress(user.getUserEmail()));
            message.addRecipient(Message.RecipientType.TO,
                                new InternetAddress(user.getUserSuperiorEmail()));
            message.setSubject(String.format("Spending request for %s by %s",
                                            BudgetPeriod.getInstance().toString(),
                                            user.getUserName()));
            //prepare email content
            message.setText(MessageFormat.format(bundle.getString("email"),
                                                    String.format("%.2f", request.getRequestCost()
                                                                    .doubleValue()),
                                                    request.getRequestInfo(),
                                                    user.getAmountSpent(),
                                                    BudgetPeriod.getInstance().toString(),
                                                    spendingHistoryList,
                                                    "\n"));

            //send email and print out confirmation
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


}
