package edu.pos.feature.extractor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PositiveFeatureExtractor
{

	private static final String fileLocation = "wordlist/positive-words.txt";



	private static List<String> listOfWords = new ArrayList<String>();

	static
	{
		try
		{
			String word = null;
			BufferedReader br = new BufferedReader(new FileReader(new File(fileLocation)));
			
			while ( (word = br.readLine()) != null )
			{
				listOfWords.add(word);
			}
			
			Collections.sort(listOfWords);
		}

		catch(Exception c)
		{

		}
	}

	public static boolean isPositive(String adj)
	{
		return Collections.binarySearch(listOfWords, adj)<0?false:true ;
	}


}
