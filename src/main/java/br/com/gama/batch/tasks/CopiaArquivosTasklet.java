package br.com.gama.batch.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.IOException;
import java.nio.file.*;

public class CopiaArquivosTasklet implements Tasklet {
    private static final Logger logger = LoggerFactory.getLogger(CopiaArquivosTasklet.class);

    private final String pastaOrigem, pastaDestino;

    public CopiaArquivosTasklet(String pastaOrigem, String pastaDestino) {
        this.pastaOrigem = pastaOrigem;
        this.pastaDestino = pastaDestino;
        logger.info("Criando a tasklet, pasta origem: {}, pasta destino: {}", this.pastaOrigem, this.pastaDestino);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("método execute sendo executado");
        Path pastaOrigemPath = Paths.get(this.pastaOrigem);
        Path pastaDestinoPath = Paths.get(this.pastaDestino);


        try (DirectoryStream<Path> pastaComArquivosOriginais = Files.newDirectoryStream(pastaOrigemPath)) {
            for (Path p : pastaComArquivosOriginais) {
                if (Files.isRegularFile(p)) {
                    Path target = pastaDestinoPath.resolve(p.getFileName());
                    try {
                        Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
                        logger.info("Arquivo {} copiado para {}", p.getFileName(), target);
                    } catch (IOException e) {
                        logger.error("Erro ao copiar o arquivo {} para {}: {}", p.getFileName(), target, e.getMessage());
                        throw new RuntimeException("Erro ao copiar arquivo: " + p.getFileName(), e);
                    }
                } else {
                    logger.warn("O caminho {} não é um arquivo regular e será ignorado.", p);
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao ler o diretório {}: {}", pastaOrigemPath, e.getMessage());
            throw new RuntimeException("Erro ao ler o diretório de origem.", e);
        }
        return RepeatStatus.FINISHED;

    }
}
