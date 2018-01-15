package morphology;

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

public class ConvertWithMinimalCountTags {
  private static Map<String, List<String>> featsMap = new HashMap<String, List<String>>();
  private static Map<String, List<String>> featsValMap = new HashMap<String, List<String>>();
  private static List<String> uniqeTags = new ArrayList<String>();

  static {
    init();
  }

  private static void init() {
    featsMap.put("DET", new ArrayList<String>() {
      {
        add("Case");
        add("Gender");
        add("Number");
      }
    });
    featsMap.put("ADV", new ArrayList<String>() {
      {
        add("Degree");
      }
    });
    featsMap.put("AUX", new ArrayList<String>() {
      {
        add("Gender");
        add("Number");
        add("VerbForm");
      }
    });
    featsMap.put("PRON", new ArrayList<String>() {
      {
        add("Case");
        add("Gender");
        add("Number");
        add("Person");
      }
    });
    featsMap.put("PROPN", new ArrayList<String>() {
      {
        add("Case");
        add("Gender");
        add("Number");
      }
    });
    featsMap.put("PART", new ArrayList<String>() {
      {
        add("Polarity");
      }
    });
    featsMap.put("ADJ", new ArrayList<String>() {
      {
        add("Case");
        add("Gender");
        add("Number");
      }
    });
    featsMap.put("VERB", new ArrayList<String>() {
      {
        add("Gender");
        add("Number");
        add("Person");
        add("VerbForm");
      }
    });
    featsMap.put("NUM", new ArrayList<String>() {
      {
        add("Case");
        add("Gender");
      }
    });
    featsMap.put("NOUN", new ArrayList<String>() {
      {
        add("Case");
        add("Gender");
        add("Number");
      }
    });

    featsValMap.put("Tense", new ArrayList<String>() {
      {
        add("Fut");
        add("Past");
        add("Pres");
      }
    });
    featsValMap.put("Foreign", new ArrayList<String>() {
      {
        add("Yes");
      }
    });
    featsValMap.put("Animacy", new ArrayList<String>() {
      {
        add("Anim");
        add("Inan");
      }
    });
    featsValMap.put("Degree", new ArrayList<String>() {
      {
        add("Cmp");
        add("Pos");
        add("Sup");
      }
    });
    featsValMap.put("VerbForm", new ArrayList<String>() {
      {
        add("Conv");
        add("Fin");
        add("Inf");
        add("Part");
      }
    });
    featsValMap.put("Gender", new ArrayList<String>() {
      {
        add("Fem");
        add("Masc");
        add("Neut");
      }
    });
    featsValMap.put("Aspect", new ArrayList<String>() {
      {
        add("Imp");
        add("Perf");
      }
    });
    featsValMap.put("Case", new ArrayList<String>() {
      {
        add("Acc");
        add("Dat");
        add("Gen");
        add("Ins");
        add("Loc");
        add("Nom");
        add("Par");
        add("Voc");
      }
    });
    featsValMap.put("Variant", new ArrayList<String>() {
      {
        add("Short");
      }
    });
    featsValMap.put("Mood", new ArrayList<String>() {
      {
        add("Cnd");
        add("Imp");
        add("Ind");
      }
    });
    featsValMap.put("Number", new ArrayList<String>() {
      {
        add("Plur");
        add("Sing");
      }
    });
    featsValMap.put("Polarity", new ArrayList<String>() {
      {
        add("Neg");
      }
    });
    featsValMap.put("Voice", new ArrayList<String>() {
      {
        add("Act");
        add("Mid");
        add("Pass");
      }
    });
    featsValMap.put("Person", new ArrayList<String>() {
      {
        add("1");
        add("2");
        add("3");
      }
    });
  }

  public static void main(String[] args) throws IOException {
    String pathConll = "C://Users//Ivan//Desktop//NLP//all_syntagrus.conllu";
    String pathResConll = "C://Users//Ivan//Desktop//resMin_all_syntagrus.conll";

    List<ArrayList<String[]>> listConll = readFile(pathConll, 0);
    List<ArrayList<String[]>> resConll = processingConveting(listConll);
    // writeToEndFile(pathResConll, resConll);
    
    Collections.sort(uniqeTags);
    for(String tag : uniqeTags) {
      System.out.print(tag + ",");
    }
  }

  public static List<ArrayList<String[]>> processingConveting(List<ArrayList<String[]>> listConll) {
    for (ArrayList<String[]> conll : listConll) {
      for (String[] row : conll) {
        String tag = row[3];
        String fs = row[5];
        if (!fs.equals("_")) {
          StringBuilder sb = new StringBuilder();
          boolean addToTag = false;
          String[] arrfs = fs.split("\\|");
          Map<String, String> fsmap = new HashMap<String, String>();
          for (String f : arrfs) {
            String[] sss = f.split("=");
            fsmap.put(sss[0], sss[1]);
          }
          List<String> listMF = featsMap.get(tag);
          if (listMF != null && !listMF.isEmpty()) {
            for (String val : listMF) {
              if (fsmap.containsKey(val)) {
                sb.append(String.valueOf(featsValMap.get(val).indexOf(fsmap.get(val)) + 1));
                addToTag = true;
              } else {
                sb.append("0");
              }
            }
            if (addToTag) {
              row[3] = tag + sb.toString();
            }
          }
        }
        if(!uniqeTags.contains(row[3])) {
          uniqeTags.add(row[3]);
        }
      }
    }
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
          list.add(line.split("\t"));
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return listConll;
  }

}
