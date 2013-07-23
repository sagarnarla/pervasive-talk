package guestbook;

import java.io.IOException;
import javax.servlet.http.*;
import java.util.List;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.opentok.api.API_Config;
import com.opentok.api.OpenTokSDK;
import com.opentok.api.constants.SessionProperties;
import com.opentok.exception.OpenTokException;
import com.opentok.api.constants.RoleConstants;


public class PervasiveTalk {
	
	public String GetToken(String sessionid, String user){
		
		OpenTokSDK sdk = new OpenTokSDK(API_Config.API_KEY, API_Config.API_SECRET);
		String connectionMetadata = "username="+user;
		String token;
		try
		{
			token = sdk.generate_token(sessionid);
		}
		catch(OpenTokException ex)
		{
			return null;
		}
		
		
		return token;
	}
	
	public String GetSessionID(String url){
		
		//String user = req.getParameter("user");
		//String url = req.getParameter("url");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		//Key urlkey = KeyFactory.createKey("Conference", url); 
		//String urlkey = url;
		try
		{
			//Query for conference on that url
			//Entity conference = datastore.get(urlkey);
			Filter UrlFilter = new FilterPredicate("URL",FilterOperator.EQUAL,url);
			Query q = new Query("Conference").setFilter(UrlFilter);
			PreparedQuery pq = datastore.prepare(q);
			Entity result = pq.asSingleEntity();
			
			if(result == null)
			{
				//Create Conference for that URL
				Entity conference = new Entity("Conference");
				
				//Create a sessionid
				String sessionid;
				try
				{
					OpenTokSDK sdk = new OpenTokSDK(API_Config.API_KEY, API_Config.API_SECRET);
					sessionid = sdk.create_session().getSessionId();
				}
				catch(OpenTokException exp)
				{
					
					//System.out.println("Could not create opentok session.");
					return null;
				}
				
				//Push sessionid into db
				conference.setProperty("SessionId", sessionid);
				conference.setProperty("URL", url);
				datastore.put(conference);
				return sessionid;
			}
			else
			{
				return (String) result.getProperty("SessionId");
			}
									
		}
		catch(TooManyResultsException ex)
		{
			//Datastore inconsistent
			return null;		
			
		}
	

}

}
