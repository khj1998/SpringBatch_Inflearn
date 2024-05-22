package springbatch_inflearn.job_parameter;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job BatchJob() {
        return new JobBuilder("job",jobRepository)
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public JobParameters jobParameters() {
        return new JobParametersBuilder()
                .addString("name","user1")
                .toJobParameters();
    }

    private Step step1() {
        return new StepBuilder("step1",jobRepository)
                .tasklet(tasklet1(),platformTransactionManager)
                .build();
    }

    private Step step2() {
        return new StepBuilder("step2",jobRepository)
                .tasklet(tasklet2(),platformTransactionManager)
                .build();
    }

    /**
     * StepContribution -> StepExecution -> JobExecution -> JobParameters 과정 참조
     * ChunkContext -> StepContext -> StepExecution -> JobExecution -> JobParameters 과정 참조
     */
    private Tasklet tasklet1() {
        return ((contribution, chunkContext) -> {
            /**
             * StepContribution과 ChunkContext -> StepContext 순으로 JobParameters를 구하는 차이
             * 전자는 JobParameters를 반환
             * 후자는 Map<String,Object> 타입을 반환
             */

            JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
            jobParameters.getString("name");
            jobParameters.getLong("seq");
            jobParameters.getDate("date");
            jobParameters.getDouble("age");

            Map<String,Object> jobParams = chunkContext.getStepContext().getJobParameters();

            log.info("step 1 has been executed");
            return RepeatStatus.FINISHED;
        });

                
    }

    private Tasklet tasklet2() {
        return ((contribution, chunkContext) -> {
            log.info("step 2 has been executed");
            return RepeatStatus.FINISHED;
        });
    }
}
