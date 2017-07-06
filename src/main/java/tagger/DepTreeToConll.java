package tagger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DepTreeToConll {

  public static void main(String[] args) throws IOException {
    if (args.length == 2) {

      List<String> list = ProcessingFile.readFile(args[0]);
      List<String> res = new ArrayList<String>();

      for (String line : list) {
        if (line.indexOf("-") != -1) {
          StringBuffer sb = new StringBuffer();

          sb.append(line.substring(line.lastIndexOf("-") + 1, line.length() - 1)).append("\t"); // index
          sb.append(line.substring(line.lastIndexOf(", ") + 2, line.lastIndexOf("-")))
              .append("\t_\t_\t_\t_\t"); // index

          int indexDep = line.substring(0, line.indexOf(", ")).lastIndexOf("-");

          sb.append(line.substring(indexDep + 1, line.indexOf(", "))).append("\t");
          sb.append(line.substring(0, line.indexOf("(")));
          sb.append("\t_\t_");

          res.add(sb.toString());
        } else {
          res.add("\n");
        }
      }

      ProcessingFile.writeToFile(args[1], res);
    }

  }


}
