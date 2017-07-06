package tagger;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConverterForTagger {
  private static StanfordCoreNLP pipeline;

  public static void main(String[] args) throws IOException {
    if (args.length == 2) {
      init();

      List<String> list = ProcessingFile.readFile(args[0]);
      List<String> res = new ArrayList<String>();
      for (String line : list) {
        List<CoreMap> sentences = process(line);
        for (CoreMap sentence : sentences) {
          StringBuffer sb = new StringBuffer();
          for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            // System.out.println(token.originalText() + "\t|\t" + token.get(NERAnnotation.class));
            sb.append(token.originalText()).append(" ");
          }
          res.add(sb.toString());
        }
      }

      ProcessingFile.writeToFile(args[1], res);
    }
  }

  public static void init() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit");
    pipeline = new StanfordCoreNLP(props);
    // pipeline.addAnnotator(new LemmatizationAnnotator());
    // pipeline.addAnnotator(new NERAnnotator());
    // pipeline.addAnnotator(new Tree);// new TreeCoreAnnotations.TreeAnnotation.class);
  }

  public static List<CoreMap> process(String text) {
    Annotation annotation = pipeline.process(text);// LemmaAnnotation
    // String val = annotation.get(LemmatizationAnnotation.class);
    return annotation.get(CoreAnnotations.SentencesAnnotation.class);
  }

}
