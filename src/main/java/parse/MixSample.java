package parse;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class MixSample {

  public static void main(String[] args) throws IOException {
    HashMap<Integer, List<String>> map = new HashMap<Integer, List<String>>(); 
    readFile("C://Users//Ivan//Desktop//res_ru_syntagrus-ud-train.conll", map);

    List<Integer> listTest = mixRandom(map.size(), 25000);

    writeFiles(map, listTest);
  }

  static List<Integer> mixRandom(int size, int count) {
    List<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < count;) {
      int rand = (int) (Math.random() * size);
      if (!list.contains(rand)) {
        list.add(rand);
        i++;
      }
    }
    return list;
  }


  private static void readFile(String file, HashMap<Integer, List<String>> map) throws IOException {
    BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    int i = 0;
    List<String> list = new ArrayList<String>();
    for (String line; (line = br.readLine()) != null;) {
      if (line.isEmpty()) {
        if (!list.isEmpty()) {
          map.put(i, list);
          list = new ArrayList<String>();
          i++;
        }
      } else {
        list.add(line);
      }
      
    }
    br.close();
  }

  private static void writeFiles(HashMap<Integer, List<String>> map, List<Integer> listTest) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    List<String> trainSample = new ArrayList<String>();
    List<String> testSample = new ArrayList<String>();
    
    for (Entry<Integer, List<String>> entry : map.entrySet())
    {
        if(listTest.contains(entry.getKey())) {
          testSample.addAll(entry.getValue());
          testSample.add("\n");
        } else {
          trainSample.addAll(entry.getValue());
          trainSample.add("\n");
        }
    }
    writeFile("C://Users//Ivan//Desktop//res_ud-SytTagRus1.conllu", trainSample);
    writeFile("C://Users//Ivan//Desktop//res_ud-SytTagRus2.conllu", testSample);
    // writeFile();
  }

  private static void writeFile(String file, List<String> lines)
      throws UnsupportedEncodingException, FileNotFoundException, IOException {
    try (Writer writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"))) {
      for (String line : lines) {
        writer.write(line + "\n");
      }
    }
  }
}
