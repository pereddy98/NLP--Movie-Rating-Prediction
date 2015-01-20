package com.sbu.cs.cse628.crawldata;
/*
 * This program is used to assign labels to script data
 * this is used as part of Dataset collection
 * Running this program will give exceptions as some of folders are missing in this package
 * The output of these program is files having movie scripts and movies label
 * the script data we used is in Script data folder in this package
 * 
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class CrawlScriptsData {

	@SuppressWarnings("resource")
	public static void main(String[] str) {

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
							"C:/Users/$rav$/Desktop/subjects/NLP/Inputdata/Thriller.txt"));
			int counter=1;
			int counter1=1;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.trim().length() > 0) {
					
					moviename = sCurrentLine.trim().replace(" ", "+");
					dataurl = apiurl + "?t=" + moviename;
					url = new URL(dataurl);

					is = url.openStream();
					dis = new DataInputStream(is);
					
					String details[];
					Map<String,String> movieMap=new HashMap<String,String>();
				
					while ((retdata = dis.readLine()) != null) {
					
						if(!retdata.contains("Movie not found!")){
							retdata=retdata.replace("\\\"", "");
						
						String[] list = retdata.split("\",");
					
					
						for (int i = 0; i < list.length; i++) {
							
							list[i] = list[i].replaceAll(""
									+ "\"", "");
							list[i] = list[i].replaceAll("\\{", "");
							list[i] = list[i].replaceAll("\\}", "");
							System.out.println(list[i]);
							movieMap.put(list[i].split(":")[0], list[i].split(":")[1]);
							if (list[i].contains("imdbID")) {
								String str1 = list[i];
								str1 = str1.split(":")[1];
								str1 = str1.replaceAll("\"", "");
								
							}
						}
						FileWriter fw = new FileWriter(
								"C:/Users/$rav$/Desktop/subjects/NLP/Inputdata/Thriller111/"+counter1+".txt");
						BufferedWriter bw = new BufferedWriter(fw);
						System.out.println("counter is:"+counter);
						String content;
						
						if(new Scanner(new File(
								"C:/Users/$rav$/Desktop/subjects/NLP/Inputdata/Thriller/"+counter+".txt")).hasNext()){
						 content = new Scanner(new File(
								"C:/Users/$rav$/Desktop/subjects/NLP/Inputdata/Thriller/"+counter+".txt")).useDelimiter(
										"\\Z").next();}
						else{
							content=" ";
						}
						
						counter++;
						counter1++;
						
						System.out.println(content.indexOf("ALL SCRIPTS")+12);
					
						String script;
						if(content.contains("ALL SCRIPTS") && content.contains("Writers :")){
						 script = content.substring(content.indexOf("ALL SCRIPTS")+12,content.indexOf("Writers :"));
							
						}else{
							script=" ";
							
						}
						
						bw.write("imdbRating:"+movieMap.get("imdbRating"));
						bw.newLine();
						
						bw.write("Title:"+movieMap.get("Title"));
						bw.newLine();
						bw.write("Metascore:"+movieMap.get("Metascore"));
						bw.newLine();
						bw.write("Actors:"+movieMap.get("Actors"));
						bw.newLine();
						bw.write("Director:"+movieMap.get("Director"));
						bw.newLine();
						bw.write("Writer:"+movieMap.get("Writer"));
						bw.newLine();
						bw.write("Plot:"+movieMap.get("Plot"));
						bw.newLine();
						bw.write("Genre:"+movieMap.get("Action"));
						bw.newLine();
						bw.write("Released:"+movieMap.get("Released"));
						bw.newLine();
						bw.write("Rated:"+movieMap.get("Rated"));
						bw.newLine();
						bw.write(script);
						
						bw.close();

					}
					
					
					}
				}
			}
		

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
}
