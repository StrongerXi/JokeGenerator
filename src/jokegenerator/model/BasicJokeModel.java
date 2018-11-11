package jokegenerator.model;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


@SuppressWarnings("unchecked")
public class BasicJokeModel implements JokeModel {

  private static final String END_OF_JOKE = "<s>";
  private static final String START_OF_JOKE = "</s>";


  private static class Node {

    /* counts[i] is the number of times words[i] appears after word */
    String words[];
    int counts[];
    int totalCount;

    public Node(String from, List<String> words, List<Integer> counts) {
      int wordSize = words.size();
      int countSize = counts.size();
      if (wordSize != countSize || wordSize == 0) {
        String msg = String.format("invalid input size : %s %d %d\n", from, wordSize, countSize);
        throw new IllegalArgumentException(msg);
      }

      this.words =  words.toArray(new String[countSize]);
      this.counts = new int[countSize];
      this.totalCount = 0;
      for (int i = 0; i < countSize; i += 1) {
        this.counts[i] = counts.get(i);
        this.totalCount += this.counts[i];
      }
    }

    /* randomly select a nextword based on the probabilistic
     * distribution of the frequencies */
    public String randomSelect() {
      int accumulatedFrequency = 0;
      /* threshold ∈ [1, totalCount] */
      int threshold = (int) (Math.random() * this.totalCount) + 1;
      System.out.printf("%d %d\n", totalCount, threshold);

      for (int i = 0; i < this.words.length; i += 1) {
        accumulatedFrequency += this.counts[i];
        if (accumulatedFrequency >= threshold) {
          return this.words[i];
        }
      }

      throw new RuntimeException("Unreachable code\n");
    }
  }



  private Map<String, Node> unigramNode = new HashMap<>();
  private Map<String, Node> bigramNode = new HashMap<>();


  public BasicJokeModel () {
    String jsonFile = "./data/jokes-data-processed.json";
    JSONTokener tokener;
    try {
      tokener = new JSONTokener(new BufferedReader(new FileReader(jsonFile)));
    } catch (IOException e) {
      throw new RuntimeException("cannot open file: " + jsonFile + "\n");
    }


    JSONObject rootJson = new JSONObject(tokener);
    System.out.println("data read in");

    JSONObject unigram = (JSONObject) rootJson.get("unigram");
    JSONObject bigram = (JSONObject) rootJson.get("bigram");
    System.out.println("unigram/bigram converted");


    for (String word : (Iterable<String>) unigram.keySet()) {
      /* An array of [array with only a String and Int (count)] */
      /* counts[i] is the number of times words[i] appears after word */
      List<String> words = new ArrayList<>();
      List<Integer> counts = new ArrayList<>();

      for (JSONArray wordAndCount : (Iterable<JSONArray>) unigram.get(word)) {
        String nextWord = (String) wordAndCount.get(0);
        int count = (int) wordAndCount.get(1);
        words.add(nextWord);
        counts.add(count);
      }

      unigramNode.put(word, new Node(word, words, counts));
    }

    System.out.println("unigram data processed");


    for (String pairWord : (Iterable<String>) bigram.keySet()) {
      /* An array of [array with only a String and Int (count)] */
      /* counts[i] is the number of times words[i] appears after word */
      List<String> words = new ArrayList<>();
      List<Integer> counts = new ArrayList<>();

      for (JSONArray wordAndCount : (Iterable<JSONArray>) bigram.get(pairWord)) {
        String nextWord = (String) wordAndCount.get(0);
        int count = (int) wordAndCount.get(1);
        words.add(nextWord);
        counts.add(count);
      }

      bigramNode.put(pairWord, new Node(pairWord, words, counts));
    }

    System.out.println("bigram data processed");
  }



  @Override
  public String generateJoke() {
    List<String> builtWords = new ArrayList<>();
    /* Begin pseudo word */
    String lastWord = BasicJokeModel.START_OF_JOKE;
    String lastLastWord = BasicJokeModel.START_OF_JOKE;
    int limit = 100;

    /* invariant: builtWords.size() == i */
    for (int i = 0; i < limit; i += 1) {
      Node node = unigramNode.get(lastWord);
      if (node == null) {
        throw new RuntimeException(lastWord);
      }

      /* Use bigramNode if possible */
      String pairKey = lastLastWord + " " + lastWord;
      if (bigramNode.containsKey(pairKey)) {
        node = bigramNode.get(pairKey);
      }

      String nextWord = node.randomSelect();
      if (nextWord.equals(BasicJokeModel.END_OF_JOKE)) {
        break;
      }

      /* Update the cached last words */
      lastLastWord = lastWord;
      lastWord = nextWord;

      /* Update the next word to be added */
      builtWords.add(nextWord);
    }
    
    return String.join(" ", builtWords);
  }
}
