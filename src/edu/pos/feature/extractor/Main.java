package edu.pos.feature.extractor;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


import edu.commons.aider.sql.DBAider;
import edu.commons.aider.sql.DataSet;
import edu.commons.aider.sql.SettingsLoader;
import edu.pos.feature.FeatureItemSet;
import edu.pos.feature.store.Store;
import edu.pos.feature.user.User;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

//left3words-wsj-0-18.tagger.

public class Main
{
	 private static Logger logger 	= Logger.getLogger(Main.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		if(args.length != 1)
			throw new IllegalArgumentException("pass only the store id as the argument");
		
		String storeID = args[0];
		Set<String> recommenedUser = new HashSet<String>();
		
		BasicConfigurator.configure();
		
		SettingsLoader settings = new SettingsLoader("app.settings");
		DBAider.init(settings);
		
		Store store = new Store(storeID);
		Set<FeatureItemSet> storeFeature = store.getFeatureForStoreFromDB();
		logger.debug("**********" + storeFeature);
		

		
		
		// new users set
		String query2 = "select reviewer_id from review where rating=? and store_id <> ? group by (reviewer_id) having count(reviewer_id)>7";
		DataSet ds2  = DBAider.read(query2, "five",storeID);
		int temp2 = ds2.rowSize();
		
		for(int j = 0 ; j < temp2 ; j++ )
		{
			String toCheckUser = ds2.getValue(j, "reviewer_id");
			User user = new User(toCheckUser);
			
			if(user.probableCustomer(storeFeature, 2))
			{
				recommenedUser.add(toCheckUser);
			}
		}
		
		logger.debug(recommenedUser.toString());
		
		
		
		
	}

}



//String query1 = "select distinct reviewer_id from review where store_id=?";
//DataSet ds1  = DBAider.read(query1, siteID);
//int temp = ds1.rowSize();
//
//
//for(int i = 0 ; i < temp ; i++)
//{
//	storeUsers.add(ds1.getValue(i, "reviewer_id"));
//}
