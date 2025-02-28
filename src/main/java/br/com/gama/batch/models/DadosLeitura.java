package br.com.gama.batch.models;

import java.time.LocalDate;

public record DadosLeitura(
    String ID,
    float VALOR,
    LocalDate DATA)
{}
