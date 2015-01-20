package com.sbu.cs.cse628.crawldata;
/*
 * This Program get details about a movie(Title, Metascore,
 *  Actors, Director, Writer, Plot, Genre, Released, Rated, 
 *  ImdbRating) using OMDB API
 * 
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class CrawlData_ExternalFactors {
	
	//crawls External factors for all the movies in each genre
	
	public static void getExtractFactors(String filePath, String outFilePath) {
		
		URL url = null;
		Scanner sc = null;
		String apiurl = "http://www.omdbapi.com/";
		String moviename = null;
		String dataurl = null;
		String retdata = null;
		InputStream is = null;
		DataInputStream dis = null;

		try {

			String sCurrentLine;

			BufferedReader br = new BufferedReader(
					new FileReader(
							filePath));
			FileWriter fw = new FileWriter(outFilePath);
			BufferedWriter bw = new BufferedWriter(fw);
			int counter = 1;
			int counter1 = 1;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.trim().length() > 0) {

					moviename = sCurrentLine.trim().replace(" ", "+");
					dataurl = apiurl + "?t=" + moviename;
					url = new URL(dataurl);

					is = url.openStream();
					dis = new DataInputStream(is);
					
					String details[];
					Map<String, String> movieMap = new HashMap<String, String>();
					
					while ((retdata = dis.readLine()) != null) {
					
						
						if (!retdata.contains("Movie not found!")) {
							retdata = retdata.replace("\\\"", "");

							String[] list = retdata.split("\",");

							
							for (int i = 0; i < list.length; i++) {
								

								list[i] = list[i].replaceAll("\"", "");
								list[i] = list[i].replaceAll("\\{", "");
								list[i] = list[i].replaceAll("\\}", "");
								movieMap.put(list[i].split(":")[0],
										list[i].split(":")[1]);
								if (list[i].contains("imdbID")) {
									String str1 = list[i];
									str1 = str1.split(":")[1];
									str1 = str1.replaceAll("\"", "");
									
								}
							}

							bw.write("Title:" + movieMap.get("Title") + "$"
									+ "Metascore:" + movieMap.get("Metascore")
									+ "$" + "Actors:" + movieMap.get("Actors")
									+ "$" + "Director:"
									+ movieMap.get("Director") + "$"
									+ "Writer:" + movieMap.get("Writer") + "$"
									+ "Plot:" + movieMap.get("Plot") + "$"
									+ "Genre:" + movieMap.get("Action") + "$"
									+ "Released:" + movieMap.get("Released")
									+ "$" + "Rated:" + movieMap.get("Rated")
									+ "$" + "imdbRating:"
									+ movieMap.get("imdbRating"));

							bw.newLine();

						}

					}

				}
			}

			bw.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			try {

				if (dis != null) {
					dis.close();
				}

				if (is != null) {
					is.close();
				}

				if (sc != null) {
					sc.close();
				}
			} catch (Exception e2) {
				;
			}
		}


	}

	
	public static void main(String[] str) {

		//Action
		getExtractFactors("./MoviesList/ActionMovies.txt", "./ExternalFactorsData/ExtFact_ActionMovies.txt");
		//Adventure
		getExtractFactors("./MoviesList/AdventureMovies.txt", "./ExternalFactorsData/ExtFact_AdventureMovies.txt");
		//Comedy
		getExtractFactors("./MoviesList/ComedyMovies.txt", "./ExternalFactorsData/ExtFact_ComedyMovies.txt");
		//Drama 
		getExtractFactors("./MoviesList/Drama.txt", "./ExternalFactorsData/ExtFact_Drama.txt");
		//Fantasy
		getExtractFactors("./MoviesList/Fantasy.txt", "./ExternalFactorsData/ExtFact_Fantasy.txt");
		//Horror
		getExtractFactors("./MoviesList/Horror.txt", "./ExternalFactorsData/ExtFact_Horror.txt");
		//Sci_Fci
		getExtractFactors("./MoviesList/Sci_FciMovies.txt", "./ExternalFactorsData/ExtFact_Sci_FciMovies.txt");
		//Thriller
		getExtractFactors("./MoviesList/Thriller.txt", "./ExternalFactorsData/ExtFact_Thriller.txt");
		
	}
}
