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

public class ConverterKKtoUD {


  public static void main(String[] args) throws IOException {
    String pathKKConll = "C://Users//Ivan//Desktop//conll_convert//resKK.conll";
    String pathResConll = "C://Users//Ivan//Desktop//conll_convert//res.conll";

    List<ArrayList<String[]>> listConll = readFile(pathKKConll, 0);
    List<ArrayList<String[]>> resConll = processingConveting(listConll);
    writeToEndFile(pathResConll, resConll);
  }

  public static String getIndexRootName(String[] line, ArrayList<String[]> conll) {
    String index = null;
    String[] strPar = conll.get(Integer.valueOf(line[6]) - 1);
    if (strPar[7].equals("��������")) {
      index = strPar[6];
    } else if (strPar[7].equals("�����_����")) {
      String ind = getIndexRootName(strPar, conll);
      if (ind != null) {
        index = ind;
      } else {
        index = strPar[6];
      }
    }
    return index;
  }

  public static String getIndexRoot(String[] line, ArrayList<String[]> conll, String dep) {
    String index = null;
    String[] strPar = conll.get(Integer.valueOf(line[6]) - 1);
    if (strPar[7].equals(dep)) {
      String ind = getIndexRootName(strPar, conll);
      if (ind != null) {
        index = ind;
      } else {
        index = strPar[6];
      }
    }
    return index;
  }

  public static int getWithRel(int index, ArrayList<String[]> conll) {
    for (int i = 0; i < conll.size(); i++) {
      if (Integer.valueOf(conll.get(i)[6]) == index) {
        return i;
      }
    }
    return -1;
  }

  public static void changeDep(ArrayList<String[]> conll) {
    for (String[] line : conll) {
      if (line[7].equals("�����_����")) {
        String index = getIndexRootName(line, conll);
        if (index != null) {
          line[6] = index;// strPar[6];
        }
      } else if (line[7].equals("�����")) {
        String index = getIndexRoot(line, conll, "�����");
        if (index != null) {
          line[6] = index;// strPar[6];
        }
      } else if (line[7].equals("�������")) {
        String index = getIndexRoot(line, conll, "�������");
        if (index != null) {
          line[6] = index;// strPar[6];
        }
      } else if (line[3].equals("ADP") && !line[7].equals("����")
          && (line[7].indexOf(line[1]) == 0 || line[7].equals("���") || line[7].equals("����"))) { // �����,
                                                                                                   // ���
                                                                                                   // �
                                                                                                   // �.�.
        String[] strPar = conll.get(Integer.valueOf(line[6]) - 1);
        int index = getWithRel(Integer.valueOf(line[0]), conll);
        String[] noun = conll.get(index);
        if ((noun[3].equals("NOUN") || noun[3].equals("PNOUN")) && strPar[3].equals("VERB")) {// if (line[7].indexOf(noun[7]) !=
                                                                // -1) {
          noun[6] = line[6];
          line[6] = noun[0];

          noun[7] = "obl";
          line[7] = "case";
        }
      }
    }
  }

  public static List<ArrayList<String[]>> processingConveting(List<ArrayList<String[]>> listConll) {
    for (ArrayList<String[]> conll : listConll) {
      changeDep(conll);// ����� �������� + ..
      for (String[] line : conll) {
        if (line[7].equals("�����")) {
          line[7] = "amod"; // DEPREL
        } else if (line[7].equals("��")) {
          line[7] = "fixed";
        } else if (line[7].equals("���") || line[7].equals("��������") || line[7].equals("���")
            || line[7].equals("�����_����")) {
          line[7] = "flat:name";
        } else if (line[7].equals("�����")) {
          line[7] = "neg";
        } else if (line[7].equals("�������")) {
          line[7] = "nsubj";
        } else if (line[7].equals("���") || line[7].equals("�������")) {
          line[7] = "advmod";
        } else if (line[7].equals("�������") || line[7].equals("�����")) {
          line[7] = "conj";
        } else if (line[7].equals("�������")) {
          line[7] = "discourse";
        } else if (line[7].equals("���")) {
          line[7] = "iobj";
        } else if (line[7].equals("����")) {
          line[7] = "cc";
        } else if (line[7].equals("��������")) {
          line[7] = "compound";
        } else if (line[7].equals("�������")) {
          line[7] = "nummod";
        } else if (line[7].equals("���")) {
          line[7] = "nmod";
        } else if (line[7].equals("���")) {
          line[7] = "appos";
        } else if (line[7].equals("����")) {
          line[7] = "flat";
        } else if (line[7].equals("����_������")) {
          line[7] = "acl";
        } else if (line[7].equals("�������")) {
          line[7] = "acl:relcl";
        } else if (line[7].equals("�����")) {
          line[7] = "advcl";
        } else if (line[7].equals("����")) {
          line[7] = "advmod"; // ?????????
          // "����(VERB,ADV) > advmod(VERB,ADV)
        } else if (line[7].equals("������") && line[3].equals("CONJ")) { // mark // 40 ������
          int index1 = getWithRel(Integer.valueOf(line[0]), conll);
          String[] vb1 = conll.get(index1);
          if (vb1[3].equals("VERB") && vb1[7].equals("������")) {
            vb1[6] = line[6];
            line[6] = vb1[0];

            vb1[7] = "ccomp";
            line[7] = "mark";
          }
        } else if (line[7].equals("���")) {
          String[] strPar = conll.get(Integer.valueOf(line[6]) - 1);
          if (strPar[7].equals("root") || strPar[7].equals("VERB")) {
            line[7] = "obj";
          }
        }
      }
    }

    return listConll;
  }

  public static void writeToEndFile(String path, List<ArrayList<String[]>> resConll) {
    Writer writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), "utf-8"));
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
        if (line.isEmpty()) {
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
