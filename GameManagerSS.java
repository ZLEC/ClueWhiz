/*
 * Back-End for Clue-less
 */
package GameBoard;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.catalina.websocket.WsOutbound;

/**
 *
 * @author ZLEC
 */
public class GameManagerSS {
   private static final int maxGames = 8;
   private static GameManagerSS gameManager;
   private final Object gamesLock;
   private static Set<Room> room;
   private static Set<Hall> hall;
   private static Set<Unknown> character;
   private static Set<Weapon> weapon;
   private Map<Long, GameSS> gameID;
   private long caseID;
   
   private GameManagerSS(){
      gamesLock = new Object();
      gameID = new HashMap<Long, GameSS>(maxGames);
      room = new HashSet<Room>();
      hall = new HashSet<Hall>();
      character = new HashSet<Unknown>();
      weapon = new HashSet<Weapon>();
      caseID = 1;
      makeDeck();
   }
   
   private void makeDeck(){
      //Characters
      character.add(new Unknown("Colonel Mustard"));
      character.add(new Unknown("Miss Scarlet"));
      character.add(new Unknown("Mr. Green"));
      character.add(new Unknown("Mrs. White"));
      character.add(new Unknown("Ms. Peacock"));
      character.add(new Unknown("Professor Plum"));
      character.add(new Unknown("Unkown"));
      
      //Weapons
      weapon.add(new Weapon("Candlestick"));
      weapon.add(new Weapon("Knife"));
      weapon.add(new Weapon("Lead Pipe"));
      weapon.add(new Weapon("Revolver"));
      weapon.add(new Weapon("Rope"));
      weapon.add(new Weapon("Wrench"));
      
      //Rooms
      createBoard();
   }
   
   private void createBoard(){
      room.add(new Room("Ballroom", new Position(0,0)));
      room.add(new Room("Billiard Room", new Position(0,0)));      
      room.add(new Room("Conservatory", new Position(0,0)));     
      room.add(new Room("Dining Room", new Position(0,0)));
      room.add(new Room("Hall", new Position(0,0)));
      room.add(new Room("Kitchen", new Position(0,0)));
      room.add(new Room("Library", new Position(0,0)));
      room.add(new Room("Lounge", new Position(0,0)));
      room.add(new Room("Study", new Position(0,0)));
 }
   
   public static synchronized GameManagerSS getInstance(){
      if(gameManager == null) gameManager = new GameManagerSS();
      return gameManager;
   }
   
   public Long createGame(String gname, String passW, String playStyle,
           String playerLang, WsOutbound playerP){
      if(passW.equals("")) passW = "Case #" + caseID;
      Player p = new Player(playerP, playerLang);
      GameSS g = new GameSS(caseID++, p.getId(), gname, passW, playStyle);
      g.addPlayer(p);
      synchronized(gamesLock){
         gameID.put((Long)g.Id, g);
      }
      return g.Id;
   }
   
   public Set<GameInfo> queryGame(String playStyle, Boolean security){
      Set<GameInfo> retVal = new HashSet<GameInfo>();
      synchronized(gamesLock){
      for(GameSS g : gameID.values()){
         if((playStyle.equals("") || g.getPlayStyle().equals(playStyle)) &&
                 (security == null || g.getPassword().equals("") != security))
            retVal.add(new GameInfo(g));
      }
      }
      
      return retVal;
   }
   
   public boolean joinGame(Long jgameID, String playerLang, WsOutbound playerP){
      boolean retVal = false;
      Player p = new Player(playerP, playerLang);
      synchronized(gamesLock){
         if(gameID.get(jgameID) != null){
            GameSS g = gameID.get(jgameID);
            gameID.remove(jgameID);
            retVal = g.addPlayer(p);
            if(retVal){
               gameID.put(jgameID, g);
            }
         }
      }
      return retVal;
   }
   
   public GameSS getGame(Long ggameid){
      GameSS g;
      synchronized(gamesLock){
         g = gameID.get(ggameid);
      }
      return g;
   }

   public static Set<Card> getRoom() {
      return new HashSet<Card>(room);
   }
   
   public static Set<Card> getHall(){
      return new HashSet<Card>(hall);
   }

   public static Set<Card> getCharacter(){
      return new HashSet<Card>(character);
   }
   
   public static Set<Card> getWeapons(){
      return new HashSet<Card>(weapon);
   }
   
   public static Set<Card> getBoard(){
      Set<Card> board = new HashSet<Card>(room);
      board.addAll(hall);
      return board;
   }
   
   public boolean leaveGame(Long lgameID, Integer playerID){
      boolean retVal = false;
      if(lgameID == null || playerID == null) return retVal;
      synchronized(gamesLock){
         GameSS g = gameID.get(lgameID);
         gameID.remove(lgameID);
         retVal = g.removePlayer(g.getPlayer(playerID));
         gameID.put(lgameID, g);
      }
      return retVal;
   }
      
      public void deleteGame(Long dgameID){
      System.out.print("Game Deleted; Remaining Game(s): ");
      synchronized(gamesLock){
         gameID.remove(dgameID);
         System.out.println(gameID.size());
      }
   }   
}
