
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mudit
 */
public class Patient {
    String id;
    ArrayList<Long> heartRateLive = new ArrayList<Long>();
    JSONObject heartRateWeekly = new JSONObject();
    JSONObject heartRateMonthly = new JSONObject();
    JSONObject heartRateBaseLine = new JSONObject();
    HashMap<String,String> familyMail = new HashMap<String,String>();
    HashMap<String,String> familyTel = new HashMap<String,String>();
    long maxHeartRate;
    //double sdevHearRate;
    Patient(String uid){
        id =uid; 
        //familyMail.put("Abhiyank","abhiyanm@andrew.cmu.edu");
        //familyTel.put("Abhiyank", "4129564315");
        familyMail.put("Mudit","msahni@andrew.cmu.edu");
        familyTel.put("Mudit", "4129612268");
        //familyMail.put("Nitish","nmudgal@andrew.cmu.edu");
        //familyTel.put("Nitish", "4123268886");
    }
    
    void sendNotifications(NotificationMessage.Severity s)
    {
        Iterator it = familyMail.entrySet().iterator();
        while (it.hasNext()) {
            
            Map.Entry pair = (Map.Entry)it.next();
            String username = (String)pair.getKey();
            String email = (String)pair.getValue();
            String msg = NotificationMessage.getMessage(s);
            System.out.println("Sent To :" + familyTel.get(username));
            NotificationMessage.sendMail(email, s);
            NotificationMessage.sendText(familyTel.get(username), s);
            
            
        }
    }
    
    


}
