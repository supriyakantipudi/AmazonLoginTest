package uiAutomation;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.Augmenter;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SignInPage {

	public static WebDriver driver;
	
	LocalStorage localStorage;
	SignInPageResources resources = new SignInPageResources();

	public SignInPage() {
		super();
	}

	@BeforeMethod
	public void openHomePage() {

		System.setProperty("webdriver.chrome.driver", "C:\\Users\\supriya.kantipudi\\Downloads\\chromedriver.exe");
		driver = new ChromeDriver();
		
		WebStorage webStorage = (WebStorage) new Augmenter().augment(driver);
		localStorage = webStorage.getLocalStorage();
		
		//Launch home page and maximize window
		driver.get("https://amazon.in");
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	}

	@Test
	public void signInAndVerifyDashboard() throws InterruptedException {
		
        //Login and verify Dashboard
		signIn();
		validateDashboard();
		
		validateLocalStorageValues();
		
        //logout
		logout();
		
		//Load 404 page and validate
		getResponseCode();
		try {
			this.captureSnapShot(driver, new SignInPageResources().fileWithPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	private void signIn() throws InterruptedException {
		
		driver.findElement(By.id(resources.accountsListLocator)).click();
		Thread.sleep(2000);
		
		driver.findElement(By.id(resources.emailLocator)).sendKeys(resources.emailValue);
		localStorage.setItem("email", resources.emailValue);
		driver.findElement(By.id("continue")).click();
		Thread.sleep(2000);
		
		driver.findElement(By.id(resources.passwordLocator)).sendKeys(resources.passwordValue);
		localStorage.setItem("password", resources.passwordValue);
		driver.findElement(By.id(resources.signInButtonLocator)).click();
		
	}
	
	private void validateLocalStorageValues() {
		
		Assert.assertEquals(resources.emailValue, localStorage.getItem("email"));
		Assert.assertEquals(resources.passwordValue, localStorage.getItem("password"));;
	}

	private void validateDashboard() {
		Assert.assertTrue(driver.findElement(By.id(new SignInPageResources().dashboardLocator)).isDisplayed());
	}

	private void getResponseCode() {
		URL url;
		try {
			url = new URL("https://www.amazon.in/401");
			driver.get("https://www.amazon.in/401");
			Thread.sleep(2000);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			Assert.assertEquals(httpURLConnection.getResponseCode(), 404);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void logout() {
		driver.get(new SignInPageResources().logoutUrl);
	}

	public void captureSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {
		
		
		// To take TakeScreenshot
		TakesScreenshot scrShot = ((TakesScreenshot) webdriver);

		// create image file
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);

		// Move image file to new destination
		File DestFile = new File(fileWithPath);

		// Copy file at destination
		FileUtils.copyFile(SrcFile, DestFile);

	}

	@AfterMethod
	public void tearDown() {
		driver.quit();
	}

}
