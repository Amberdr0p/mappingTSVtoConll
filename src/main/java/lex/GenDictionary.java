package lex;

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

public class GenDictionary {

  static Map<String, List<Pair<String, String>>> dict = new HashMap<String, List<Pair<String, String>>>();

  public static void main(String[] args) throws IOException {
    String pathConll = "C://Users//Ivan//Desktop//res_ru_syntagrus-ud-train.conll";
    String pathResConll = "C://Users//Ivan//Desktop//dict1.tsv";
    List<ArrayList<String[]>> conll = readFile(pathConll);
    List<String> res = processingFile(conll);
    writeToEndFile(pathResConll, res);
  }

  public static List<String> processingFile(List<ArrayList<String[]>> conll) {
    List<String> list = new ArrayList<String>();
    for(ArrayList<String[]> arr : conll) {
      for(String[] row : arr) {
        String word = row[1];
        Pair<String,String> lemmaTag = new Pair(row[2], row[3]);
        if(dict.containsKey(word)) {
          List<Pair<String, String>> dlist = dict.get(word);
          if(!dlist.contains(lemmaTag)) {
            dlist.add(lemmaTag);
          }
        } else {
          List<Pair<String,String>> lst = new ArrayList<Pair<String,String>>();
          lst.add(lemmaTag);
          dict.put(word, lst);
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
      for(String res : list) {
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
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  public static List<ArrayList<String[]>> readFile(String path) throws IOException {
    List<ArrayList<String[]>> listConll = new ArrayList<ArrayList<String[]>>();
    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

      ArrayList<String[]> list = new ArrayList<String[]>();
      for (String line; (line = br.readLine()) != null;) {
        if (line.isEmpty() || line.indexOf("#") == 0) {
          if (!list.isEmpty()) {
            listConll.add(list);
            list = new ArrayList<String[]>();
          }
        } else {
          // String[] arrConll = line.split("\t");
          list.add(line.split("\t"));
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return listConll;
  }
}
