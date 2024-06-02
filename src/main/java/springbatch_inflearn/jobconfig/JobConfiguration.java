package springbatch_inflearn.jobconfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import springbatch_inflearn.Incrementer.CustomJobParametersIncrementer;
import springbatch_inflearn.validationconfig.CustomJobParametersValidator;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobExecutionListener jobExecutionListener;

    @Bean
    public Job job() {
        log.info("PlatformTransactionManager Class Name : "+platformTransactionManager.getClass().getName());
        log.info("JobRepository Class Name : "+jobRepository.getClass().getName());

        return new JobBuilder("repository_test",jobRepository)
                .start(step())
                .next(step2())
                .listener(jobExecutionListener)
                .incrementer(new CustomJobParametersIncrementer())
                //.incrementer(new RunIdIncrementer()) // 스프링에서 제공하는 파라미터 id 증가 클래스.
                //.preventRestart()
                .build();
    }

    /*@Bean
    public Job job2() {
        return new JobBuilder("repository_test2",jobRepository)
                .start(flow())
                .next(step5())
                .end()
                .build();
    }*/

    private Flow flow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");

        return flowBuilder.start(step3())
                .next(step4())
                .end();
    }

    private Step step() {
        return new StepBuilder("start-step",jobRepository)
                .tasklet(tasklet(),platformTransactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    private Step step2() {
        return new StepBuilder("step2",jobRepository)
                .tasklet(tasklet2(),platformTransactionManager)
                .startLimit(3)
                .build();
    }

    private Step step3() {
        return new StepBuilder("start-step",jobRepository)
                //.tasklet(new CustomTasklet(),platformTransactionManager)
                .tasklet(tasklet(),platformTransactionManager)
                .build();
    }

    private Step step4() {
        return new StepBuilder("step2",jobRepository)
                .tasklet(tasklet2(),platformTransactionManager)
                .build();
    }

    private Step step5() {
        return new StepBuilder("step2",jobRepository)
                .tasklet(tasklet3(),platformTransactionManager)
                .build();
    }

    private Tasklet tasklet() {
        return ((contribution, chunkContext) -> {
            log.info("step1 - tasklet1 수행");
            Thread.sleep(3000);
            return RepeatStatus.FINISHED;
        });
    }

    private Tasklet tasklet2() {
        return ((contribution, chunkContext) -> {
            log.info("step2 - tasklet2 수행");
            throw new RuntimeException("step2 failed!");
            //return RepeatStatus.FINISHED;
        });
    }

    private Tasklet tasklet3() {
        return ((contribution, chunkContext) -> {
            log.info("step5 - flow job 수행");
            return RepeatStatus.FINISHED;
        });
    }
}
