package parse;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

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
import java.util.TreeMap;

public class CompareConll {

  public static void main(String[] args) throws IOException {
    List<ArrayList<String[]>> listOrigin =
        readFile("C://Users//Ivan//Desktop//NLP//ru_syntagrus-ud-dev.conllu");
    List<ArrayList<String[]>> listRes =
        readFile("C://Users//Ivan//Desktop//NLP//res_test_dev.conll");
    MaxentTagger mt = new MaxentTagger("C://Users//Ivan//Desktop//NLP//russian-ud-pos.tagger");

    List<Integer> listRelation = new ArrayList<Integer>(Collections.nCopies(140, 0));
    List<Integer> listIndex = new ArrayList<Integer>(Collections.nCopies(140, 0));
    List<Integer> listErr = new ArrayList<Integer>(Collections.nCopies(140, 0));
    
    List<Integer> listCountRel = new ArrayList<Integer>(Collections.nCopies(140, 0));

    Map<String, Integer> tagsErr = new HashMap<String, Integer>();
    Map<String, Integer> tagsCount = new HashMap<String, Integer>();

    for (int i = 0; i < listRes.size(); i++) {
      int shift = 0;
      for (int j = 0; j < listRes.get(i).size(); j++) {
        for (int s = 0; s < listOrigin.get(i).size() - j; s++) {
          if (listOrigin.get(i).get(j + shift)[1] == null
              || listOrigin.get(i).get(j + shift)[1].equals("")
              || listOrigin.get(i).get(j + shift)[1].equals("_")) {
            shift++;
          } else {
            break;
          }
        }
        String tagStr = mt.tagTokenizedString(listRes.get(i).get(j)[1]);
        listRes.get(i).get(j)[3] = tagStr.substring(tagStr.indexOf("_") + 1).trim();
        boolean err = false;
        int shiftIndex = Math.abs(Integer.valueOf(listOrigin.get(i).get(j + shift)[6])
            - Integer.valueOf(listOrigin.get(i).get(j + shift)[0]));
        if (!listOrigin.get(i).get(j + shift)[6].equals(listRes.get(i).get(j)[6])) {
          listRes.get(i).get(j)[0] = "#INDEX=" + listOrigin.get(i).get(j + shift)[6] + "#" + "\t"
              + listRes.get(i).get(j)[0];
          err = true;
          listIndex.set(shiftIndex, listIndex.get(shiftIndex) + 1);
        }
        if (!listOrigin.get(i).get(j + shift)[7].equals(listRes.get(i).get(j)[7])) {
          listRes.get(i).get(j)[0] = "#RELATION=" + listOrigin.get(i).get(j + shift)[7] + "#" + "\t"
              + listRes.get(i).get(j)[0];
          err = true;
          listRelation.set(shiftIndex, listRelation.get(shiftIndex) + 1);
        }
        listCountRel.set(shiftIndex, listCountRel.get(shiftIndex) + 1);
        if (err) {
          listErr.set(shiftIndex, listErr.get(shiftIndex) + 1);
          listRes.get(i).get(j)[3] =
              listOrigin.get(i).get(j + shift)[3] + "|" + listRes.get(i).get(j)[3];

          if (tagsErr.containsKey(listOrigin.get(i).get(j + shift)[7])) {
            tagsErr.replace(listOrigin.get(i).get(j + shift)[7],
                tagsErr.get(listOrigin.get(i).get(j + shift)[7]) + 1);
          } else {
            tagsErr.put(listOrigin.get(i).get(j + shift)[7], 1);
          }
        }
        if (tagsCount.containsKey(listOrigin.get(i).get(j + shift)[7])) {
          tagsCount.replace(listOrigin.get(i).get(j + shift)[7],
              tagsCount.get(listOrigin.get(i).get(j + shift)[7]) + 1);
        } else {
          tagsCount.put(listOrigin.get(i).get(j + shift)[7], 1);
        }
      }
    }

    Map<Double, Integer> treeMapIndex = new TreeMap<Double, Integer>();
    System.out.println("Index");
    for (int i = 0; i < listIndex.size(); i++) {
      if (listIndex.get(i) != 0) {
        System.out.println(String.valueOf(i) + "\t/\t" + String.valueOf(listIndex.get(i)));
        treeMapIndex.put(((double) listIndex.get(i))/((double)listCountRel.get(i)), i);
      }
    }
    System.out.println();
    System.out.println();
    System.out.println("Relation");
    Map<Double, Integer> treeMapRel = new TreeMap<Double, Integer>();
    for (int i = 0; i < listRelation.size(); i++) {
      if (listRelation.get(i) != 0) {
        System.out.println(String.valueOf(i) + "\t/\t" + String.valueOf(listRelation.get(i)));
        treeMapRel.put(((double) listRelation.get(i))/((double)listCountRel.get(i)), i);
      }
    }
    System.out.println();
    System.out.println();
    System.out.println("Error");
    Map<Double, Integer> treeMapErr = new TreeMap<Double, Integer>();
    for (int i = 0; i < listErr.size(); i++) {
      if (listErr.get(i) != 0) {
        System.out.println(String.valueOf(i) + "\t/\t" + String.valueOf(listErr.get(i)));
        treeMapErr.put(((double) listErr.get(i))/((double)listCountRel.get(i)), i);
      }
    }


    
    Map<Double, String> treeMapTagsErr = new TreeMap<Double, String>();
    System.out.println();
    System.out.println();
    System.out.println("Relation tag");
    for (Map.Entry<String, Integer> entry : tagsErr.entrySet()) {
      System.out.println(entry.getKey() + "\t/\t" + entry.getValue());
      treeMapTagsErr.put(((double) entry.getValue())/((double) tagsCount.get(entry.getKey())), entry.getKey());
    } 
    
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println("Относительная частота");
    System.out.println();
    System.out.println("Index");
    for (Map.Entry<Double, Integer> entry : treeMapIndex.entrySet()) {
      System.out.println(entry.getValue() + "\t/\t" + entry.getKey());
    }
    System.out.println();
    System.out.println();
    System.out.println("Relation");
    for (Map.Entry<Double, Integer> entry : treeMapRel.entrySet()) {
      System.out.println(entry.getValue() + "\t/\t" + entry.getKey());
    }
    System.out.println();
    System.out.println();
    System.out.println("Error");
    for (Map.Entry<Double, Integer> entry : treeMapErr.entrySet()) {
      System.out.println(entry.getValue() + "\t/\t" + entry.getKey());
    }
    System.out.println();
    System.out.println();
    System.out.println("Relation tag");
    for (Map.Entry<Double, String> entry : treeMapTagsErr.entrySet()) {
      System.out.println(entry.getValue() + "\t/\t" + entry.getKey() + "\t/\t" + String.valueOf(
          tagsCount.get(entry.getValue())));
    }
    
    
    System.out.println();
    System.out.println();
    System.out.println("Новые значения");
    for (int i = 0; i < listErr.size(); i++) {
      if (listErr.get(i) != 0) {
        System.out.println(String.valueOf(i) + "\t" + String.valueOf(((double) listErr.get(i))/((double)listCountRel.get(i))));
      }
    }
    
    
    writeToFile("C://Users//Ivan//Desktop//NLP//res_TEST1.conll", listRes);

  }

  public static void writeToFile(String path, List<ArrayList<String[]>> resConll) {
    Writer writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false), "utf-8"));
      for (ArrayList<String[]> conll : resConll) {
        for (String[] line : conll) {
          writer.write(String.join("\t", line) + "\n");
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
          list.add(line.split("\t"));
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return listConll;
  }
}
