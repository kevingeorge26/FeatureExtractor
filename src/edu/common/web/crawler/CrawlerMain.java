package edu.common.web.crawler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

import edu.pos.feature.store.Store;

public class CrawlerMain {

	static List<String> linktoCrawl = new ArrayList<String>();
	static Store store;
	static int totalReview =  0;

	public static void getLinkstoCrawl()
	{
		linktoCrawl.add("http://www.yelp.com/biz/chi-cafe-chicago?start=40");
		linktoCrawl.add("http://www.yelp.com/biz/chi-cafe-chicago?start=80");
		linktoCrawl.add("http://www.yelp.com/biz/chi-cafe-chicago?start=120");
		linktoCrawl.add("http://www.yelp.com/biz/chi-cafe-chicago?start=160");
		linktoCrawl.add("http://www.yelp.com/biz/chi-cafe-chicago?start=200");
	}


	public static void main(String[] args)
	{
		getLinkstoCrawl();
		store = new Store("yelp chi town");

		for( String link : linktoCrawl)
		{
			getReviews(link);
		}

		System.out.println("total review = "+totalReview+" ****"+ store.getAggregatedFeature(totalReview));
	}


	public static void getReviews(String link)
	{
		try
		{
			URL yahoo = new URL(link);

			HttpURLConnection yc = (HttpURLConnection) yahoo.openConnection();
			yc.setInstanceFollowRedirects(false);
			yc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux i686; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
			yc.connect();			

			if(yc.getResponseCode() == 200)
			{
				System.out.println("200");
				Parser parser = new Parser(yc);

				NodeList nodeList = parser.parse(new NodeFilter() {

					@Override
					public boolean accept(Node arg0)
					{
						if (arg0.getText().contains( "review_comment description ieSucks" ) )
							return true;
						else
							return false;
					}
				});

				totalReview += nodeList.size();

				for( int i = 0 ; i < nodeList.size() ; i ++)
					store.addReview(nodeList.elementAt(3).toPlainTextString());

			}

			else if(yc.getResponseCode() >= 400 && yc.getResponseCode() <=500)
			{
				System.out.println("there was a redirection from link ");

			}
			else if(yc.getResponseCode() >= 300 && yc.getResponseCode() <= 307)
			{				

				System.out.println("there was a redirection from link ");
			}



		}

		catch (Exception e)
		{
			System.out.println("exception "+ e);
		}

	}

}
