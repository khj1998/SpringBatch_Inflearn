package springbatch_inflearn.JobRunnerTest;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {
    /**
     * 스프링에서 JobLauncher의 구현체 빈을 컨테이너에 등록해준다.
     */
    private final JobLauncher jobLauncher;

    private final JobParameters jobParameters;
    private final Job job;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //jobLauncher.run(job,jobParameters);
    }
}
