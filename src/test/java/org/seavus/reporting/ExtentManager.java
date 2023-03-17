package org.seavus.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports instance;

    public synchronized static ExtentReports getInstance() {
        if (instance == null) {
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter("reports/extent-report.html");
            htmlReporter.config().setEncoding("utf-8");
            htmlReporter.config().setDocumentTitle("Casumo - Automation Reports");
            htmlReporter.config().setReportName("Casumo - Automation Reports");
            htmlReporter.config().setTheme(Theme.DARK);

            instance = new ExtentReports();
            instance.setSystemInfo("Organization", "Seavus");
            instance.attachReporter(htmlReporter);
        }

        return instance;
    }
}
