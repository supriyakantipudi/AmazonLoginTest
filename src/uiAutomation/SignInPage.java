package uiAutomation;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import uiAutomationResources.SignInPageResources;

public class SignInPage extends SignInPageResources {
	
	WebDriver driver;
	Wait<WebDriver> fluentWait;
	LocalStorage localStorage;
			
    @BeforeClass
	public void login()
	{

		System.setProperty("webdriver.chrome.driver", "../TestProject/resources/drivers/");
		WebDriver driver = new ChromeDriver();
		WebStorage webStorage = (WebStorage) new Augmenter().augment(driver);
		localStorage = webStorage.getLocalStorage();
	}

	@Test
	public void signInAndNavigateTo() {
		driver.manage().window().maximize();
		driver.navigate().to(pageUrlString);
		driver.manage().timeouts().pageLoadTimeout(10,TimeUnit.SECONDS);
		signIn();
		validateDashboard();
		try {
			this.captureSnapShot(driver, fileWithPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		validateLocalStorageValues();
		getResponseCode();
		logout();
		try {
			this.captureSnapShot(driver, fileWithPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void signIn() {
		driver.findElement(By.id(accountsListLocator)).click();
		driver.findElement(By.id(emailLocator)).sendKeys(emailValue);
		localStorage.setItem("email", emailValue);
		driver.findElement(By.id("continue")).click();
		driver.findElement(By.id(passwordLocator)).sendKeys(passwordValue);
		localStorage.setItem("password", passwordValue);
		driver.findElement(By.id(signInButtonLocator)).click();
	}
	
	private void validateDashboard() {
		driver.findElement(By.id(dashboardLocator)).isDisplayed();
	}
	
	private void validateLocalStorageValues() {
		Assert.assertEquals(emailValue, localStorage.getItem("email"));
		Assert.assertEquals(passwordValue, localStorage.getItem("password"));;
	}
	
	private void getResponseCode() {
		URL url;
		try {
			url = new URL("https://www.amazon.in/401");
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			System.out.println("httpURLConnection.getResponseCode():: "+httpURLConnection.getResponseCode());
			Assert.assertEquals(200, httpURLConnection.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void logout() {
		driver.get(logoutUrl);
	}
	
	public void captureSnapShot(WebDriver webdriver,String fileWithPath) throws Exception{

        //To take TakeScreenshot
        TakesScreenshot scrShot =((TakesScreenshot)webdriver);

        //create image file
        File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

        //Move image file to new destination
         File DestFile=new File(fileWithPath);

        //Copy file at destination
         FileUtils.copyFile(SrcFile, DestFile);

    }
	
	@AfterClass
	public void afterTest()
	{
		driver.quit();
	}
	
}
