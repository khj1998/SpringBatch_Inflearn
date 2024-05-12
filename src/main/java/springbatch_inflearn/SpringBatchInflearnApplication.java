package springbatch_inflearn;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchInflearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchInflearnApplication.class, args);
    }

}
