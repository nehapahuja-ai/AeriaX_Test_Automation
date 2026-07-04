package listeners;

import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;

public class TestListener implements ITestListener, IExecutionListener {
    @Override
    public void onTestFailure(ITestResult result) {
        result.setAttribute("failureMessage", result.getThrowable() == null ? "" : result.getThrowable().getMessage());
    }

    @Override
    public void onExecutionFinish() {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "allure", "generate", "target/allure-results", "-o", "target/allure-report", "--clean"
        );
        processBuilder.inheritIO();

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("Allure report generation failed with exit code " + exitCode);
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Allure CLI was not found or could not be started. Install Allure CLI to generate HTML automatically.",
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Allure report generation was interrupted.", exception);
        }
    }
}
