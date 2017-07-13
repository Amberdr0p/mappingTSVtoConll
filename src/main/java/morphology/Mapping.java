package morphology;

import org.apache.commons.lang3.StringUtils;

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

public class Mapping {

  private static List<String> uniqeTags = new ArrayList<String>();

  private static Map<String, List<String>> feats = new HashMap<String, List<String>>();

  private static Map<String, List<String>> featsVal = new HashMap<String, List<String>>();

  public static void main(String[] args) throws IOException {
    String pathConll = "C://Users//Ivan//Desktop//ru_syntagrus-ud-train.conllu";
    String pathResConll = "C://Users//Ivan//Desktop//res_ru_syntagrus-ud-train.conll";

    List<ArrayList<String[]>> listConll = readFile(pathConll, 0);
    List<ArrayList<String[]>> resConll = processingConveting(listConll);
    writeToEndFile(pathResConll, resConll);
  }

  public static List<ArrayList<String[]>> processingConveting(List<ArrayList<String[]>> listConll) {
    for (ArrayList<String[]> conll : listConll) {
      for (String[] row : conll) {
        String tag = row[3];
        String fs = row[5];
        if (!fs.equals("_")) {
          String[] arrfs = fs.split("\\|");
          List<String> ls = new ArrayList<String>();
          List<String> lsVal = new ArrayList<String>();
          for (String f : arrfs) {
            String[] sss = f.split("=");
            ls.add(sss[0]);
            lsVal.add(sss[1]);
          }
          if (feats.containsKey(tag)) {
            List<String> arrFeats = feats.get(tag);
            for (String f : ls) {
              if (!arrFeats.contains(f)) {
                arrFeats.add(f);
              }
            }
          } else {
            feats.put(tag, ls);
          }
          for (int i = 0; i < lsVal.size(); i++) {
            if (featsVal.containsKey(ls.get(i))) {
              List<String> lll = featsVal.get(ls.get(i));
              // int ind = lll.indexOf(lsVal.get(i));
              if (!lll.contains(lsVal.get(i))) {
                lll.add(lsVal.get(i));
                // val = lll.size();
                // } else {
                // val = ind + 1;
                // }
              }
            } else {
              List<String> lll1 = new ArrayList<String>();
              lll1.add(lsVal.get(i));
              featsVal.put(ls.get(i), lll1);
              // val = 1;
            }
          }
        } else if (!uniqeTags.contains(tag)) {
          uniqeTags.add(tag);
        }
      }
    }

    for (Map.Entry<String, List<String>> entry : feats.entrySet()) {
      Collections.sort(entry.getValue());
      System.out.println(entry.getKey() + entry.getValue());
    }
    System.out.println();
    for (Map.Entry<String, List<String>> entry : featsVal.entrySet()) {
      Collections.sort(entry.getValue());
      System.out.println(entry.getKey() + entry.getValue());
    }


    for (ArrayList<String[]> conll1 : listConll) {
      for (String[] row1 : conll1) {
        String tag1 = row1[3];
        String fs1 = row1[5];
        if (!fs1.equals("_")) {
          List<String> listF = feats.get(tag1);
          StringBuilder value = new StringBuilder(StringUtils.repeat("0", listF.size()));
          String[] arrfs = fs1.split("\\|");
          // List<String> ls = new ArrayList<String>();
          for (String f1 : arrfs) {
            String[] arrayF = f1.split("=");
            int index = listF.indexOf(arrayF[0]);
            // int val = 1;
            List<String> lll = featsVal.get(arrayF[0]);
            int val = lll.indexOf(arrayF[1]) + 1;

            /*
             * if (featsVal.containsKey(arrayF[0])) { List<String> lll = featsVal.get(arrayF[0]);
             * int ind = lll.indexOf(arrayF[1]); if (ind == -1) { lll.add(arrayF[1]); val =
             * lll.size(); } else { val = ind + 1; } } else { List<String> lll1 = new
             * ArrayList<String>(); lll1.add(arrayF[1]); featsVal.put(arrayF[0], lll1); val = 1; }
             */
            value.replace(index, index + 1, String.valueOf(val));
          }
          String resVal = row1[3] + value;
          if (!uniqeTags.contains(resVal)) {
            uniqeTags.add(resVal);
          }
          row1[3] = resVal;
        }
      }
    }

    Collections.sort(uniqeTags);
    for (String taggg : uniqeTags) {
      System.out.println(taggg);
    }
    /*
     * for (Map.Entry<String, List<String>> entry : feats.entrySet()) {
     * System.out.println(entry.getKey() + "/" + entry.getValue()); } System.out.println(); for
     * (Map.Entry<String, List<String>> entry : featsVal.entrySet()) {
     * System.out.println(entry.getKey() + "/" + entry.getValue()); }
     */

    return listConll;
  }

  public static void writeToEndFile(String path, List<ArrayList<String[]>> resConll) {
    Writer writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false), "utf-8"));
      for (ArrayList<String[]> conll : resConll) {
        for (String[] line : conll) {
          StringBuilder lineRes = new StringBuilder();

          for (int i = 0; i < line.length; i++) {
            lineRes.append(line[i]);
            if (i != line.length - 1) {
              lineRes.append("\t");
            }
          }
          writer.write(lineRes + "\n");
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

  public static List<ArrayList<String[]>> readFile(String path, int countScip) throws IOException {
    List<ArrayList<String[]>> listConll = new ArrayList<ArrayList<String[]>>();
    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
      int i = 0;
      for (String line; i < countScip && (line = br.readLine()) != null; i++) {
      }

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
