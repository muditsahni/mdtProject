

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.TwilioRestResponse;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.*;
import com.twilio.sdk.resource.list.AccountList;
import com.twilio.sdk.resource.list.AvailablePhoneNumberList;
import com.twilio.sdk.resource.list.ParticipantList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.twilio.sdk.TwilioRestException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mudit
 */
public class NotificationMessage {
    
    /** The Constant ACCOUNT_SID. Find it at twilio.com/user/account */
	public static final String ACCOUNT_SID = "AC277facbd5a51dd983c853685be23bc3b";

	/** The Constant AUTH_TOKEN. Find it at twilio.com/user/account */
	public static final String AUTH_TOKEN = "d4cc09f4fd0cd8eb3dc75179bb893e4a";
    static HashMap<Severity,String> messageMap = new HashMap<Severity,String>(); 
    
    static public enum Severity {
        Alert, Urgent, Emergency;
        
    }
    static 
    {
        try {
            InetAddress IP= getIPAddress();
            String ip = IP.toString().split("/")[1];
            messageMap.put(Severity.Alert, "Alert Message - Heart beat above baseline \n please follow the link immediately\nhttp://"+ip+":47285/MdtProject/MDTProjectController?userid=user01&view=sub");
            messageMap.put(Severity.Urgent, "Urgent Message - Heart beat increasing rapidly \n please follow the link immediately\nhttp://"+ip+":47285/MdtProject/MDTProjectController?userid=user01&view=sub");
            messageMap.put(Severity.Emergency, "Emergency Message - Heart beat in danger zone \n please follow the link immediately\nhttp://"+ip+":47285/MdtProject/MDTProjectController?userid=user01&view=sub");
        } catch (Exception ex) {
            Logger.getLogger(NotificationMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    static String getMessage(Severity s)
    {
        return messageMap.get(s);
    }
    
    static void sendMail(String toUser, Severity s)
    {
      // Recipient's email ID needs to be mentioned.
      //String to = "abhiyank@gmail.com";//change accordingly

      // Sender's email ID needs to be mentioned
      String from = "mdtprojectteam16@gmail.com";//change accordingly
      final String username = "mdtprojectteam16";//change accordingly
      final String password = "mdtProject16";//change accordingly

      // Assuming you are sending email through relay.jangosmtp.net
      String host = "smtp.gmail.com";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "587");

      // Get the Session object.
      Session session = Session.getInstance(props,
      new javax.mail.Authenticator() {
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
         }
      });

      try {
         // Create a default MimeMessage object.
          javax.mail.Message message1 = new MimeMessage(session);

         // Set From: header field of the header.
         message1.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
        message1.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toUser));

         // Set Subject: header field
         message1.setSubject("Alert Message From Patient");

         // Now set the actual message
         message1.setText(messageMap.get(s));

         // Send message
         Transport.send(message1);

         System.out.println("Sent message successfully....");

      } catch (MessagingException e) {
            throw new RuntimeException(e);
      }
    }
    
    static void sendText(String toUser, Severity s)
    {
        
            //RestExamples RE = new RestExamples();
            try{
		TwilioRestClient client1 = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

		// Get the main account (The one we used to authenticate the client)
		Account mainAccount = client1.getAccount();

		// Get all accounts including sub accounts
		AccountList accountList = client1.getAccounts();

		// All lists implement an iterable interface, you can use the foreach
		// syntax on them
		for (Account a : accountList) {
			System.out.println(a.getFriendlyName());
		}

		// Send an sms (using the new messages endpoint)
		MessageFactory messageFactory = mainAccount.getMessageFactory();
		List<NameValuePair> messageParams = new ArrayList<NameValuePair>();
                
		messageParams.add(new BasicNameValuePair("To", toUser)); // Replace with a valid phone number
		messageParams.add(new BasicNameValuePair("From", "(412) 502-5160")); // Replace with a valid phone
		// number in your account
		messageParams.add(new BasicNameValuePair("Body", messageMap.get(s)));
		messageFactory.create(messageParams);

	}
           catch(Exception ec)
           {
               ec.printStackTrace();
           }
        
    }
    public static InetAddress getIPAddress() throws SocketException {
        InetAddress ip = null;
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
        {
            if(netint.getName().equals("wlan0"))
            {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    return  inetAddress; 
                }           
                   //displayInterfaceInformation(netint);

            }
            continue;

        }
        return ip;
    }
}
