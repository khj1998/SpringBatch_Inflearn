package springbatch_inflearn.configuration.JobConfiguration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
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
public class HelloJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job helloJob(JobRepository jobRepository) {
        return new JobBuilder("helloJob",jobRepository)
                .start(helloStep1())
                .next(helloStep2())
                .build();
    }

    /**
     * step에서는 기본적으로 tasklet이 무한 반복된다.
     * 이를 방지하기 위해서는 다음 방법이 있다.
     * RepeatStatus.FINISHED = 한번만 실행하고 종료
     */
    public Step helloStep1() {
        return new StepBuilder("helloStep1",jobRepository)
                .tasklet(myTasklet1(),platformTransactionManager)
                .build();
    }

    public Step helloStep2() {
        return new StepBuilder("helloStep2",jobRepository)
                .tasklet(myTasklet2(),platformTransactionManager)
                .build();
    }

    private Tasklet myTasklet1() {
        return (((contribution, chunkContext) -> {
            log.info("=======================");
            log.info(">>> Hello Spring Batch!");
            log.info("=======================");
            return RepeatStatus.FINISHED;
        }));
    }

    private Tasklet myTasklet2() {
        return (((contribution, chunkContext) -> {
            log.info("=======================");
            log.info(">>> Hello Spring Batch2!");
            log.info("=======================");
            return RepeatStatus.FINISHED;
        }));
    }
}
