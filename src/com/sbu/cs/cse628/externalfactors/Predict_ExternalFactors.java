package com.sbu.cs.cse628.externalfactors;
/*
 * For each Genre 
 * This program splits 80% of the data as train  data and 20% of data as test data
 * 
 * It takes external factors actors , directors, writers , metascore , rated , released
 * as feature and predicts Accuracies 
 *  *  
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
import java.util.TreeMap;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

public class Predict_ExternalFactors {
	static Map<Integer, List<TreeMap<Integer, Double>>> dataHashMap = new HashMap<Integer, List<TreeMap<Integer, Double>>>();
	static Map<String, Integer> globalActors = new HashMap<String, Integer>();
	static Map<String, Integer> globalDirectors = new HashMap<String, Integer>();
	static Map<String, Integer> globalWriters = new HashMap<String, Integer>();
	static Map<String, Integer> globalFeatureList = new HashMap<String, Integer>();
	static int globalCounter=4;

	static List<TreeMap<Integer, Double>> trainData = new ArrayList<TreeMap<Integer, Double>>();
	static List<Integer> trainLabel = new ArrayList<Integer>();
	static List<TreeMap<Integer, Double>> testData = new ArrayList<TreeMap<Integer, Double>>();
	static List<Integer> testLabel = new ArrayList<Integer>();
	static double[] resultArray=new double[8];
	static int resultCounter=0;

	public static void restVariables(){
		 dataHashMap = new HashMap<Integer, List<TreeMap<Integer, Double>>>();
		 globalActors = new HashMap<String, Integer>();
		 globalDirectors = new HashMap<String, Integer>();
		 globalWriters = new HashMap<String, Integer>();
		 globalFeatureList = new HashMap<String, Integer>();
		 globalCounter=4;


		 trainData = new ArrayList<TreeMap<Integer, Double>>();
		 trainLabel = new ArrayList<Integer>();
		 testData = new ArrayList<TreeMap<Integer, Double>>();
		 testLabel = new ArrayList<Integer>();
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
		
		//System.out.printf("%.2f\n", resultArray[1]);
		System.out.println("Accuracy by considering 80 % data as train and 20 % as test");
		System.out.printf("Action Genre:%.2f\n",resultArray[0]);
		System.out.printf("Adventure Genre:%.2f\n",resultArray[1]);
		System.out.printf("Comedy Genre:%.2f\n",resultArray[2]);
		System.out.printf("Drama Genre:%.2f\n",resultArray[3]);
		System.out.printf("Fantasy Genre:%.2f\n",resultArray[4]);
		System.out.printf("Horror Genre:%.2f\n",resultArray[5]);
		System.out.printf("Science Fiction Genre:%.2f\n",resultArray[6]);
		System.out.printf("Thriller Genre:%.2f\n",resultArray[7]);

	}
	
	//constructing data hash map- considering only external factors Actors, Directors, Writers,
	// metascore, released, rated
	 
	public static void constructFeatureMap(String inputFile, double threshold1, double threshold2) throws NumberFormatException, IOException{
		String sCurrentLine;
		BufferedReader br = new BufferedReader(new FileReader(
				inputFile));
	

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
				//System.out.println(s.toString());
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
				
				//System.out.println("LABELS "+tempMap.get("imdbRating"));
				  if (Double.parseDouble(tempMap.get("imdbRating")) >= threshold1) {
						label = 1;
						
					}
				  else if (Double.parseDouble(tempMap.get("imdbRating")) <= threshold2) {
						label = 2;
			
					}
				  
		
					documentWordCount.put(1,
							Double.parseDouble(tempMap.get("Metascore")));
					if(!tempMpaa.containsKey(tempMap.get("Rated"))){
						System.out.println("Rated:"+tempMap.get("Rated"));
					}
					documentWordCount
							.put(2, tempMpaa.get(tempMap.get("Rated")));
					documentWordCount.put(
							3,	tempRealeseDate.get(tempMap.get("Released").split(
									" ")[1]));
					String[] actorList = tempMap.get("Actors").split(",");
					
				

					for (int i = 0; i < actorList.length; i++) {
						if(!globalFeatureList.containsKey(actorList[i])){
							globalFeatureList.put(actorList[i],globalCounter++ );
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
								globalFeatureList.get(directorList[i]),
								(double) 1);
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
	
	//constructing data - considering 80 % data as training and 20 % data as test

	private static void constructData() {
		Iterator<?> it = dataHashMap.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			List<?> temp = (List<?>) pairs.getValue();
			int countArt = temp.size();
			int label=(int) pairs.getKey();
		
			if (label==1 || label==2) {
				List<TreeMap<Integer, Double>> tempData = dataHashMap.get(pairs
						.getKey());
				
				trainData.addAll(tempData.subList(0,
						(int) Math.floor(0.8 * countArt)));
				testData.addAll(tempData.subList(
						(int) Math.floor(0.8 * countArt), countArt));
				for (int i = 0; i < countArt; i++) {
					if (i < (int) Math.floor(0.8 * countArt)) {
						trainLabel.add(Integer.parseInt(pairs.getKey()
								.toString()));
					} else {
						testLabel.add(Integer.parseInt(pairs.getKey()
								.toString()));
					}
				}
			}
			

		}

	}
	
	//Classification using lib linear

	private static void libLinearSVM() throws IOException {
		Feature[][] trainFeaturePresence2DArray = new Feature[trainData.size()][];
		Feature[][] testFeaturePresence2DArray = new Feature[testData.size()][];
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
				
				//System.out.println(wordCountEntry.getKey()+" its value "+wordCountEntry.getValue());
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
		for (TreeMap<Integer, Double> documentWordCount : testData) {
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
			
			testFeaturePresence2DArray[k] = featurePresenceArray;
			k++;
		}
		int testpresenceErrorCount = 0;
		
		for (int l = 0; l < testData.size(); l++) {

			Feature[] instance = testFeaturePresence2DArray[l];
			double prediction = Linear.predict(presencemodel, instance);
			if (testLabel.get(l) != prediction) {
				
				/*System.out.println(" Test label" + testLabel.get(l)
						+ "Incorrect predicted:" + prediction);*/
				testpresenceErrorCount++;
			} else {
				/*System.out.println("Test label" + testLabel.get(l)
						+ "predicted:" + prediction);*/
			}
		}
		/*System.out.println("error count:" + testpresenceErrorCount);
		System.out.println("total test data:" + testData.size());
		System.out.println("total train data:" + trainData.size());*/
		double s=testData.size();
		double accuracy=testpresenceErrorCount/s;
		//System.out.println("Accuracy: "+(1-accuracy)*100);
		double result=(1-accuracy)*100;
		resultArray[resultCounter++]=result;

	
}
}
