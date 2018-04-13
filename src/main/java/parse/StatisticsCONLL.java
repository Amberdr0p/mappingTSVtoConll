package parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import tagger.ProcessingFile;

public class StatisticsCONLL {

	public static void main(String[] args) throws IOException {
		String pathConllFolder;
		if(args.length > 0) {
			pathConllFolder = args[0];
		} else {
			pathConllFolder = "/home/comp/eclipse-workspace/PARS/conll";
		}
		List<File> conllFiles = getFiles(pathConllFolder, ".conll", "conllu");
		checkAndMkdir(new File("statistics"));
		Map<String, Integer> fullMapPOS = new HashMap<String, Integer>();
		Map<String, Integer> fullMapSyntactic = new HashMap<String, Integer>();
		Map<Pair<String, String>, Integer> fullMapPOSSyntatic = new HashMap<Pair<String, String>, Integer>();
		int countTokens = 0;
		int countSentence = 0;
		for (File conllFile : conllFiles) {
			List<List<String>> fullList = new ArrayList<List<String>>();
			preProcessingConllFile(conllFile, fullList);
			countSentence += fullList.size();
			countTokens += processingConllFile(conllFile.getName(), fullList, fullMapPOS, fullMapSyntactic,
					fullMapPOSSyntatic);
		}
		System.out.println("Count sentence: " + countSentence + " tokens: " + countTokens);
		writeToFile("summary.txt", fullMapPOS, fullMapSyntactic, fullMapPOSSyntatic, countSentence, countTokens);
	}

	public static int processingConllFile(String filename, List<List<String>> fullList, Map<String, Integer> fullMapPOS,
			Map<String, Integer> fullMapSyntactic, Map<Pair<String, String>, Integer> fullMapPOSSyntatic) {
		Map<String, Integer> mapPOS = new HashMap<String, Integer>();
		Map<String, Integer> mapSyntactic = new HashMap<String, Integer>();
		Map<Pair<String, String>, Integer> mapPOSSyntatic = new HashMap<Pair<String, String>, Integer>();
		int countTokens = 0;
		for (List<String> sentence : fullList) {
			countTokens += sentence.size();
			for (String token : sentence) {
				String[] splitToken = token.split("\t");
				if (mapPOS.containsKey(splitToken[3])) {
					mapPOS.replace(splitToken[3], mapPOS.get(splitToken[3]) + 1);
				} else {
					mapPOS.put(splitToken[3], 1);
				}
				if (fullMapPOS.containsKey(splitToken[3])) {
					fullMapPOS.replace(splitToken[3], fullMapPOS.get(splitToken[3]) + 1);
				} else {
					fullMapPOS.put(splitToken[3], 1);
				}
				if (mapSyntactic.containsKey(splitToken[7])) {
					mapSyntactic.replace(splitToken[7], mapSyntactic.get(splitToken[7]) + 1);
				} else {
					mapSyntactic.put(splitToken[7], 1);
				}
				if (fullMapSyntactic.containsKey(splitToken[7])) {
					fullMapSyntactic.replace(splitToken[7], fullMapSyntactic.get(splitToken[7]) + 1);
				} else {
					fullMapSyntactic.put(splitToken[7], 1);
				}

				if (!splitToken[6].equals("0")) {
					// System.out.println(splitToken[7] + " " + splitToken[6]);
					Pair<String, String> pair = new MutablePair<String, String>(splitToken[3],
							sentence.get(Integer.valueOf(splitToken[6]) - 1).split("\t")[3]);
					if (mapPOSSyntatic.containsKey(pair)) {
						mapPOSSyntatic.replace(pair, mapPOSSyntatic.get(pair) + 1);
					} else {
						mapPOSSyntatic.put(pair, 1);
					}
					if (fullMapPOSSyntatic.containsKey(pair)) {
						fullMapPOSSyntatic.replace(pair, fullMapPOSSyntatic.get(pair) + 1);
					} else {
						fullMapPOSSyntatic.put(pair, 1);
					}
				} else {
					if (!splitToken[7].equals("root")) {
						System.out.println(splitToken[7] + " " + splitToken[6]);
					}
				}
			}
		}
		// writeToFile(filename, mapPOS, mapSyntactic, mapPOSSyntatic, fullList.size(), countTokens);
		return countTokens;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	private static void checkAndMkdir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	private static void writeToFile(String filename, Map<String, Integer> fullMapPOS,
			Map<String, Integer> fullMapSyntactic, Map<Pair<String, String>, Integer> fullMapPOSSyntatic,
			int countSentence, int countTokens) {
		Writer writer = null;
		fullMapPOS = sortByValue(fullMapPOS);
		fullMapSyntactic = sortByValue(fullMapSyntactic);
		fullMapPOSSyntatic = sortByValue(fullMapPOSSyntatic);
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(
									"statistics/" + filename.substring(0, filename.lastIndexOf(".")) + ".txt", false),
							"utf-8"));

			writer.write("------------POS ------------\n");
			int i = 1;
			List<String> printingList = new ArrayList<String>();
			for (Map.Entry<String, Integer> entry : fullMapPOS.entrySet()) {
				if (i > fullMapPOS.size() / 2 + 1) {
					printingList.set(i - fullMapPOS.size() / 2 - 2,
							printingList.get(i - fullMapPOS.size() / 2 - 2) + " & " + entry.getKey() + " & "
									+ String.valueOf(i) + " & "
									+ String.format("%.4f", ((float) entry.getValue()) / countTokens));
				} else {
					printingList.add(entry.getKey() + " & " + String.valueOf(i) + " & "
							+ String.format("%.4f", ((float) entry.getValue()) / countTokens));
				}
				i++;
			}
			for (String printing : printingList) {
				writer.write(printing + " \\\\\n");
			}
			List<String> printingListSynt = new ArrayList<String>();
			writer.write("\n\n------------Syntactic------------\n");
			i = 1;
			for (Map.Entry<String, Integer> entry : fullMapSyntactic.entrySet()) {
				if (i > fullMapSyntactic.size() / 3 ) {
					int index;
					if (i < fullMapSyntactic.size() * 2 / 3 + 1) {
						index = i - fullMapSyntactic.size() / 3 - 1;
					} else {
						index = i - 2 * fullMapSyntactic.size() / 3 - 1;
					}
					printingListSynt.set(index, printingListSynt.get(index) + " & " + entry.getKey() + " & " + String.valueOf(i)
							+ " & " + String.format("%.4f", ((float) entry.getValue()) / countTokens));
				} else {
					printingListSynt.add(entry.getKey() + " & " + String.valueOf(i) + " & "
							+ String.format("%.4f", ((float) entry.getValue()) / countTokens));
				}

				i++;
			}
			for (String printingSynt : printingListSynt) {
				writer.write(printingSynt + " \\\\\n");
			}
			writer.write("\n\n------------POS and Syntactic------------\n");
			for (Map.Entry<Pair<String, String>, Integer> entry : fullMapPOSSyntatic.entrySet()) {
				writer.write(entry.getKey().getLeft() + "," + entry.getKey().getRight() + "\t"
						+ ((float) entry.getValue()) / countTokens + "\n");
			}
			writer.write("Count sentences = " + String.valueOf(countSentence) + ", count tokens = "
					+ String.valueOf(countTokens) + "\n");
			writer.write("------------Abs. POS------------\n");
			for (Map.Entry<String, Integer> entry : fullMapPOS.entrySet()) {
				writer.write(entry.getKey() + "\t" + entry.getValue() + "\n");
			}
			writer.write("\n\n------------Abs. Syntactic------------\n");
			for (Map.Entry<String, Integer> entry : fullMapSyntactic.entrySet()) {
				writer.write(entry.getKey() + "\t" + entry.getValue() + "\n");
			}
			writer.write("\n\n------------Abs. POS and Syntactic------------\n");
			for (Map.Entry<Pair<String, String>, Integer> entry : fullMapPOSSyntatic.entrySet()) {
				writer.write(
						entry.getKey().getLeft() + "," + entry.getKey().getRight() + "\t" + entry.getValue() + "\n");
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
					e.printStackTrace();
				}
			}
		}
	}

	public static void preProcessingConllFile(File conllFile, List<List<String>> fullList) throws IOException {
		List<String> rowsFile = ProcessingFile.readFile(conllFile.getAbsolutePath());
		List<String> list = new ArrayList<String>();
		for (String rowFile : rowsFile) {
			if (rowFile.isEmpty()) {
				if (!list.isEmpty()) {
					fullList.add(list);
					list = new ArrayList<String>();
				}
			} else if (rowFile.indexOf("# text") != 0 && rowFile.indexOf("# sent_id") != 0) {
				if (rowFile.split("\t").length == 10) {
					list.add(rowFile);
				} else {
					System.out.println(rowFile);
				}
			}
		}
	}

	public static List<File> getFiles(String pathFolder, final String... endFiles) throws IOException {
		File folder = new File(pathFolder);
		List<File> resFiles = new ArrayList<File>();
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					resFiles.addAll(getFiles(file.getAbsolutePath(), endFiles));
				} else {
					String filename = file.getName().toLowerCase();
					for (String endFile : endFiles) {
						if (filename.endsWith(endFile)) {
							resFiles.add(file);
						}
					}
				}
			}
		} else {
			System.out.println("Not found files in folder " + pathFolder);
		}
		return resFiles;
	}

}
