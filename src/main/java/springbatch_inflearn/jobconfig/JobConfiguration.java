package springbatch_inflearn.jobconfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
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
                    //.start(jobStep(null)) // JobStep 정의
                    .on("TEST_PASSED")
                    .to(mainJobStep())
                    .next(lastStep())
                    .on("COMPLETED")
                    .stop()
                .from(step())
                    .on("TEST_FAILED")
                    .to(failedFlowStep())
                    .next(lastStep())
                    .on("COMPLETED")
                    .stop()
                .from(jobFlowStep())
                    .on("FAILED")
                    .to(failedFlowStep())
                .end()
                .listener(jobExecutionListener)
                .incrementer(new CustomJobParametersIncrementer())
                //.incrementer(new RunIdIncrementer()) // 스프링에서 제공하는 파라미터 id 증가 클래스.
                //.preventRestart() // 재시작 방지 api
                .build();
    }

    private Job childJob() {
        return new JobBuilder("childJob",jobRepository)
                .start(jobFlowStep())
                .build();
    }

    /**
     * Step 내부에서 Job을 실행. main job과 독립적으로 저장된다.
     */
    private Step jobStep(JobLauncher jobLauncher) {
        return new StepBuilder("jobStep",jobRepository)
                .job(childJob())
                .launcher(jobLauncher)
                .parametersExtractor(jobParametersExtractor())
                .listener(new StepExecutionListener() {
                    /**
                     * ParameterExtractor에서 ExecutorContext서 꺼낼 값을 미리 설정
                     */
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().putString("name","user1");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return null;
                    }
                })
                .build();
    }

    /**
     * StepExecution 내부의 ExecutionContext의 Key에 해당하는 Value 값을 가져오는 역할을 한다.
     * ExecutionContext에 해당 Key 값을 저장해야 가져올 수 있다
     */
    private DefaultJobParametersExtractor jobParametersExtractor() {
        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        extractor.setKeys(new String[]{"name"});
        return extractor;
    }

    private Step step() {
        return new StepBuilder("start-step",jobRepository)
                .tasklet(tasklet(),platformTransactionManager)
                .allowStartIfComplete(true)
                .listener(new CustomExitStatus())
                .build();
    }

    private Step jobFlowStep() {
        return new StepBuilder("job-flow-step",jobRepository)
                .tasklet(jobFlowTasklet(),platformTransactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    private Step mainJobStep() {
        return new StepBuilder("main-job-step",jobRepository)
                //.tasklet(new CustomTasklet(),platformTransactionManager)
                .tasklet(mainJobTasklet(),platformTransactionManager)
                //.startLimit(3) // 최대 실행 가능 횟수
                .build();
    }

    /**
     * Job 실패시 실행될 Step
     */
    private Step failedFlowStep() {
        return new StepBuilder("failed-flow-job",jobRepository)
                .tasklet(failedJobTasklet(),platformTransactionManager)
                .build();
    }

    private Step lastStep() {
        return new StepBuilder("last-step",jobRepository)
                .tasklet(lastJobTasklet(),platformTransactionManager)
                .build();
    }

    private Tasklet tasklet() {
        return ((contribution, chunkContext) -> {
            log.info("step1 - tasklet1 수행");
            return RepeatStatus.FINISHED;
        });
    }

    private Tasklet jobFlowTasklet() {
        return ((contribution, chunkContext) -> {
            log.info("jobflow - tasklet 수행");

            return RepeatStatus.FINISHED;
        });
    }

    private Tasklet mainJobTasklet() {
        return ((contribution, chunkContext) -> {
            log.info("step2 - tasklet2 수행");
            //throw new RuntimeException("step2 failed!");
            return RepeatStatus.FINISHED;
        });
    }

    private Tasklet failedJobTasklet() {
        return ((contribution, chunkContext) -> {
            log.info("job flow failed - failed job tasklet 수행");
            return RepeatStatus.FINISHED;
        });
    }

    private Tasklet lastJobTasklet() {
        return ((contribution, chunkContext) -> {
            log.info("last job tasklet 수행");
            return RepeatStatus.FINISHED;
        });
    }
}
