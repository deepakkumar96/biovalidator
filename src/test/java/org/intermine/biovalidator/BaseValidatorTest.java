package org.intermine.biovalidator;

import org.intermine.biovalidator.api.Message;
import org.intermine.biovalidator.api.ValidationResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BaseValidatorTest
{
    protected void dumpErrors(ValidationResult result) {
        result.getErrorMessages().forEach(System.out::println);
        result.getWarningMessages().forEach(System.out::println);
    }

    protected String getFullPath(String filePath) {
        return "src/test/resources/" + filePath;
    }

    protected List<String> getErrorsListFromValidationResult(ValidationResult result) {
        return result.getErrorMessages()
                .stream()
                .map(Message::getMessage)
                .collect(Collectors.toList());
    }

    protected List<String> getWarningsListFromValidationResult(ValidationResult result) {
        return result.getWarningMessages()
                .stream()
                .map(Message::getMessage)
                .collect(Collectors.toList());
    }

    protected InputStreamReader createInMemoryInputStream(String data) {
        InputStream is = new ByteArrayInputStream(data.getBytes());
        return new InputStreamReader(is);
    }

    protected String path(String filename) {
        return "src/test/resources/fasta/" + filename;
    }

    /**
     * Run code with simple benchmark
     * @param codeToBeValidated code to be run
     */
    public static void simpleBenchmark(Runnable codeToBeValidated){
        long start = System.nanoTime();
        long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        codeToBeValidated.run();

        long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        long timeTaken = System.nanoTime() - start;
        System.out.println("Time Taken : " +
                TimeUnit.MILLISECONDS.convert((timeTaken), TimeUnit.NANOSECONDS));
        System.out.print("Memory Used : " + (afterUsedMem-beforeUsedMem));
    }
}
