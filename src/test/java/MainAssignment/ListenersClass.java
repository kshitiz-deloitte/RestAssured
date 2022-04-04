package MainAssignment;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenersClass implements ITestListener {
    protected static ExtentReports extentReports;
    protected static Logger log;
    protected static ExtentSparkReporter sparkReporter;
    @Override
    public void onTestStart(ITestResult result) {

        sparkReporter = new ExtentSparkReporter(result.getInstanceName()+".html");
        extentReports.attachReporter(sparkReporter);
        log = LogManager.getLogger(result.getInstanceName());
        ITestListener.super.onTestStart(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info(result.getName() + ": Succeeded");
        ExtentTest test = extentReports.createTest(result.getName());
        test.log(Status.PASS, "Success");
        test.pass(MediaEntityBuilder.createScreenCaptureFromPath("img.png").build());
        test.pass(MediaEntityBuilder.createScreenCaptureFromBase64String("base64").build());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.info(result.getName() + ": Failed");
        ExtentTest test = extentReports.createTest(result.getName());
        test.log(Status.FAIL, "Failure");
        test.fail(MediaEntityBuilder.createScreenCaptureFromPath("img.png").build());
        test.fail(MediaEntityBuilder.createScreenCaptureFromBase64String("base64").build());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ITestListener.super.onTestSkipped(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
    }

    @Override
    public void onStart(ITestContext context) {
        extentReports = new ExtentReports();
        ITestListener.super.onStart(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        extentReports.flush();
        ITestListener.super.onFinish(context);
    }
}
