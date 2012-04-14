package edu.pos.feature.extractor;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

//left3words-wsj-0-18.tagger.

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		Tagger tagger =  new Tagger();
		Set<String> features = new HashSet<String>();
		tagger.getFeatures(features, "1207, 1205, 83, 2006-12-27, five, Very Satisfied -- I have ordered many, many ink cartridges from 123inkjets.com over the past 3 years for 3 different Epson printers and have always had excellent service. The product has consistently been as good or better than OEM at up to 70% below retail cost. Shiping is always fast. Communication is always prompt. Their customer service people are always friendly and will do whatever it takes to satisfy. And, what is amazing, they regularly offer big discounts that mean big discounts from their already rock-bottom prices. Also, they carry a larger inventory than any similar company. I've done business with similar companies and can state from experience that 123inkjets is by far the best in the business., , 292470, zero, false, false");
		
		System.out.println(features);

	}

}
