package edu.pos.feature.extractor;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagger
{
	final static  String[] desiredTags = {"NN","NNS","NNP","NNPS"};
	final static String[] desiredProperty = {"JJ","JJR","JJS"};
	final int DistanceSize = 5;

	static Set<String> desiredTagSet = new HashSet<String>();
	static Set<String> desiredPropertySet = new HashSet<String>();

	private static MaxentTagger tagger;

	static
	{
		Collections.addAll(desiredTagSet, desiredTags);
		Collections.addAll(desiredPropertySet, desiredProperty);

		try
		{
			tagger = new MaxentTagger("models/wsj-0-18-left3words.tagger");
		}
		catch (Exception e)
		{

		}
	}


	public void  getFeatures(Set<String> features, String contentText)
	{

		List<List<HasWord>> sentences = tagger.tokenizeText(new BufferedReader(new StringReader(contentText)));

		for (List<HasWord> sentence : sentences)
		{
			ArrayList<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
			extractDesiredTags(taggedSentence,features);
		}

	}


	private void extractDesiredTags(ArrayList<TaggedWord> taggedSentence , Set<String> features)
	{		
		int size = taggedSentence.size();

		for( int i = 0 ; i < size ; i++)
		{
			TaggedWord word = taggedSentence.get(i);

			if( desiredTagSet.contains( word.tag() ) )
			{

				if ( checkifPositive(taggedSentence, i, size))
				{
					features.add(word.value());
				}

			}

		}
	}

	private boolean checkifPositive(ArrayList<TaggedWord> tSentence,int index, int size)
	{
		boolean isPositive = false;
		int j , endPoint;

		if ( index-DistanceSize < 0 )
		{
			j = 0 ;
		}
		else
		{
			j = index-DistanceSize;
		}

		if( index+DistanceSize>=size )
		{
			endPoint = size;
		}
		else
		{
			endPoint = index+DistanceSize;
		}

		for ( ; j < endPoint ; j++)
		{
			TaggedWord word = tSentence.get(j);

			if( desiredPropertySet.contains( word.tag() ) )
			{
				isPositive = PositiveFeatureExtractor.isPositive(word.value());

				if(isPositive)
					break;

			}


		}


		return isPositive;
	}
}
