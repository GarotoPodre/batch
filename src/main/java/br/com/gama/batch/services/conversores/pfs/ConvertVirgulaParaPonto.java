package br.com.gama.batch.services.conversores.pfs;

public class ConvertVirgulaParaPonto {

    public static Float converte(String original) {
        String valor=original.replaceAll(",", ".");
        return Float.valueOf(valor);
    }
}
