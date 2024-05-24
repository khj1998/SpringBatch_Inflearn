package springbatch_inflearn.validationconfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomJobParametersValidator implements JobParametersValidator {
    /**
     * jobparameters 값을 검증
     * SpringBatch 5버전 이후 Job 생성시 이름이 매개변수로 전달되어야함.
     * 필수값 이름 외 다른 파라미터 검증용으로 활용이 가능.
     */
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (parameters.getString("name") == null) {
            throw new JobParametersInvalidException("name parameter is not found");
        }

        if (parameters.getDate("date")==null) {
            throw new JobParametersInvalidException("date parameter is not found");
        }

        if (parameters.getString("count")==null) {
            log.warn("count parameter has not been set");
        }
    }
}
