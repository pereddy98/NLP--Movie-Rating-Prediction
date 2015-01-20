package com.sbu.cs.cse628.crawldata;
/*
 * This program is used to generate lynx commands to crawl movie scripts
 *  data from IMSDB site for movies in each genre
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExecuteShellComand {

	public static void main(String[] args) throws IOException {
		ExecuteShellComand obj = new ExecuteShellComand();
		BufferedReader br = new BufferedReader(new FileReader(
				"C:/Users/$rav$/Desktop/subjects/NLP/Inputdata/Thriller.txt"));
		FileWriter fw = new FileWriter(
				"C:/Users/$rav$/Desktop/subjects/NLP/Inputdata/Thrillerlynx.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		String sCurrentLine;
		int counter=1;
		while ((sCurrentLine = br.readLine()) != null) {

			if (sCurrentLine.trim().length() > 0) {
				sCurrentLine=sCurrentLine.trim();
				System.out.println(sCurrentLine);
				String movieName=sCurrentLine.replace(" ","-");
				String command = "lynx -crawl -dump http://www.imsdb.com/scripts/"+movieName+".html"
						+"  >"
						+"C:/Users/$rav$/Desktop/subjects/NLP/Inputdata/Thriller/"+counter+".txt";
				bw.write(command
						+System.getProperty("line.seperator"));
				System.out.println(command);
				counter++;
				
				
			}
		}
		bw.close();
		

	}

	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

}