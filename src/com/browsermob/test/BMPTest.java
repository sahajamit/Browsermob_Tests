package com.browsermob.test;

import com.browsermob.server.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
/**
 * This class automates Android Chrome Browser directly through ChromeDriver. All the requests and response will be routed through Browsermob proxy server.
 * There is no Appium involved here. You need to set the path of the ChromeDriver.
 * Just run this class directly.
 *
 */
public class BMPTest {
	static DesiredCapabilities  capabilities;
	public static void main(String[] args) throws MalformedURLException{
		/**
		 * Starts the Browsermob proxy server.
		 */
		BrowserMobProxyServer test=new BrowserMobProxyServer();
		ProxyServer server=test.startBMPServer();
		/**
		 * Instantiate the class which analyzes HAR file.
		 */
		AnalyseHAR analyseHar=new AnalyseHAR();
		
		capabilities = DesiredCapabilities.chrome();
		/*Sets the proxy for Chrome browser. This works well with desktop chrome browser and android Emulators. 
		 * Hopefully this does not work with android device!	
		 */
		
//		options.addArguments("--proxy-server=localhost:8091");		//Change the IP address according to you.
//		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		
		/**
		 * Below two lines of code also does not work proxying on android. You can try with newer versions of ChromeDriver!
		 */
//		capabilities.setCapability("httpProxy","localhost:8091");
		setProxy();
		
		WebDriver driver = new ChromeDriver(capabilities);
//		WebDriver driver = new FirefoxDriver(capabilities);
		driver.manage().window().maximize();
		String url="";
//		
//		url="http://www.equinox.com/clubs";
//		driver.get(url);
		server.newHar("equinox");
		url="https://stag.equinox.com/login";
		url = "https://www.equinox.com/login";
		driver.get(url);
		
//		driver.navigate().refresh();
//		server.waitForNetworkTrafficToStop(100, 10000);
		
		/*Creating new HAR file and saving it in \Har directory.*/
		
		Har har=server.getHar();
		analyseHar.saveHarFile("equinoxHar.har", har);
		driver.close();
		driver.quit();
		test.stopServer();
	}
	private static void setProxy(){
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
			proxy.setAutodetect(false);
			proxy.setHttpProxy("localhost:8019");
			proxy.setSslProxy("localhost:8019");

			capabilities.setCapability(CapabilityType.PROXY,proxy);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception while setting the proxy for android");
		}
}

}
