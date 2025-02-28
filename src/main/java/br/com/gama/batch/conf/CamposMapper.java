package br.com.gama.batch.conf;

import br.com.gama.batch.models.DadosLeitura;
import br.com.gama.batch.services.conversores.datas.ConverteStringToLocaldate;
import br.com.gama.batch.services.conversores.pfs.ConvertVirgulaParaPonto;
import org.springframework.batch.item.file.LineMapper;

import java.util.StringTokenizer;

/**
 * Classe do tipo mapper para tratar os dados, que vêm em estado bruto e não conforme
 * com o padrão da tabela do banco de dados postgres.
 */
public class CamposMapper implements LineMapper<DadosLeitura> {
    @Override
    public DadosLeitura mapLine(String line, int lineNumber) throws Exception {

        StringTokenizer tokenizer=new StringTokenizer(line, " ");

        DadosLeitura leitura=new DadosLeitura(
                tokenizer.nextToken(),
                ConvertVirgulaParaPonto.converte(tokenizer.nextToken()),
                ConverteStringToLocaldate.convertTo(tokenizer.nextToken()));

        return leitura;
    }
}
