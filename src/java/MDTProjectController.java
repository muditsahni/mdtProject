/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 *
 * @author Mudit
 */
@WebServlet(urlPatterns = {"/MDTProjectController/*"})
public class MDTProjectController extends HttpServlet {
MDTProjectModel m =  null;
int count =1;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
           
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String uid = (String)request.getParameter("userid");
        Cookie myCookie =
                        new Cookie("userid", uid);
                        response.addCookie(myCookie);
        
        InetAddress IP= getIPAddress();
                //InetAddress.getLocalHost();
        Cookie ipAdd =
                        new Cookie("ip", IP.toString().split("/")[1]);
                        response.addCookie(ipAdd);
        
        System.out.println(IP.toString().split("/")[1]);
        request.getSession().setAttribute("userid", uid);
        String clientOrigin = request.getHeader("origin");
        response.setHeader("Access-Control-Allow-Origin", "*");
        if (request.getParameter("view") == null)
        {
            m = new MDTProjectModel(uid);
            count = 1;
           // response.addHeader("Access-Control-Allow-Origin", "*");
            RequestDispatcher view = request.getRequestDispatcher("home.html");
            view.forward(request, response);
        
        }
        else
        {
            String view = (String)request.getParameter("view");
            
            PrintWriter out = response.getWriter();
            switch (view)
            {
                case "livefeed" : 
                    String str = System.currentTimeMillis()+","+m.getHeartRateLive(uid).get(0);
                    out.write(str);
                    
                   //System.out.println((m.getHeartRateLive(uid).get(0)).toString());
                   if (m.getHeartRateLive(uid).get(0) > m.getPatient(uid).maxHeartRate )
                   {
                       if (count == 3)
                       {
                           
                                Thread t = new Thread( new Runnable(){
                                public void run(){
                                  m.getPatient(uid).sendNotifications(NotificationMessage.Severity.Emergency);
                                   }
                            });
                            t.start();
                       }
                        count++;
                        System.out.println(count);
                        System.out.println(m.getPatient(uid).maxHeartRate);
                   }

                    m.getHeartRateLive(uid).add(m.getHeartRateLive(uid).remove(0));

                   break;
                case "weekly" :  
                    JSONArray arr = new JSONArray();
                    arr =  (JSONArray)(m.getHeartRateWeekly(uid)).get("activities-heart-intraday");

                    StringBuilder sb = new StringBuilder();
                    for (int i=0; i< arr.size(); i++)
                    {
                        JSONObject obj = (JSONObject)arr.get(i);
                        String dateStr = (String)obj.get("date");

                       JSONArray arr1 = (JSONArray) obj.get("dataset");

                       for (int j=0; j<arr1.size(); j++)
                       {
                           String time_dt ="";
                           String time = (String)((JSONObject)arr1.get(j)).get("time");
                           long value = (long)((JSONObject)arr1.get(j)).get("value");

                           time_dt =  dateStr+" " + time;
                           SimpleDateFormat dt = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
                        Date date;
                            try {
                                date = dt.parse(time_dt);

                        // *** same for the format String below
                        SimpleDateFormat dt1 = null;
                        if (j==0)
                        {
                            dt1 = new SimpleDateFormat("dd MMM");
                        } else
                        {
                            dt1 = new SimpleDateFormat("HH:mm");
                        }
                        String dtm = dt1.format(date);

                        sb.append(dtm).append("&").append(value);
                        if (!(i == arr.size()-1 && j==arr1.size()-1) )
                            sb.append("@");


                           } catch (ParseException ex) {
                                Logger.getLogger(MDTProjectController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                       }
                    }
                    out.write(sb.toString()); break;
                    
                case "monthly" : //out.write((m.getHeartRateMonthly(uid)).toString()); break;
                    JSONArray arr2 = new JSONArray();
                    arr2 =  (JSONArray)(m.getHeartRateMonthly(uid)).get("activities-heart-intraday");

                    StringBuilder sb1 = new StringBuilder();
                    String dtm = "";
                    long avgVal = 0;
                    for (int i=0; i< arr2.size(); i++)
                    {
                        JSONObject obj = (JSONObject)arr2.get(i);
                        String dateStr = (String)obj.get("date");
                        JSONArray arr3 = (JSONArray) obj.get("dataset");
                        long value = 0;
                        for (int j=0; j<arr3.size(); j++)
                        {
                            String time_dt ="";
                            String time = (String)((JSONObject)arr3.get(j)).get("time");
                            value += (long)((JSONObject)arr3.get(j)).get("value");
                            time_dt =  dateStr+" " + time;
                            SimpleDateFormat dt = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
                            Date date;
                            try {
                                date = dt.parse(time_dt);
                                // *** same for the format String below
                                SimpleDateFormat dt1 = new SimpleDateFormat("E, dd MMM ");
                                dtm = dt1.format(date);
                           } catch (ParseException ex) {
                                Logger.getLogger(MDTProjectController.class.getName()).log(Level.SEVERE, null, ex);
                           }
                       }
                        avgVal = value/arr3.size();
                        sb1.append(dtm).append("&").append(avgVal);
                        if (!(i == arr2.size()-1) ){
                            sb1.append("@");
                        }
                    }
                    System.out.println(sb1.toString());
                    out.write(sb1.toString()); break;
                
                case "baseline" : 
                    JSONObject base = (JSONObject)m.getHeartRateBaseLine(uid).get("activities-heart");
                    StringBuilder baseLine = new StringBuilder();
                                
                    JSONObject det = (JSONObject) base.get("heartRateZones");
                                 
                    baseLine.append((long)det.get("max")).append("@").append((long)det.get("min")).append("@")
                                .append((long)det.get("avg")).append("@").append((long)det.get("dev"));
                                  
                    System.out.println(baseLine.toString());
                    out.write(baseLine.toString());
                    break;
                        
                case "nominees" :  
                    StringBuilder nominees = new StringBuilder();
                    Iterator it = m.getPatient(uid).familyMail.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        String username = (String)pair.getKey();
                        String email = (String)pair.getValue();
                        String tel = m.getPatient(uid).familyTel.get(username);
                        nominees.append(username).append("#").append(email).append("#").append(tel);
                        if(it.hasNext() != false){
                            nominees.append("&");
                        }

                    }
                    System.out.println(nominees.toString());
                    out.write(nominees.toString());
                    
                    break;
                    
                case "sub" : response.sendRedirect(request.getContextPath()+"/subscriberhome.html?userid="+uid);break;
            }
            
            out.flush();
        }
        
       // String uid = (String)request.getParameter("userid");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       // processRequest(request, response);
            String uid = (String)request.getParameter("userid");
            
            if (request.getParameter("name") == null )
            {
                try {
                    m = new MDTProjectModel(uid);
                    
                    response.addHeader("Access-Control-Allow-Origin", "*");
                    
                    String dir = "C:\\Users\\Mudit\\Documents\\NetBeansProjects\\MdtProject";
                    JSONParser parser = new JSONParser();
                    Object obj1 = parser.parse(new FileReader(
                            dir+ "\\User01_login.txt"));
                    JSONObject userJSON =  (JSONObject)((JSONObject)obj1).get("user-account");
                    //String userName = (String)userJSON.get("user");
                    String pwd = (String)request.getParameter("password");
                    String pass = (String)userJSON.get("password");
                    String nextView = "";
                            
                    if (pwd.equals(pass))
                    {
                        Cookie myCookie =
                        new Cookie("userid", uid);
                        response.addCookie(myCookie);
                        //nextView = "home.html?userid="+uid;
                        response.sendRedirect(request.getRequestURL()+"?userid="+uid);
                        
                       // HttpSession session = request.getSession();
                        //String username = (String)request.getAttribute("un");
                        //session.setAttribute("userid", uid);
                    }
                        
                        
                    else
                    {
                        request.setAttribute("validation", "false");
                        
                        //nextView = "index.jsp?validation=false";
                        response.sendRedirect(request.getContextPath()+"/index.html?validation=false");
                        //request.("validation", "false");
                        
                        //view.forward(request, response);
                    }
                        
                         
                   //
                } catch (org.json.simple.parser.ParseException ex) {
                    Logger.getLogger(MDTProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                StringBuilder nominee = new StringBuilder();
                String name = request.getParameter("name");
                String mailId = request.getParameter("emailid");
                String telno = request.getParameter("telno");
                m.patients.get(uid).familyMail.put(name, mailId);
                m.patients.get(uid).familyTel.put(name, telno);
                nominee.append(name).append("#").append(mailId).append("#").append(telno);
                response.setStatus(200);
                PrintWriter out = response.getWriter();
                out.write(nominee.toString());
                out.flush();
            }
            
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    public InetAddress getIPAddress() throws SocketException {
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
