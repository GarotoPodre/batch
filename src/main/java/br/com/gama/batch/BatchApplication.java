package br.com.gama.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner() {
//		return args -> {
//			logger.info("CommandLineRunner sendo executado.");
//			JobParameters jobParameters = new JobParametersBuilder()
//					.addLong("time", System.currentTimeMillis()).toJobParameters();
//			try {
//				logger.info("Iniciando o job ETL");
//				JobExecution jobExecution = jobLauncher.run(job, jobParameters);
//				logger.info("Job Execution Status: {}", jobExecution.getStatus());
//			} catch (JobExecutionAlreadyRunningException | JobRestartException
//					 | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
//				logger.error("Erro ao executar o job: ", e);
//			}
//		};
//	}

}
