package springbatch_inflearn.JobRunnerTest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
public class JobInstanceConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job job() {
        return new JobBuilder("job_instance",jobRepository)
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

    public Step step1() {
        return new StepBuilder("step1",jobRepository)
                .tasklet(tasklet1(),platformTransactionManager)
                .build();
    }

    public Step step2() {
        return new StepBuilder("step2",jobRepository)
                .tasklet(tasklet2(),platformTransactionManager)
                .build();
    }

    private Tasklet tasklet1() {
        return ((contribution, chunkContext) -> {
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
