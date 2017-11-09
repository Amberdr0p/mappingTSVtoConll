package parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CutLargeFile {
  
  public static void main(String[] args) {
    
  }
  
  private static BufferedReader getBr(String path, int countScip) throws IOException {
    int i = 0;
    List<String> res = new ArrayList<String>();
    BufferedReader br = new BufferedReader(new FileReader(path));
    for (String line; i < countScip && (line = br.readLine()) != null; i++) {
    }
    return br;
  }
  
  private static void writeFileLarge()
      throws UnsupportedEncodingException, FileNotFoundException, IOException {
    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream("modelFullRes100.txt", true), "utf-8"))) {
      BufferedReader br = getBr("C:\\Users\\ISST\\manaslu\\eclipse-workspace\\ru_ar1380b.x1_", 0);

      // br.readLine();
      int i=0;
      int count = 100;
      for (String line; (line = br.readLine()) != null && i < count;i++ ) {
          // writer.write(line + "\n");
          System.out.println(line);
      }
    }
  }
  
}
