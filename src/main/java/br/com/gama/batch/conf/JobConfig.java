package br.com.gama.batch.conf;

import br.com.gama.batch.tasks.CopiaArquivosTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

import java.io.IOException;

/**
 * Essa classe é espaço reservado para os beans que serão usados na applicação.
 * Essa aplicação é desenhada para ser executada via linha de comando (achei mais fácil para usar no cron/tarefas agendadas
 */
@Configuration
public class JobConfig {
    private static final Logger logger = LoggerFactory.getLogger(CopiaArquivosTasklet.class);
    @Value("${my.custom.pastafonte}")
    private  String pastaFonte;

    @Value("${my.custom.pastadestino}")
    private String pastaDestino;

    @Bean
    public Job job(JobRepository repository, Step step1){
        System.out.println("##### INICIANDO JOB #####");

        return new JobBuilder("ETL", repository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository repository, JdbcTransactionManager transactionManager){
        logger.info("Primeiro step do job - copiando arquivos originais de {}, para {}", pastaFonte,pastaDestino );

        CopiaArquivosTasklet task = new CopiaArquivosTasklet(this.pastaFonte, this.pastaDestino);


        return new StepBuilder("extract", repository)
                .tasklet(task, transactionManager)
                .build();

    }

}
