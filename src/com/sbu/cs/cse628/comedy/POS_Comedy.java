package com.sbu.cs.cse628.comedy;
/*
 * For Comedy Genre 
 * This program splits 80% of the data as train  data and 20% of data as test data
 * It  takes POS Tags Distribution in movie scripts as features and predicts Accuracy
 * Uses SVM -Lib linear as classifier 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;



public class POS_Comedy {

	
	static Map<Double, List<TreeMap<Integer, Integer>>> dataHashMap = new HashMap<Double, List<TreeMap<Integer, Integer>>>();
	static Map<String, Integer> globalUnigrams = new HashMap<String, Integer>();
	static List<TreeMap<Integer, Integer>> trainData = new ArrayList<TreeMap<Integer, Integer>>();
	static List<Double> trainLabel = new ArrayList<Double>();
	static List<TreeMap<Integer, Integer>> testData = new ArrayList<TreeMap<Integer, Integer>>();
	static List<Double> testLabel = new ArrayList<Double>();
	static int gCounter = 1;

	public static void main(String args[]) throws IOException {

		
		final File file = new File("./Script_data/Comedy1");
		MaxentTagger tagger = new MaxentTagger(
				"./POSTaggerLib/english-left3words-distsim.tagger ");

		constructFeatures(file, tagger);
		constructData();
		libLinearSVM();
		
	}

	private static void libLinearSVM() throws IOException {
		Feature[][] trainFeaturePresence2DArray = new Feature[trainData.size()][];
		Feature[][] testFeaturePresence2DArray = new Feature[testData.size()][];
		SolverType solver = SolverType.L2R_L2LOSS_SVC; 
		double C = 1.0; 
		double eps = 0.01; 
		Problem presenceproblem = new Problem();
		presenceproblem.l = trainData.size();
		presenceproblem.n = globalUnigrams.size(); 
		presenceproblem.bias = 1;
		
		double[] targetValues = new double[trainLabel.size()];
		for (int i = 0; i < trainLabel.size(); i++)
			targetValues[i] = (double) trainLabel.get(i);

		int j = 0;
		
		for (TreeMap<Integer, Integer> documentWordCount : trainData) {
			Feature[] featureArray = new FeatureNode[documentWordCount.size()];
			Feature[] featurePresenceArray = new FeatureNode[documentWordCount
					.size()];
			int i = 0;
			for (Map.Entry<Integer, Integer> wordCountEntry : documentWordCount
					.entrySet()) {
				
				FeatureNode featurePresenceNode = new FeatureNode(
						wordCountEntry.getKey(), wordCountEntry.getValue());
				
				featurePresenceArray[i] = featurePresenceNode;

				i++;
			}

			
			trainFeaturePresence2DArray[j] = featurePresenceArray;

			j++;
		}
		

		presenceproblem.x = trainFeaturePresence2DArray;
		presenceproblem.y = targetValues;

		Parameter presenceparameter = new Parameter(solver, C, eps);
		

		Model presencemodel = Linear.train(presenceproblem, presenceparameter);
		File presencemodelFile = new File("model");
		presencemodel.save(presencemodelFile);
		
		presencemodel = Model.load(presencemodelFile);
		
		int k = 0;
		for (TreeMap<Integer, Integer> documentWordCount : testData) {
			Feature[] featureArray = new FeatureNode[documentWordCount.size()];
			Feature[] featurePresenceArray = new FeatureNode[documentWordCount
					.size()];
			int i = 0;
			for (Map.Entry<Integer, Integer> wordCountEntry : documentWordCount
					.entrySet()) {

				FeatureNode featurePresenceNode = new FeatureNode(
						wordCountEntry.getKey(), wordCountEntry.getValue());
				
				featurePresenceArray[i] = featurePresenceNode;
				i++;
			}
			
			testFeaturePresence2DArray[k] = featurePresenceArray;
			k++;
		}
		int testpresenceErrorCount = 0;
		
		for (int l = 0; l < testData.size(); l++) {

			Feature[] instance = testFeaturePresence2DArray[l];
			double prediction = Linear.predict(presencemodel, instance);
			if (testLabel.get(l) != prediction) {
				
				testpresenceErrorCount++;
			} else {
				
			}
		}
		double s = testData.size();
		double accuracy = testpresenceErrorCount / s;
		System.out.println("\nAccuracy by considering 80 % data as train and 20 % as test and POS Tags distribution as features\n");
		System.out.printf("Comedy Genre:%.2f\n",(1 - accuracy) * 100);
		

	}

	private static void constructData() {
		int counter = 0;
		Iterator it = dataHashMap.entrySet().iterator();
		int class1=0;
		int class2=0;
		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			
			List temp = (List) pairs.getValue();
			int countArt = temp.size();
			
			double label = 0.0;
			if ((double) pairs.getKey() >= 8.2) {
				label = 1.0;
				class1 += countArt;
			} else if ((double) pairs.getKey() <= 4.8) {
				label = 2.0;
				class2 += countArt;
			}
			if (label == 1.0 || label == 2.0) {
				
				List<TreeMap<Integer, Integer>> tempData = dataHashMap
						.get(pairs.getKey());

				trainData.addAll(tempData.subList(0,
						(int) Math.floor(0.8 * countArt)));
				testData.addAll(tempData.subList(
						(int) Math.floor(0.8 * countArt), countArt));
				for (int i = 0; i < countArt; i++) {
					if (i < (int) Math.floor(0.8 * countArt)) {
						trainLabel.add(label);
					} else {
						testLabel.add(label);
					}
				}
				counter++;
			}
			
		}
		

	}

	static int spc_count = -1;

	static void constructFeatures(File aFile, MaxentTagger tagger) throws IOException {
		spc_count++;
		String spcs = "";
		for (int i = 0; i < spc_count; i++)
			spcs += " ";
		double label = 0.0;
		if (aFile.isFile()) {

			TreeMap<Integer, Integer> documentWordCount = new TreeMap<Integer, Integer>();

			String sCurrentLine;

			BufferedReader br = new BufferedReader(new FileReader(aFile));
			String word = br.readLine();
			
			if (word.contains("imdbRating")) {
				word = word.split(":")[1];

				if (null != word) {
					if (!word.trim().contains("N/A")
							&& !word.trim().equals("null")) {
						label = Double.parseDouble(word);
					}
				}

			}
			
			String content = new Scanner(aFile).useDelimiter("\\Z").next();
		
			String temp[] = content.split("\\.");
			int poscount=0;
			
			if (temp.length >= 200) {
				for (String s : temp) {
					
					String temp1 = tagger.tagString(s);
					String[] wodList = temp1.split(" ");
					
					for (String s1 : wodList) {
						if (s1.trim().length() > 0) {
							poscount+=1;
							String tag = s1.split("_")[1];
							if (!globalUnigrams.containsKey(tag)) {
								globalUnigrams.put(tag, gCounter++);
							}

							if (documentWordCount.containsKey(globalUnigrams
									.get(tag)))

								documentWordCount.put(globalUnigrams.get(tag),
										documentWordCount.get(globalUnigrams
												.get(tag)) + 1);
							else {
								documentWordCount.put(globalUnigrams.get(tag),
										1);
							}
						}

					}

				}
				
				Iterator posItr = documentWordCount.entrySet().iterator();
				while (posItr.hasNext()) {

					Map.Entry pairs = (Map.Entry) posItr.next();
					documentWordCount.put((Integer) pairs.getKey(), (int)pairs.getValue());
				}
				if (dataHashMap.containsKey(label)) {
					List<TreeMap<Integer, Integer>> tempa = dataHashMap
							.get(label);
					tempa.add(documentWordCount);
					
					dataHashMap.put(label, tempa);
				} else {
					List<TreeMap<Integer, Integer>> tempa = new ArrayList<TreeMap<Integer, Integer>>();
					tempa.add(documentWordCount);
					
					dataHashMap.put(label, tempa);

				}

			}

		} else if (aFile.isDirectory()) {
			
			File[] listOfFiles = aFile.listFiles();
			if (listOfFiles != null) {
				for (int i = 0; i < listOfFiles.length; i++)
					constructFeatures(listOfFiles[i], tagger);
			} else {
				System.out.println(spcs + " [ACCESS DENIED]");
			}
		}
		spc_count--;
	}

}
