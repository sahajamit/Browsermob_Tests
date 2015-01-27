package com.browsermob.server;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;
/**
 * Main Proxy class that starts the BrowserMob Proxy Server.
 */
public class BrowserMobProxyServer{

	ProxyServer server;
	AnalyseHAR analyseHar;
	int port;
	public BrowserMobProxyServer(){
		this.port = 8019;
	}
	public BrowserMobProxyServer(int port){
		this.port = port;
	}
	
	public ProxyServer startBMPServer() {
		/**
		 * Creates an instance of ProxyServer
		 */
		server=new ProxyServer(port);
        
		try {
			/*
			 * Starts the Proxy Server at port 8091
			 */
			server.start();
			/**
			 * Captures the headers and other body content. This is mandatory.
			 */
	        server.setCaptureHeaders(true);
	        server.setCaptureContent(true);
	       /**
	        * Defining the custom interceptor class that intercepts BMP Request as well as BMP Response.
	        */
	        RequestResponseInterceptor interceptor=new RequestResponseInterceptor();
	        server.addRequestInterceptor(interceptor);
	        server.addResponseInterceptor(interceptor);
	        /**
//	         * Proxy Server can rewrite any Request. 
	         */
//	        server.rewriteUrl("http://*.*", "http://www.google.com");
	        /**
	         * Proxy Server blacklists any request which is for HTTP protocol.
	         */
//	         server.blacklistRequests(".*http.*", 404);
	        analyseHar=new AnalyseHAR();
	       
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return server;
	}
	
	private void startWebDriver(){
		Proxy proxy=null;
		/**
		 * Creates an instance of Proxy on the machine
		 */
		try {
			proxy = new Proxy(); 
			/**
			 * Proxy is configured for HTTP as well as HTTPS request. This is mandatory. 
			 * Other than this, the proxy will not be able to intercept HTTP as well as HTTPS requests.
			 */
			proxy.setHttpProxy("localhost:8091");
			proxy.setSslProxy("localhost:8091");
	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception while retrieving the proxy for selenium");
			stopServer();
		}
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(CapabilityType.PROXY,proxy);
		
		/**
		 * Firefox Driver is configured to automate the Firefox browser. 
		 * No configuration for Firefox Profile is required here, even for HTTPS connections.
		 */
		WebDriver driver=new FirefoxDriver(capabilities);
		
		//FirefoxProfile profile=new FirefoxProfile();
		//capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		//profile.setAcceptUntrustedCertificates(true);
		//profile.setAssumeUntrustedCertificateIssuer(true);
		//profile.setProxyPreferences(proxy);
		/**
		 * Creating the Har file to be populated by BMP.
		 */
        server.newHar("Git");
        System.out.println("Git Page is loading…");
        driver.get("https://github.com");	
        /**
         * Ending the page 'Git' in the HAR.
         */
        server.endPage();
        
        /**
         * Defining new page in the HAR.
         */
        server.newPage("Git_SignIn");
        clickGitSignInButton(driver);
        System.out.println("Clicked the SignIn Button");
        /**
         * Waiting for network communication to stop.
         */
        server.waitForNetworkTrafficToStop(500, 50000);
        server.endPage();
      
        System.out.println("Page has loaded. Filling form now");
        server.newPage("Git_Form_Submit");
        gitLogin(driver);
        System.out.println("Submitted form.");
        server.endPage();
        
        /**
         * Saving the HAR file.
         */
        Har harfile=server.getHar();
        analyseHar.saveHarFile("D:/HarFiles/Git_click.har", harfile);
        
        analyseHar.investigateHar(harfile);
		
        driver.quit();
        try {
			server.stop();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Exception while stopping the proxy server");
		}
	}
	
	/**
	 * Automates click action on 'SIGN IN' button on github.com web page.
	 * @param driver
	 */
	private void clickGitSignInButton(WebDriver driver){
		WebElement buttonEle =null;
        try{
        	buttonEle=driver.findElement(By.cssSelector("a.button.signin"));
        	
        }catch(NoSuchElementException e){
        	e.printStackTrace();
        	System.out.println("Not able to find the element. Quit");
        	stopServer();
			return;
        }
        buttonEle.click();	
	}
	/**
	 * Automates filling up of Sign In form on github.com
	 * @param driver
	 */
	private void gitLogin(WebDriver driver){
		
		WebElement buttonEle =null;
		WebElement usernameEle=null;
		WebElement passwordEle=null;
        try{
        	usernameEle=driver.findElement(By.name("login"));
        	passwordEle=driver.findElement(By.name("password"));
        	buttonEle=driver.findElement(By.name("commit"));
        }catch(NoSuchElementException e){
        	e.printStackTrace();
        	System.out.println("Not able to find the element. Quit");
        	stopServer();
			return;
        }
        usernameEle.sendKeys("abcd");
        passwordEle.sendKeys("abcd");
        buttonEle.click();
	}
	/**
	 * Stops the Browsermobproxy server and ends all open connections.
	 */
	public void stopServer(){
		try {
			server.stop();
			server=null;
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Exception while stopping the proxy server");
		}
	}
	
	/**
	 * Automates Login action on https://irctc.co.in
	 */
	private void buttonClick(WebDriver driver) {
		WebElement usernameEle =null;
		WebElement passwordEle =null;
		WebElement buttonEle =null;
		try{
			usernameEle=driver.findElement(By.name("userName"));
			passwordEle=driver.findElement(By.name("password"));
			buttonEle=driver.findElement(By.name("button"));
		}catch(NoSuchElementException e){
			e.printStackTrace();
			System.out.println("Not able to find the element. Quit");
			stopServer();
			return;
		}
		usernameEle.sendKeys("abcd");
		passwordEle.sendKeys("irctc");
		buttonEle.submit();
	}
	
}
