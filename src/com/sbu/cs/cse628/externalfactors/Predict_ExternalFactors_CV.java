package com.sbu.cs.cse628.externalfactors;
/*
 * For each Genre 
 * It takes external factors actors , directors, writers , metascore , rated , released
 * as feature and predicts 5 fold cross validation Accuracies 
 *  
 * Uses SVM -Lib linear as classifier 
 * 
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

public class Predict_ExternalFactors_CV {
	static Map<Integer, List<TreeMap<Integer, Double>>> dataHashMap = new HashMap<Integer, List<TreeMap<Integer, Double>>>();
	static Map<String, Integer> globalActors = new HashMap<String, Integer>();
	static Map<String, Integer> globalDirectors = new HashMap<String, Integer>();
	static Map<String, Integer> globalWriters = new HashMap<String, Integer>();
	static Map<String, Integer> globalFeatureList = new HashMap<String, Integer>();
	static int globalCounter = 4;
	static List<TreeMap<Integer, Double>> trainData = new ArrayList<TreeMap<Integer, Double>>();
	static List<Integer> trainLabel = new ArrayList<Integer>();
	
	static double[] resultArray = new double[8];
	static int resultCounter = 0;

	public static void restVariables() {
		dataHashMap = new HashMap<Integer, List<TreeMap<Integer, Double>>>();
		globalActors = new HashMap<String, Integer>();
		globalDirectors = new HashMap<String, Integer>();
		globalWriters = new HashMap<String, Integer>();
		globalFeatureList = new HashMap<String, Integer>();
		globalCounter = 4;
		trainData = new ArrayList<TreeMap<Integer, Double>>();
		trainLabel = new ArrayList<Integer>();
		
	}

	public static void main(String args[]) throws IOException {
		constructFeatureMap("./PreProcessedExternalFactorsData/ExtFact_ActionMovies.txt",8.3,5.5);
		restVariables();
		constructFeatureMap("./PreProcessedExternalFactorsData/ExtFact_AdventureMovies.txt",8.0,5.9);
		restVariables();
		constructFeatureMap("./PreProcessedExternalFactorsData/ExtFact_ComedyMovies.txt",8.0,5.6);
		restVariables();
		constructFeatureMap("./PreProcessedExternalFactorsData/ExtFact_Drama.txt",8.5,5.7);
		restVariables();
		constructFeatureMap("./PreProcessedExternalFactorsData/ExtFact_Fantasy.txt",7.8,6);
		restVariables();
		constructFeatureMap("./PreProcessedExternalFactorsData/ExtFact_Horror.txt",7.2,5.5);
		restVariables();
		constructFeatureMap("./PreProcessedExternalFactorsData/ExtFact_Sci_FciMovies.txt",7.9,5.8);
		restVariables();
		constructFeatureMap("./PreProcessedExternalFactorsData/ExtFact_Thriller.txt",8.2,5.4);
		System.out.println("\n\n\n");

		System.out.printf("Cross Validation Accuracy for"
				+ " Action Genre Movies:%.2f\n", resultArray[0]);
		System.out.printf("Cross Validation Accuracy for"
				+ " Adventure Genre Movies:%.2f\n", resultArray[1]);
		System.out.printf("Cross Validation Accuracy for"
				+ " Comedy Genre Movies:%.2f\n", resultArray[2]);
		System.out.printf("Cross Validation Accuracy for"
				+ " Drama Genre Movies:%.2f\n", resultArray[3]);
		System.out.printf("Cross Validation Accuracy for"
				+ " Fantasy Genre Movies:%.2f\n", resultArray[4]);
		System.out.printf("Cross Validation Accuracy for"
				+ " Horror Genre Movies:%.2f\n", resultArray[5]);
		System.out.printf("Cross Validation Accuracy for"
				+ " Science Fiction Genre Movies:%.2f\n", resultArray[6]);
		System.out.printf("Cross Validation Accuracy for"
				+ " Thriller Genre Movies:%.2f\n", resultArray[7]);

	}

	private static void constructFeatureMap(String inputFile,
			double threshold1, double threshold2) throws NumberFormatException,
			IOException {
		String sCurrentLine;
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		while ((sCurrentLine = br.readLine()) != null) {
			TreeMap<Integer, Double> documentWordCount = new TreeMap<Integer, Double>();

			String[] data = sCurrentLine.split("\\$");
			Map<String, String> tempMap = new HashMap<String, String>();
			Map<String, Double> tempMpaa = new HashMap<String, Double>();
			Map<String, Double> tempRealeseDate = new HashMap<String, Double>();

			for (int i = 0; i < 5; i++) {
				tempMpaa.put("G", 1.0);
				tempMpaa.put("PG", 2.0);
				tempMpaa.put("PG-13", 3.0);
				tempMpaa.put("R", 4.0);
				tempMpaa.put("NC-17", 5.0);
			}
			for (int i = 0; i < 12; i++) {
				tempRealeseDate.put("Jan", 0.0);
				tempRealeseDate.put("Feb", 0.0);
				tempRealeseDate.put("Mar", 0.0);
				tempRealeseDate.put("Apr", 0.0);
				tempRealeseDate.put("May", 1.0);
				tempRealeseDate.put("Jun", 2.0);
				tempRealeseDate.put("Jul", 1.0);
				tempRealeseDate.put("Aug", 0.0);
				tempRealeseDate.put("Sep", 0.0);
				tempRealeseDate.put("Oct", 0.0);
				tempRealeseDate.put("Nov", 2.0);
				tempRealeseDate.put("Dec", 1.0);
			}

			for (String s : data) {

				tempMap.put(s.split(":")[0], s.split(":")[1]);
			}

			int label = 0;
			if (!(tempMap.get("imdbRating").equals("N/A")
					|| tempMap.get("Metascore").equals("N/A")
					|| tempMap.get("Rated").equals("N/A")
					|| tempMap.get("Released").equals("N/A")
					|| tempMap.get("Director").equals("N/A")
					|| tempMap.get("Actors").equals("N/A") || tempMap.get(
					"Writer").equals("N/A"))) {

				if (Double.parseDouble(tempMap.get("imdbRating")) >= threshold1) {
					label = 1;
				} else if (Double.parseDouble(tempMap.get("imdbRating")) <= threshold2) {
					label = 2;
				}
				documentWordCount.put(1,
						Double.parseDouble(tempMap.get("Metascore")));
				documentWordCount.put(2, tempMpaa.get(tempMap.get("Rated")));
				documentWordCount.put(3, tempRealeseDate.get(tempMap.get(
						"Released").split(" ")[1]));
				String[] actorList = tempMap.get("Actors").split(",");

				for (int i = 0; i < actorList.length; i++) {
					if (!globalFeatureList.containsKey(actorList[i])) {
						globalFeatureList.put(actorList[i], globalCounter++);
					}
					documentWordCount.put(globalFeatureList.get(actorList[i]),
							(double) 1);
				}

				String[] directorList = tempMap.get("Director").split(",");
				for (int i = 0; i < directorList.length; i++) {
					if (!globalFeatureList.containsKey(directorList[i])) {
						globalFeatureList.put(directorList[i], globalCounter++);
					}
					documentWordCount.put(
							globalFeatureList.get(directorList[i]), (double) 1);
				}

				String[] writerList = tempMap.get("Writer").split(",");
				for (int i = 0; i < writerList.length; i++) {

					if (!globalFeatureList.containsKey(writerList[i])) {
						globalFeatureList.put(writerList[i], globalCounter++);
					}
					documentWordCount.put(globalFeatureList.get(writerList[i]),
							(double) 1);
				}

				if (dataHashMap.containsKey(label)) {
					List<TreeMap<Integer, Double>> tempa = dataHashMap
							.get(label);
					tempa.add(documentWordCount);
					dataHashMap.put(label, tempa);
				} else {
					List<TreeMap<Integer, Double>> tempa = new ArrayList<TreeMap<Integer, Double>>();
					tempa.add(documentWordCount);

					dataHashMap.put(label, tempa);

				}
			}

		}
		constructData();
		libLinearSVM();
	}

	private static void constructData() {
		Iterator<?> it = dataHashMap.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			List<?> temp = (List<?>) pairs.getValue();
			int countArt = temp.size();
			int label = (int) pairs.getKey();

			if (label == 1 || label == 2) {
				List<TreeMap<Integer, Double>> tempData = dataHashMap.get(pairs
						.getKey());

				trainData
						.addAll(tempData.subList(0, (int) Math.floor(countArt)));

				for (int i = 0; i < countArt; i++) {
					if (i < (int) Math.floor(0.8 * countArt)) {
						trainLabel.add(Integer.parseInt(pairs.getKey()
								.toString()));
					} else {
						trainLabel.add(Integer.parseInt(pairs.getKey()
								.toString()));
					}
				}
			}
		}

	}

	private static void libLinearSVM() throws IOException {
		Feature[][] trainFeaturePresence2DArray = new Feature[trainData.size()][];
		SolverType solver = SolverType.L2R_L2LOSS_SVC;
		double C = 1.0;
		double eps = 0.001;
		Problem presenceproblem = new Problem();
		presenceproblem.l = trainData.size();
		presenceproblem.n = globalFeatureList.size() + 3;
		presenceproblem.bias = 1;

		double[] targetValues = new double[trainLabel.size()];
		for (int i = 0; i < trainLabel.size(); i++)
			targetValues[i] = (double) trainLabel.get(i);

		int j = 0;

		for (TreeMap<Integer, Double> documentWordCount : trainData) {
			Feature[] featurePresenceArray = new FeatureNode[documentWordCount
					.size()];
			int i = 0;
			for (Map.Entry<Integer, Double> wordCountEntry : documentWordCount
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

		int nr_fold = 5;

		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[presenceproblem.l];

		long start, stop;
		start = System.currentTimeMillis();
		Linear.crossValidation(presenceproblem, presenceparameter, nr_fold,
				target);
		stop = System.currentTimeMillis();

		if (presenceparameter.getSolverType().isSupportVectorRegression()) {
			for (int i = 0; i < presenceproblem.l; i++) {
				double y = presenceproblem.y[i];
				double v = target[i];
				total_error += (v - y) * (v - y);
				sumv += v;
				sumy += y;
				sumvv += v * v;
				sumyy += y * y;
				sumvy += v * y;
			}
		} else {
			int total_correct = 0;
			for (int i = 0; i < presenceproblem.l; i++)
				if (target[i] == presenceproblem.y[i])
					++total_correct;

			double result = 100.0 * total_correct / presenceproblem.l;
			
			resultArray[resultCounter++] = result;
			

		}

	}
}
