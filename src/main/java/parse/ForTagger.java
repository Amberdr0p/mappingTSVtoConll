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
import java.util.List;

public class ForTagger {
  
  public static void main(String[] args) throws IOException {
    String pathConll = "C://Users//Ivan//Desktop//ru_syntagrus-ud-train.conllu";
    String pathResConll = "C://Users//Ivan//Desktop//res_without_codes_tags.txt";
    
    List<ArrayList<String[]>> conll = readFile(pathConll);
    // List<String> res = processingFile(pathConll);
    writeToEndFile(pathResConll, conll);
  }

  public static void writeToEndFile(String path, List<ArrayList<String[]>> resConll) {
    Writer writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false), "utf-8"));
      for (ArrayList<String[]> conll : resConll) {
        for (String[] line : conll) {
          writer.write(line[1] + "_" + line[3] + " ");
        }
        writer.write("\n");
      }
    } catch (

    UnsupportedEncodingException e) {
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
