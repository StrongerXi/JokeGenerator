package jokegenerator.model;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class JokeModelImpl implements JokeModel {


  JokeModelImpl(String source) {

    try
    {
      ObjectMapper mapper = new ObjectMapper();

      /*
      Scanner sc = new Scanner(new FileReader(source));
      String jsonStr = sc.toString();
      Map<String, Object> jsonMap;
      */

      // convert JSON string to Map

      Map<String, Map<String, Map<String, Integer>>> g1 = mapper.readValue(new File(source),
          new TypeReference<Map<String, Map<String, Map<String, Integer>>>>(){});

      Map<String, Map<String, Integer>> unigram = g1.get("unigram");
      Map<String, Map<String, Integer>> bigram = g1.get("bigram");
      Set<Entry<String, Map<String, Integer>>> unigramSet = unigram.entrySet();
      for (Entry<String, Map<String, Integer>> e : unigramSet) {
        Map<String, Integer> frequencyList = e.getValue();
        //int sum = 0;
        Set<Entry<String, Integer>> one = frequencyList.entrySet();
        int sum = 0;
        for(Entry<String,Integer> e2: one) {
          sum += e2.getValue();
        }
        unigramInsertWord(e.getKey());
        for(Entry<String,Integer> e2: one) {
          unigramHelper(e2.getKey(), e2.getValue()/sum);
        }
      }


      //System.out.println(jsonMap);
    }
    catch(IOException ie)
    {
      ie.printStackTrace();
    }

  }

  @Override
  public String generateJoke() {
    return null;
  }

  public void unigramInsertWord(String word) {
    try {
      // The newInstance() call is a work around for some
      // broken Java implementations

      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (Exception ex) {
      // handle the error
    }
    Statement stmt = null;
    ResultSet rs = null;

    Connection conn = null;

    try {
      conn =
          DriverManager.getConnection("jdbc:mysql://localhost:3306/jokes", "root", "sesame");

    } catch (SQLException ex) {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
      throw new RuntimeException("bad");
    }

    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery("SELECT * FROM word");
      while(rs.next()) {
        System.out.print(rs.getInt("word_id"));
        System.out.println(rs.getString("word_name"));
      }
      stmt.executeUpdate("INSERT INTO word(word_name) VALUES ('five'), ('four')");
      ResultSet rs2 = stmt.executeQuery("SELECT * FROM word");
      while(rs2.next()) {
        System.out.print(rs2.getInt("word_id"));
        System.out.println(rs2.getString("word_name"));
      }
      // Now do something with the ResultSet ....
    }
    catch (SQLException ex){
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed

      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException sqlEx) { } // ignore

        rs = null;
      }

      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException sqlEx) { } // ignore

        stmt = null;
      }
    }
  }
}
