package com.browsermob.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.HarPage;
import net.lightbody.bmp.core.har.HarPageTimings;
import net.lightbody.bmp.core.har.HarPostData;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.core.har.HarTimings;

public class AnalyseHAR {

	/**
	 * Saves the Har file at the directory path.
	 * @param path
	 * @param harfile
	 */
	public void saveHarFile(String filename, Har harfile){
		File classpathRoot = new File(System.getProperty("user.dir"));
		File file = new File(classpathRoot,"\\Har\\"+filename ); 		
		System.out.println("Path of File is="+file.getPath());
	
		try {
			FileOutputStream fos=new FileOutputStream(file);
			System.out.println("Harfile="+harfile);
			harfile.writeTo(fos);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Parses the HAR file and prints out all the information
	 * @param harfile
	 */
	public void investigateHar(Har harfile) {
		/*Root of Exported Data*/
		HarLog log=harfile.getLog();
		
		/*Comment information*/
		String comment=log.getComment();
		//System.out.println("1. Comment ="+comment);
		
		/*Creator information*/
		/*HarNameVersion creatorData=log.getCreator();
		String creator_comment=creatorData.getComment();
		String creator_name=creatorData.getName();
		String creator_version=creatorData.getVersion();*/
		
		/*System.out.println("2. Creator Data:-");
		System.out.println("Creator: name="+creator_name);
		System.out.println("Creator: comment="+creator_comment);
		System.out.println("Creator: version="+creator_version);*/
		
		/*Browser information*/	
		/*HarNameVersion browserData=log.getBrowser();
		String browser_name=browserData.getName();
		String browser_comment=browserData.getComment();
		String browser_version=browserData.getVersion();*/
		
		/*System.out.println("3. Browser Data:");
		System.out.println("Browser: name="+browser_name);
		System.out.println("Browser: comment="+browser_comment);
		System.out.println("Browser: version="+browser_version);*/
		
		/*Entries information*/
		List<HarEntry> listEntries=log.getEntries();
		
		for(HarEntry entry:listEntries){
			long time=entry.getTime();
			String pageRef=entry.getPageref();
			
			HarTimings timings=entry.getTimings();
			long blocked=timings.getBlocked();
			long connect=timings.getConnect();
			long dns=timings.getDns();
			long recieve=timings.getReceive();
			long send= timings.getSend();
			long ssl=timings.getSsl();
			long wait=timings.getWait();
			long sum=connect+dns+recieve+send+ssl+wait;
			
			String connection=			entry.getConnection();
			String pageref=			entry.getPageref();
			String serverIP=			entry.getServerIPAddress();
			
			HarRequest request=			entry.getRequest();
			String requestURL=			request.getUrl();
			
			System.out.println("\nRequest URL: "+requestURL);
			
			List<HarNameValuePair> queryString=			request.getQueryString();
			HarPostData postData=			request.getPostData();

			String postDataText=null;
			if(postData!=null)
				postDataText=			postData.getText();
		
			//System.out.println("Connection="+connection+"  PageRef="+pageref+"   serverIP="+serverIP+"   post data="+postDataText);
			System.out.println("PageRef= "+pageRef);

			if(queryString.size()>0){
				System.out.println("Request Query String:");
				for(HarNameValuePair pair: queryString){
					String name= pair.getName();
					String value= pair.getValue();
				
					System.out.println("Name= "+name+"  Value= "+value);
				}
			}
			/*You can dig in to Response Object also*/
			HarResponse response=			entry.getResponse();
			int status=response.getStatus();
			//System.out.println("Time="+time+"  Sum of timings tag="+sum+  " (Except Blocked time="+blocked+")");
			System.out.println("Time="+time);
			System.out.println("Response code="+status);
		}
		/*Pages Information*/
		List<HarPage> harPage=log.getPages();
		for(HarPage page: harPage){
			
			String id=page.getId();
			String title=page.getTitle();
			Date date=page.getStartedDateTime();
			
			System.out.println("**********************************************************************");
			
			HarPageTimings timing=page.getPageTimings();
			if(timing!=null){
				
				//long onContentload=timing.getOnContentLoad();
				long onload=timing.getOnLoad();
				System.out.println("PageTiming Data: Title="+title+"   Id="+id+"   onLoad="+onload+"   Date="+date);
			}
		}
	}
}
