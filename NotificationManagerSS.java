/*
 * Server Side of the Notification Manager
 */
package GameBoard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZLEC
 */

public class NotificationManagerSS {
   
   private static NotificationManagerSS notiManager;
   private Map<String, Map<NotificationEnum, String>> notifications;
   private Map<String, List<String>> playerString;
   
   private NotificationManagerSS(){
      notifications = new HashMap<String, Map<NotificationEnum, String>>();
      playerString = new HashMap<String, List<String>>();
      readNotificationDir("unknown");
      readClientStringFiles("unknown");
   }
   
   private void readNotificationDir(String directory){
      //Get files in the specific directory
      File notidir = new File(directory);
      File[] nFiles = notidir.listFiles();
      if(nFiles == null){System.out.println("Error: File not found."); return;}
      
      String[] readSS;
      BufferedReader brLine;
      EnumMap<NotificationEnum, String> readNotifications;
      
      for(File lineFile : nFiles){
         //Read language's notifications for EnumMap
         readNotifications = new EnumMap<NotificationEnum, String>(NotificationEnum.class);
         // Check the notification
         try {
            brLine = new BufferedReader(new FileReader(lineFile));
            
            while(brLine.ready()){
               //symbol '|' separate the NotificationEnum from string
               readSS = brLine.readLine().split("\\|");             
               if(readSS.length != 2){
                  String line = "";
                  for(String s : readSS) line += s;
                  throw new Exception("Invalid notification: " + line);
               }
               
               //Remove invalid or unwanted characters from the string
               readSS[1] = readSS[1].trim().replaceAll("[\",]", "");
               
               //Add notification to map
               readNotifications.put(NotificationEnum.valueOf(readSS[0].trim()), readSS[1]);
            }
            
            //Reading Completed
            notifications.put(lineFile.getName().split("\\.")[0], readNotifications);
            
         } catch (Exception ex) {
            Logger.getLogger(NotificationManagerSS.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }
}
