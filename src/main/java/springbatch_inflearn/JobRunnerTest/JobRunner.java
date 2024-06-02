package springbatch_inflearn.JobRunnerTest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 스프링에서 Job 자동 실행 옵션을 off 하고 직접 run하는 클래스.
 */
@Slf4j
//@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {
    /**
     * 스프링에서 JobLauncher의 구현체 빈을 컨테이너에 등록해준다.
     */
    private final JobLauncher jobLauncher;
    private final JobParametersValidator jobParametersValidator;
    private final Job job;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("jobRunner 실행");

        JobParameters jobParameters = new JobParametersBuilder()
                .addJobParameter("name","validate-test",String.class)
                .addJobParameter("date",new Date(), Date.class)
                .toJobParameters();

        jobParametersValidator.validate(jobParameters);

        jobLauncher.run(job,jobParameters);
    }
}
