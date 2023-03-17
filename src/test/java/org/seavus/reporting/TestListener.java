package org.seavus.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final ExtentReports extent = ExtentManager.getInstance();

    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getTestClass().getName() + "::" + result.getMethod().getMethodName());
        extentTest.set(test);
    }

    public void onTestSuccess(ITestResult result) {
        Markup markup = MarkupHelper.createLabel(String.format("<b>Test method %s successful</b>", result.getMethod().getMethodName()), ExtentColor.GREEN);

        extentTest.get().log(Status.PASS, markup);
    }

    public void onTestFailure(ITestResult result) {
        String exceptionMessage = result.getThrowable().getMessage();
        String trace = String.format("<details><summary><b><font color=red>Exception occurred, click to see details</font></b></summary>%s</details> \n", exceptionMessage.replaceAll(",", "<br>"));

        extentTest.get().fail(trace);

        Markup markup = MarkupHelper.createLabel(String.format("<b>Test method %s failed</b>", result.getMethod().getMethodName()), ExtentColor.RED);

        extentTest.get().log(Status.FAIL, markup);
    }

    public void onTestSkipped(ITestResult result) {
        Markup markup = MarkupHelper.createLabel(String.format("<b>Test method %s skipped</b>", result.getMethod().getMethodName()), ExtentColor.YELLOW);
        extentTest.get().log(Status.SKIP, markup);
    }

    public void onTestFailedWithTimeout(ITestResult result) {
        this.onTestFailure(result);
    }

    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }
}
