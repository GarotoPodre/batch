package br.com.gama.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class BatchApplication {

	private static final Logger logger = LoggerFactory.getLogger(BatchApplication.class);
	@Autowired
	private Job job;
	@Autowired
	private Job limpaJob;
	@Autowired
	private JobLauncher jobLauncher;

	/**
	 * Já que eu estou utilizando meu próprio CommandLineRunner,
	 * eu desabilitei o automático SpringBoot
	 * Isso foi feito no application.properties
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			logger.info("Executando CommandLineRunner.");
			// Run the ETL job
			JobParameters jobParametersEtl = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis()).toJobParameters(); // this makes every run unique
			try {
				logger.info("ETL job iniciando");
				jobLauncher.run(job, jobParametersEtl);
				logger.info("ETL job finalizado");
			} catch (JobExecutionAlreadyRunningException | JobRestartException
					 | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
				logger.error("Erro ocorreu quando o ETL job foi executado: ", e);
			}

			JobParameters jobParametersCleanup = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis()).toJobParameters(); // this makes every run unique
			try {
				logger.info("limpaJob iniciado");
				jobLauncher.run(limpaJob, jobParametersCleanup);
				logger.info("limpaJob finalizado");
			} catch (JobExecutionAlreadyRunningException | JobRestartException
					 | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
				logger.error("Erro ocorreu quando o limpaJob foi executado: ", e);
			}
		};
	}

}
