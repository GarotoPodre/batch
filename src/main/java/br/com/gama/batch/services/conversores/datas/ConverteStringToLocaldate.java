package br.com.gama.batch.services.conversores.datas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ConverteStringToLocaldate{

    public static LocalDate convertTo(String dataOriginal){//, String pattern) {
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data=LocalDate.parse(dataOriginal, formatter);
        return data;
    }

}
