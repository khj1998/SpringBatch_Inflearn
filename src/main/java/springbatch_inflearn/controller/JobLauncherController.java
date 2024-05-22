package springbatch_inflearn.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springbatch_inflearn.dto.Member;

import java.util.Date;

/**
 * 동기, 비동기 Joblauncher run 방식 테스트를 위한 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class JobLauncherController {
    private final Job job;
    private final JobLauncher jobLauncher;

    /**
     * 동기적 방식 - batch 처리가 완료될 때까지 응답 지연.
     */
    @PostMapping("/batch")
    public String launch(@RequestBody Member member) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("member_id",member.getMemberId())
                .addDate("date",new Date())
                .toJobParameters();

        jobLauncher.run(job,jobParameters);

        return "batch_completed!!";
    }

    /**
     * 비동기 방식 - batch 처리를 기다리지 않고 즉시 응답.
     */
    @PostMapping("/batch-async")
    public String launchAsync(@RequestBody Member member) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("member_id",member.getMemberId())
                .addDate("date",new Date())
                .toJobParameters();

        TaskExecutorJobLauncher taskExecutorJobLauncher = (TaskExecutorJobLauncher) jobLauncher;
        taskExecutorJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());

        jobLauncher.run(job,jobParameters);

        return "async batch_completed!!";
    }
}
