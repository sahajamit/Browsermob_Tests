package com.browsermob.server;

import java.util.Enumeration;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.http.BrowserMobHttpRequest;
import net.lightbody.bmp.proxy.http.BrowserMobHttpResponse;
import net.lightbody.bmp.proxy.http.RequestInterceptor;
import net.lightbody.bmp.proxy.http.ResponseInterceptor;
import net.lightbody.bmp.proxy.jetty.http.HttpRequest;

/**
 * Interceptor class that watches every data while the request crosses the Proxy Server.
 */
public class RequestResponseInterceptor implements RequestInterceptor, ResponseInterceptor{

	int flag=1;
	@Override
	public void process(BrowserMobHttpRequest mobrequest, Har har) {
		
		/**
		 * Here we get access to net.lightbody.bmp.proxy.jetty.http.HttpRequest object and not org.apache.http.HttpRequest
		 */
		
		HttpRequest request= mobrequest.getProxyRequest();
		StringBuffer url=request.getRequestURL();
//		System.out.println("\nRequest URL="+url.toString());
		String user_agent=request.getField("User-Agent");
		if(url.toString().contains("stag")){
			mobrequest.getMethod().addHeader("Authorization", "Basic RGV2UHJldmlldzpFcXVpbm94MSE=");
//			request.addField("Authorization", "Basic RGV2UHJldmlldzpFcXVpbm94MSE=");
		}
		
//		System.out.println(user_agent);

		/**
		 * This prints all the header data encapsulated in the HTTPRequest. Example:
			Host = github.com
			User-Agent = Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0
			Accept = text/html,application/xhtml+xml,application/xml;q=0.9,**;q=0.8
			Accept-Language = en-US,en;q=0.5
			Accept-Encoding = gzip, deflate
			Connection = keep-alive
		 * 
		 */
		/*
		Enumeration<String> enumeration=request.getFieldNames();
		if(enumeration!=null)
			while(enumeration.hasMoreElements()){
				String fieldKey=enumeration.nextElement();
				String name=request.getField(fieldKey);
				System.out.println(fieldKey+" = "+name);
			}
		*/
		
		/**
		 * We can re-execute the Http Request and check the response received.
		 */
		/*
		if(flag==1){
			flag=flag+1; //This is mandatory other the execute method will cause indefinite loop.
		}
		else
			return;
		
		BrowserMobHttpResponse mobresponse=mobrequest.execute();
		org.apache.http.HttpResponse response=mobresponse.getRawResponse();
		System.out.println("Inner HttpResponse status="+response.getStatusLine());
		*/
	}
	@Override
	public void process(BrowserMobHttpResponse mobresponse, Har har) {
		/**
		 * We get access to this object org.apache.http.HttpResponse and not net.lightbody.bmp.proxy.jetty.http.HttpResponse.
		 */
		org.apache.http.HttpResponse response=mobresponse.getRawResponse();
//		if(response!=null && response.getStatusLine()!=null)
//			System.out.println("HttpResponse status="+response.getStatusLine()); //HttpResponse status=HTTP/1.1 200 OK
		/**
		 * You can also iterate on HarEntry extracted from BrowserMobHttpResponse
		 * or you can work with the second parameter of this method i.e. Har. 
		 */
		//HarEntry entry=mobresponse.getEntry();
	}
}
