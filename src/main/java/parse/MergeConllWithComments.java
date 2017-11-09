package parse;

import tagger.ProcessingFile;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

public class MergeConllWithComments {

  public static void main(String[] args) throws IOException {
    List<String> conllFile = ProcessingFile.readFile("C://Users//Ivan//Desktop//results.conll");
    List<String> txtFile = ProcessingFile.readFile("C://Users//Ivan//Desktop//0005_practice.txt");

    Writer writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C://Users//Ivan//Desktop//lab1.conll", false), "utf-8"));
      int i=1;
      boolean isAdd = true;
      for (String line : conllFile) {
        if(isAdd && txtFile.size() > (i*2-1)) {
          writer.write("# text = " + txtFile.get(i*2-2) + "\n");
          writer.write("# sent_id = " + String.valueOf(i) + "\n");
          i++;
          isAdd = false;
        }
        writer.write(line + "\n");
        if(line.isEmpty()) {
          isAdd = true;
          //writer.write("\n");
        }
        
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

}
