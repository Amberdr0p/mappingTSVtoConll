package parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MergeConll {

  public static void main(String[] args) throws IOException {
    File folderConll = new File("C://Users//Ivan//Desktop//Curation//curation");
    String fileConll = "C://Users//Ivan//Desktop//finish_conll//forums_and_music.conll";
    File[] folders = folderConll.listFiles();
    // FileUtils.
    for (File folder : folders) {
      File[] file = folder.listFiles();
      if (file.length == 1) {
        copyFile(file[0].getAbsolutePath(),
            fileConll);
      } else {
        System.out.println(folder.getAbsolutePath() + " " + file.length);
      }
    }
  }
  
  private static void copyFile(String file1, String file2) throws IOException {
    try (Writer writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2, true), "utf-8"))) {

      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(file1), "UTF-8"));

      for (String line; (line = br.readLine()) != null;) {
        writer.write(line + "\n");
      }
      br.close();
    }
  }
  
}
