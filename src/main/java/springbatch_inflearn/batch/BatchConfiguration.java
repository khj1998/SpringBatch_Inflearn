package springbatch_inflearn.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
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
                .next(step3())
                .next(step4())
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

    private Step step3() {
        return new StepBuilder("step3",jobRepository)
                .tasklet(tasklet3(),platformTransactionManager)
                .build();
    }

    private Step step4() {
        return new StepBuilder("step4",jobRepository)
                .tasklet(tasklet4(),platformTransactionManager)
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

            /*JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
            jobParameters.getString("name");
            jobParameters.getLong("seq");
            jobParameters.getDate("date");
            jobParameters.getDouble("age");

            Map<String,Object> jobParams = chunkContext.getStepContext().getJobParameters();*/

            log.info("step 1 has been executed");

            /**
             * jobName과 stepName을  jobExecutionContext,stepExecutionContext에 저장.
             * 다음 step에도 해당 jobName과 stepName이 공유되는지 확인하기 위한 코드.
             */

            ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
            ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();

            String jobName = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName();
            String stepName = chunkContext.getStepContext().getStepName();

            if (jobExecutionContext.get("jobName") == null) {
                jobExecutionContext.put("jobName",jobName);
            }

            if (stepExecutionContext.get("stepName") == null) {
                stepExecutionContext.put("stepName",stepName);
            }

            return RepeatStatus.FINISHED;
        });
    }

    private Tasklet tasklet2() {
        return ((contribution, chunkContext) -> {
            log.info("step 2 has been executed");

            /**
             * jobName이 step간 공유가 되었는지, stepName이 step 각 공유되지 않았는지 확인하는 코드
             * 정상 상황 : jobName이 step1 설정과 같고, stepName은 공유되지 않아야 함.
             */
            
            ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
            ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();

            log.info("jobName : "+jobExecutionContext.get("jobName"));
            log.info("stepName : "+stepExecutionContext.get("stepName"));

            String stepName = chunkContext.getStepContext().getStepName();

            if (stepExecutionContext.get("stepName") == null) {
                stepExecutionContext.put("stepName",stepName);
            }

            return RepeatStatus.FINISHED;
        });
    }

    private Tasklet tasklet3() {
        return ((contribution, chunkContext) -> {
            /**
             * 실패하여 재시도하는 경우, 실패한 데이터를 가져올 수 있는지 테스트하는 코드.
             */
            log.info("step 3 has been executed");

            Object name = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name");

            /**
             * 첫번째 실행에서 런타임 에러를 발생시켜 step3를 실패시키고,
             이후 런타임 에러 코드를 주석 처리하여 성공시켜 step4가 DB에 저장되는지 확인한다.

             * step4에서 공유 가능한 JobExecutionContext에 name 값을 설정해 동일하게 나오는지 확인하기 위한 코드.
             */
            if (name == null) {
                chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("name","user1");
                //throw new RuntimeException("step3 has been failed");
            }

            return RepeatStatus.FINISHED;
        });
    }

    private Tasklet tasklet4() {
        return ((contribution, chunkContext) -> {
            /**
             * step3에서 JobExecutionContext에 저장한 name 값을 출력한다.
             */
            log.info("step 4 has been executed");
            log.info("name : "+chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name"));
            return RepeatStatus.FINISHED;
        });
    }
}
