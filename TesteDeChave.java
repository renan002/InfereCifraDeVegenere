import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TesteDeChave {

    static HashMap<Character, Double> frequenciaLetrasPTBR = new HashMap<Character, Double>(){{
        put('a', 14.63);
        put('e', 12.57);
        put('o', 10.73);
        put('s', 7.81);
        put('r', 6.53);
        put('i', 6.18);
        put('n', 5.05);
        put('d', 4.99);
        put('m', 4.74);
        put('u', 4.63);
        put('t', 4.34);
        put('c', 3.88);
        put('l', 2.78);
        put('p', 2.52);
        put('v', 1.67);
        put('g', 1.30);
        put('h', 1.28);
        put('q', 1.20);
        put('b', 1.04);
        put('f', 1.02);
        put('z', 0.47);
        put('j', 0.40);
        put('x', 0.21);
        put('k', 0.02);
        put('w', 0.01);
        put('y', 0.01);
    }};

    static String texto1 = "oaromainebriantedocafefrescopassado";
    static String texto2 = "aanalisedefrequenciaeumatecnicabast";
    static String texto3 = "aimportanciadadapreservacaodomeioam";

    static String cifra1 = "daucyazpsnlbaamidhqlfvfdsjcgvoklovo";
    static String cifra2 = "paqoxijgrqzkedninvwlelmmhvcfoqsuokt";
    static String cifra3 = "pipdarkcboctdnwepksdeivmqrovuawbcsm";


    static String chave = "padomarcomutanteatolaramorasgostosa";

    public static void main(String[] args) {
        retornaFrequenciaTextoDescriptografado(texto1).forEach((k,v) -> {
            if (v>0.0)
                System.out.printf("char: %s - Frequencia: %f\n", k, v);
        });
        System.out.println("=================================================================================");
        System.out.printf("Chave testada: %s\n", "olaistoehumtestedechavebatataquente");
        retornaFrequenciaTextoDescriptografado(descriptografarVigenere(cifra1, "olaistoehumtestedechavebatataquente")).forEach((k,v) -> {

            if (v>0.0)
                System.out.printf("char: %s - Frequencia: %f\n", k, v);
        });
        System.out.println("=================================================================================");
        System.out.printf("Chave testada: %s\n", "auladeestatisticatodastercaequintas");
        retornaFrequenciaTextoDescriptografado(descriptografarVigenere(cifra1, "auladeestatisticatodastercaequintas")).forEach((k,v) -> {
            if (v>0.0)
                System.out.printf("char: %s - Frequencia: %f\n", k, v);
        });
        System.out.println("=================================================================================");
        System.out.printf("Chave testada: %s\n", "achavetemqueserdiferentbatataquente");
        retornaFrequenciaTextoDescriptografado(descriptografarVigenere(cifra1, "achavetemqueserdiferentbatataquente")).forEach((k,v) -> {

            if (v>0.0)
                System.out.printf("char: %s - Frequencia: %f\n", k, v);
        });
        System.out.println("=================================================================================");
        System.out.printf("Chave testada: %s\n", "tudonestachaveehdiferentesdasoutras");
        retornaFrequenciaTextoDescriptografado(descriptografarVigenere(cifra1, "tudonestachaveehdiferentesdasoutras")).forEach((k,v) -> {

            if (v>0.0)
                System.out.printf("char: %s - Frequencia: %f\n", k, v);
        });
        System.out.println("=================================================================================");
        System.out.printf("Chave testada: %s\n", "padomarcomutanteatolaramorasmuitobo");
        retornaFrequenciaTextoDescriptografado(descriptografarVigenere(cifra1, "padomarcomutanteatolaramorasmuitobo")).forEach((k,v) -> {

            if (v>0.0)
                System.out.printf("char: %s - Frequencia: %f\n", k, v);
        });
        System.out.println("=================================================================================");
        System.out.printf("Chave testada: %s\n", "padomarcomutanteatolaramorasgostosa");
        retornaFrequenciaTextoDescriptografado(descriptografarVigenere(cifra1.toLowerCase(), chave)).forEach((k,v) -> {

            if (v>0.0)
                System.out.printf("char: %s - Frequencia: %f\n", k, v);
        });
    }


    private static Map<Character, Double> retornaFrequenciaTextoDescriptografado(String textoDescriptografado) {
        Map<Character, Double> frequenciasDoTexto = new HashMap<>();
        int tamanhoTexto = textoDescriptografado.length();
        int[] quantidadeLetras = new int[tamanhoTexto];

        for (char c : textoDescriptografado.toCharArray()) {
            int i = getIndexByChar(c);
            quantidadeLetras[i]++;
        }

        for (char c : frequenciaLetrasPTBR.keySet()) {
            int i = getIndexByChar(c);
            double d = (double) quantidadeLetras[i] / tamanhoTexto;
            frequenciasDoTexto.put(c, d*100);
        }

        return sortByValueCD(frequenciasDoTexto);
    }


    private static String descriptografarVigenere(String textoCifrado, String chave) {
        StringBuilder textoDescriptografado = new StringBuilder();

        for (int i = 0; i < textoCifrado.length(); i++) {
            char letraCifrada = textoCifrado.charAt(i);
            char letraChave = chave.charAt(i);
            int temp = (letraCifrada - letraChave + 26);
            int temp2 = (temp % 26) + 'a';
            char letraOriginal = (char) (temp2);
            textoDescriptografado.append(letraOriginal);
        }
        System.out.println(textoDescriptografado);
        return textoDescriptografado.toString();
    }


    static int getIndexByChar(char c) {

        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else {
            return -1;
        }
    }

    private static HashMap<Character, Double> sortByValueCD(Map<Character, Double> hm) {
        List<Map.Entry<Character, Double> > list =
                new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.comparingByValue());

        Collections.reverse(list);

        HashMap<Character, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<Character, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
