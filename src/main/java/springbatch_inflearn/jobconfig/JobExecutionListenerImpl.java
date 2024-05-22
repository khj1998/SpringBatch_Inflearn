package springbatch_inflearn.jobconfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutionListenerImpl implements JobExecutionListener {
    private final JobRepository jobRepository;

    /**
     * 스프링에서 제공하는 repository 커스텀 테스트on}
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
    }

    /**
     * jonName, jobParameters 값으로 마지막으로 저장된 JobExecution을 가져온다.
     * 마지막 실행된 job의 step 메타데이터들을 출력
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate","20240522").toJobParameters();

        JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName,jobParameters);

        if (lastJobExecution != null) {
            for (StepExecution stepExecution : lastJobExecution.getStepExecutions()) {
                BatchStatus status = stepExecution.getStatus();
                log.info("status = " + status);

                ExitStatus exitStatus = stepExecution.getExitStatus();
                log.info("exitStatus = "+exitStatus.getExitCode());

                String stepName = stepExecution.getStepName();
                log.info("stepName = " + stepName);
            }
        }
    }
}
