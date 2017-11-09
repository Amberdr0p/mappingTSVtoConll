package lex;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenDict2 {

  static Map<String, List<Pair<String, String>>> dict =
      new HashMap<String, List<Pair<String, String>>>();

  public static void main(String[] args) throws IOException {
    String pathFile = "C://Users//Ivan//Desktop//res_MFdev_tags.txt";
    String pathResFile = "C://Users//Ivan//Desktop//resdevMF1.txt";
    MaxentTagger tagger = new MaxentTagger("C://Users//Ivan//Desktop//russian-ud-mf.tagger");
    List<String> file = readFile(pathFile);

    List<String> res = procFile(file, tagger);
    writeToEndFile(pathResFile, res);
  }

  public static List<String> procFile(List<String> file, MaxentTagger tagger) {
    List<String> list = new ArrayList<String>();
    int count = 0;
    int countEq = 0;
    int countWithoutPunct = 0;
    int countEqWithoutPunct = 0;
    for (String raw : file) {
      StringBuffer resLine = new StringBuffer();
      String[] tokens = raw.split(" ");
      boolean isFirst = true;
      for (String token : tokens) {
        if (!isFirst) {
          resLine.append(" ");
        }
        System.out.println(token);
        String[] word = token.split("_");
        if (word.length == 2) {
          String res = tagger.tagTokenizedString(word[0]);
          if (res.trim().equals(token)) {
            if (!word[1].equals("PUNCT")) {
              countEqWithoutPunct++;
            }
            countEq++;
            System.out.println("countEq " + countEq);
          }
          if (!word[1].equals("PUNCT")) {
            countWithoutPunct++;
          }
          count++;
          resLine.append(res);
        } else {
          System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE "+token);
        }
      }
      list.add(resLine.toString() + "\n");
    }
    System.out.println(count);
    System.out.println(countEq);
    System.out.println(countEqWithoutPunct);
    System.out.println(countWithoutPunct);
    float res = countEq / count;
    float resWP = countEqWithoutPunct / countWithoutPunct;
    System.out.println(res);
    System.out.println(resWP);
    return list;
  }

  public static void loadTsv(String tsv) throws IOException {
    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(tsv), "UTF-8"));

      for (String line; (line = br.readLine()) != null;) {
        String[] ln = line.split("\t");
        Pair<String, String> lemmaTag = new Pair(ln[1], ln[2]);
        if (dict.containsKey(ln[0])) {
          List<Pair<String, String>> dlist = dict.get(ln[0]);
          if (!dlist.contains(lemmaTag)) {
            dlist.add(lemmaTag);
          }
        } else {
          List<Pair<String, String>> lst = new ArrayList<Pair<String, String>>();
          lst.add(lemmaTag);
          dict.put(ln[0], lst);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static List<String> processingFile(List<String> file, MaxentTagger tagger) {
    List<String> list = new ArrayList<String>();

    for (String row : file) {
      String[] arr = row.split(",");
      String lemma = arr[0];
      String tag = tagger.tagTokenizedString(arr[0]).split("_")[1].trim();
      Pair<String, String> lemmaTag = new Pair(lemma, tag);
      for (int i = 2; i < arr.length; i++) {
        if (dict.containsKey(arr[i])) {
          List<Pair<String, String>> dlist = dict.get(arr[i]);
          if (!dlist.contains(lemmaTag)) {
            dlist.add(lemmaTag);
          }
        } else {
          List<Pair<String, String>> lst = new ArrayList<Pair<String, String>>();
          lst.add(lemmaTag);
          dict.put(arr[i], lst);
        }
      }
    }

    for (Map.Entry<String, List<Pair<String, String>>> entry : dict.entrySet()) {
      List<Pair<String, String>> listVal = entry.getValue();
      for (Pair<String, String> val : listVal) {
        list.add(entry.getKey() + "\t" + val.first + "\t" + val.second + "\n");
      }
    }
    Collections.sort(list);
    return list;
  }

  public static void writeToEndFile(String path, List<String> list) {
    Writer writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false), "utf-8"));
      for (String res : list) {
        writer.write(res);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static List<String> readFile(String path) throws IOException {
    List<String> list = new ArrayList<String>();
    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

      for (String line; (line = br.readLine()) != null;) {
        list.add(line);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return list;
  }

}
