package br.com.gama.batch.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApagaArquivosTasklet implements Tasklet {
    private static final Logger logger = LoggerFactory.getLogger(ApagaArquivosTasklet.class);
    private final String pasta;

    public ApagaArquivosTasklet(String pasta) {
        this.pasta = pasta;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Apagando arquivos da pasta {}", pasta);
        Path pastaPath = Paths.get(pasta);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pastaPath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    try {
                        Files.delete(file);
                        logger.info("Arquivo {} apagado com sucesso.", file.getFileName());
                    } catch (IOException e) {
                        logger.error("Erro ao apagar o arquivo {}: {}", file.getFileName(), e.getMessage());
                        throw new RuntimeException("Erro ao apagar o arquivo: " + file.getFileName(), e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao acessar a pasta {}: {}", pastaPath, e.getMessage());
            throw new RuntimeException("Erro ao acessar a pasta: " + pastaPath, e);
        }

        return RepeatStatus.FINISHED;
    }
}
