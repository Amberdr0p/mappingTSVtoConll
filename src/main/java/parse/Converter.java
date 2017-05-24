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

public class Converter {
  public static void main(String[] args) throws IOException {
    File folderAnn = new File("C://Users//Ivan//Desktop//semsin//annotation");
    String folderConll = "C://Users//Ivan//Desktop//webAnnoProcessing//tsvV//";
    File[] folders = folderAnn.listFiles();
    // FileUtils.
    for (File folder : folders) {
      File[] file = folder.listFiles();
      if (file.length == 1) {
        copyFile(file[0].getAbsolutePath(),
            folderConll + folder.getName().replaceAll(".conll", ".tsv"));
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
