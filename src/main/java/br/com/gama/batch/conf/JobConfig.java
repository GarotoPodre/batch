package br.com.gama.batch.conf;

import br.com.gama.batch.models.DadosLeitura;
import br.com.gama.batch.tasks.ApagaArquivosTasklet;
import br.com.gama.batch.tasks.CopiaArquivosTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Essa classe é espaço reservado para os beans que serão usados na applicação.
 * Essa aplicação é desenhada para ser executada via linha de comando (achei mais
 * fácil para usar no cron/tarefas agendadas
 */
@Configuration
public class JobConfig {
    private static final Logger logger = LoggerFactory.getLogger(CopiaArquivosTasklet.class);
    @Value("${my.custom.pastafonte}")
    private  String pastaFonte;

    @Value("${my.custom.pastadestino}")
    private String pastaDestino;

    /**
     * Primeiro Job. Executa extract (E), Transform (T) e load(L)
     * @param repository
     * @param step1
     * @param step2
     * @return
     */
    @Bean
    public Job job(JobRepository repository, Step step1, Step step2){
        System.out.println("##### INICIANDO JOB #####");

        return new JobBuilder("ETL", repository)
                .start(step1)
                .next(step2)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    /**
     * Primeiro Step do primeiro Job.
     * Procura os arquivos que estão na pasta exterior à aplicação, 'csvs' (aonde os loggers gravam seus arquivos),
     * e os copia para pasta interna 'staging', para posterior tratamento.
     * @param repository
     * @param transactionManager
     * @return
     */
    @Bean
    public Step step1(JobRepository repository, JdbcTransactionManager transactionManager){
        logger.info("Primeiro step do job - copiando arquivos originais de {}, para {}", pastaFonte,pastaDestino );

        CopiaArquivosTasklet task = new CopiaArquivosTasklet(this.pastaFonte, this.pastaDestino);

        return new StepBuilder("extract", repository)
                .tasklet(task, transactionManager)
                .build();

    }

    /**
     * Metodo que lê os as linhas do arquivo, uma a uma, e cria uma instancia do objeto DadosLeitura
     * para cada linha.
     *
     * @return
     * @throws IOException
     */
    @Bean
    public ItemReader<DadosLeitura> itemReader() throws IOException{

        //Para ler múltiplos arquivos de uma pasta
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String localizacao = "file:"+pastaDestino+"/*.csv";
        Resource[] resources=resolver.getResources(localizacao);
        logger.info("numero de arquivos em resources: "+resources.length);

        MultiResourceItemReader<DadosLeitura> multiResourceItemReader=new MultiResourceItemReader<>();
        multiResourceItemReader.setResources(resources);
        FlatFileItemReader<DadosLeitura>csvItemReader = new FlatFileItemReader<>();

        csvItemReader.setLinesToSkip(1);//pulando o cabecalho
        csvItemReader.setName("csvItemReader");

        //Configurando o mapeador de linhas
        csvItemReader.setLineMapper(new CamposMapper());

        //Configura o reader
        multiResourceItemReader.setDelegate(csvItemReader);

        return multiResourceItemReader;
    }

    /**
     * Metodo que recebe o datasource como parametro (este vem do .properties),
     * e retorna um objeto que, utilizando sql, grava os dados no banco de dados.
     *
     * Os dados no insert correspondem aos nomes dos campos do objeto.
     *
     * O metodo 'beanMapped', atraves de reflection, instrui o writer a 'ligar'
     * os campos do objeto com as colunas da tabela, quando o nome for igual.
     *
     * @param dataSource
     * @return
     */
    @Bean
    public JdbcBatchItemWriter<DadosLeitura>dataTableWriter(DataSource dataSource){
        String sql="insert into leitura values(:ID, :VALOR, :DATA)";

        return new JdbcBatchItemWriterBuilder<DadosLeitura>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    /**
     * Segundo Step do primeiro job. Lê os arquivos do diretório 'staging', trata os
     * registros e os carrega
     * na tabela 'leitura' do banco de dados.
     * @param jobRepository
     * @param transactionManager
     * @param itemReader
     * @param itemWriter
     * @return
     */
    @Bean
    public Step step2(
            JobRepository jobRepository, JdbcTransactionManager transactionManager,
            ItemReader<DadosLeitura> itemReader, ItemWriter<DadosLeitura>itemWriter){

        logger.info("Segundo step do job - lendo os arquivos .csv e gravando no banco de dados" );

        return new StepBuilder("transform_and_load", jobRepository)
                .<DadosLeitura, DadosLeitura>chunk(100, transactionManager)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Job limpaJob(JobRepository jobRepository, Step apagaArquivosInternos, Step apagaArquivosExternos){
        return new JobBuilder("limpa_job", jobRepository)
                .start(apagaArquivosInternos)
                .next(apagaArquivosExternos)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    /**
     * Primeiro step do segundo Job. Apaga os arquivos da pasta interna (staging)
     * da aplicacao
     * @param jobRepository
     * @param transactionManager
     * @return
     */
    @Bean
    public Step apagaArquivosInternos(JobRepository jobRepository, JdbcTransactionManager transactionManager){
        ApagaArquivosTasklet task = new ApagaArquivosTasklet(this.pastaDestino);

        logger.info("Arquivos da pasta {} foram apagados", this.pastaDestino);

        return new StepBuilder("apaga_arquivos_internos", jobRepository)
                .tasklet(task, transactionManager)
                .build();

    }

    /**
     * Segundo step do Job. Apaga arquivos da pasta interna (csvs) da aplicacao.
     * @param jobRepository
     * @param transactionManager
     * @return
     */
    @Bean
    public Step apagaArquivosExternos(JobRepository jobRepository, JdbcTransactionManager transactionManager){
        ApagaArquivosTasklet task =new ApagaArquivosTasklet(this.pastaFonte);
        logger.info("Arquivos da pasta {} foram apagados", this.pastaFonte);

        return new StepBuilder("apaga_arquivos_externos", jobRepository)
                .tasklet(task, transactionManager)
                .build();
    }

}
