
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mudit
 */
public class MDTProjectModel {
   
    HashMap<String, Patient> patients = new HashMap<String, Patient>();
      
    MDTProjectModel(String id)
    {
        Patient patient = new Patient(id);
        // Read file
         JSONParser parser = new JSONParser();
              
         
        try {
            String dir = "C:\\Users\\Mudit\\Documents\\NetBeansProjects\\MdtProject";
            Object obj = parser.parse(new FileReader(
                    dir+"\\User01_04252016_2s_1900_1902.txt"));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            JSONArray dataSet = (JSONArray) ((JSONObject)jsonObject.get("activities-heart-intraday")).get("dataset");
            //String author = (String) jsonObject.get("Author");
           // JSONArray companyList = (JSONArray) jsonObject.get("activities-heart-intraday").get("dataset");
            
            
            for (int i=0; i< dataSet.size(); i++)
            {
                patient.heartRateLive.add(((Long)((JSONObject)dataSet.get(i)).get("value")));
            }
            Object obj1 = parser.parse(new FileReader(
                   dir+ "\\User01_05012016_8h_M.txt"));
            
            patient.heartRateMonthly = (JSONObject) obj1;
           
            Object obj2 = parser.parse(new FileReader(
                    dir + "\\User01_04252016_8h_W.txt"));
            
            patient.heartRateWeekly = (JSONObject) obj2;
            
            Object obj3 = parser.parse(new FileReader(
                    dir + "\\User01_04252016_baselinedata.txt"));
            
            patient.heartRateBaseLine = (JSONObject) obj3;
            patient.maxHeartRate = (long)((JSONObject)((JSONObject)(patient.heartRateBaseLine.get("activities-heart"))).get("heartRateZones")).get("max");
            patients.put(id, patient);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    ArrayList<Long> getHeartRateLive(String uid)
    {
        return patients.get(uid).heartRateLive;
    }
    
    JSONObject getHeartRateWeekly(String uid)
    {
        return patients.get(uid).heartRateWeekly;
    }
    
    JSONObject getHeartRateMonthly(String uid)
    {
        return patients.get(uid).heartRateMonthly;
    }
    JSONObject getHeartRateBaseLine(String uid)
    {
        return patients.get(uid).heartRateBaseLine;
    }
    
    
    
    Patient getPatient(String uid){
        return patients.get(uid);
    }
}
