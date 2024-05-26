package springbatch_inflearn.Incrementer;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 동일한 Job을 여러번 실행할 수 있도록 Parameter 값을 증가시키는 커스텀 클래스.
 */
@Configuration
public class CustomJobParametersIncrementer implements JobParametersIncrementer {
    static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-hhmmss");
    @Override
    public JobParameters getNext(JobParameters parameters) {
        String id = format.format(new Date());

        return new JobParametersBuilder().addString("run.id",id).toJobParameters();
    }
}
