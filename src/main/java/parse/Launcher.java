package parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Launcher {

  public static void main(String[] args) throws IOException {

    String pathResConll = "C://Users//Ivan//Desktop//webAnnoProcessing//res.conll";
    File[] filesTsv =
        getFilesFromFolder("C://Users//Ivan//Desktop//webAnnoProcessing//tsv", ".tsv");
    String pathConllFolder = "C://Users//Ivan//Desktop//webAnnoProcessing//conll//";

    for (File fileTsv : filesTsv) {
      String filenameTsv = fileTsv.getName();
      String filenameConll = filenameTsv.substring(0, filenameTsv.length() - 4) + ".conll";

      List<String> listTsv = readFile(fileTsv.getAbsolutePath(), 7);
      List<String> listConll = readFile(pathConllFolder + filenameConll, 0);

      if (listTsv.size() == listConll.size()) {
        List<String> sentence = parseSentence(listTsv, listConll);
        WriteToEndFile(pathResConll, sentence);
      } else {
        System.out.println("ОШИБКА - коллекции разного размера " + filenameTsv);
      }
    }
  }

  public static List<String> parseSentence(List<String> listTsv, List<String> listConll) {
    List<String> sentence = new ArrayList<String>();
    for (int i = 0; i < listTsv.size(); i++) {
      sentence.add(parseLine(listTsv.get(i), listConll.get(i)));
    }
    sentence.add("\n");

    return sentence;
  }

  public static String parseLine(String lineTsv, String lineConll) {
    String[] arrTsv = lineTsv.split("\t");
    String[] arrConll = lineConll.split("\t");

    StringBuilder sb = new StringBuilder();

    sb.append(arrConll[0]).append("\t").append(arrConll[1]).append("\t"); // ID -> FORM
    sb.append(arrTsv[4]).append("\t").append(arrTsv[3]).append("\t").append(arrTsv[3]).append("\t"); // LEMMA,
                                                                                                     // UPOSTAG,
                                                                                                     // XPOSTAG
    sb.append(arrConll[5]).append("\t"); // FEATS

    // arrTsv[5].split("|");
    if (!arrTsv[5].equals("root")) {
      sb.append(arrTsv[7].replaceAll("1-", "")).append("\t").append(arrTsv[5]).append("\t"); // HEAD,
                                                                                             // DEPREL
    } else {
      sb.append(0).append("\t").append(arrTsv[5]).append("\t");
    }
    sb.append(arrConll[8]).append("\t").append(arrConll[9]);// DEPS, MISC

    return sb.toString();
  }

  private static File[] getFilesFromFolder(String folderName, final String type) {
    File folder = new File(folderName);
    return folder.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(type);
      }
    });
  }

  public static void WriteToEndFile(String path, Collection<String> list) {
    Writer writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), "utf-8"));
      for (String line : list) {
        writer.write(line + "\n");
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

  public static List<String> readFile(String path, int countScip) throws IOException {
    List<String> list = new ArrayList<String>();
    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
      int i = 0;
      for (String line; i < countScip && (line = br.readLine()) != null; i++) {
      }
      for (String line; (line = br.readLine()) != null; i++) {
        if (!line.isEmpty()) {
          list.add(line);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return list;
  }

}
