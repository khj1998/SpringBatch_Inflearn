package springbatch_inflearn.jobconfig;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CustomExitStatus implements StepExecutionListener {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        String exitCode = stepExecution.getExitStatus().getExitCode();

        if (exitCode.equals(ExitStatus.COMPLETED.getExitCode())) {
            return new ExitStatus("TEST_PASSED");
        }

        if (exitCode.equals(ExitStatus.STOPPED.getExitCode())) {
            return new ExitStatus("TEST_STOPPED");
        }

        return new ExitStatus("TEST_FAILED");
    }
}
