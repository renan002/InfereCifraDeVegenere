import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Cifra {

    static String alfabeto = "abcdefghijklmnopqrstuvwxyz";
    static char[] alfabetos = alfabeto.toCharArray();
    static String chave;
    static int tamanhoTextos = 0;
    static HashMap<Character, Integer> quantidadeLetrasCifrados = new HashMap<Character, Integer>();
    static HashMap<Character, Float> frequenciaLetrasCifrados = new HashMap<Character, Float>();
    private static Map<String, Integer> bigramasCifrados = new HashMap<>();
    private static int totalBigramasCifrados = 0;
    private static Map<String, Integer> bigramasTextos = new HashMap<>();
    private static int totalBigramasTextos = 0;

    static String cifra1;
    static String cifra2;
    static String cifra3;

    public static void main(String[] args) throws IOException {

        String texto1 = Files.readAllLines(Paths.get("textos/Texto1.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        String texto2 = Files.readAllLines(Paths.get("textos/Texto2.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        String texto3 = Files.readAllLines(Paths.get("textos/Texto3.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        cifra1 = Files.readAllLines(Paths.get("cifras/cifra1.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        cifra2 = Files.readAllLines(Paths.get("cifras/cifra2.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        cifra3 = Files.readAllLines(Paths.get("cifras/cifra3.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        tamanhoTextos = texto1.length();

        retornaChaveCompleta();

        gravarCifra(texto1, "cifras/cifra1.txt");
        gravarCifra(texto2, "cifras/cifra2.txt");
        gravarCifra(texto3, "cifras/cifra3.txt");

        //inferirCifra();

        System.out.println("===============================================================================================");

        calcularFrequenciaBigramasCifrados(cifra1);
        calcularFrequenciaBigramasCifrados(cifra2);
        calcularFrequenciaBigramasCifrados(cifra3);

        calcularFrequenciaBigramasTextos(texto1);
        calcularFrequenciaBigramasTextos(texto2);
        calcularFrequenciaBigramasTextos(texto3);


        System.out.println(analisarBigramas());

        inferirChave(cifra1, quantidadeLetrasCifrados, bigramasCifrados);
    }

    public static void calcularFrequenciaBigramasCifrados(String textoCifrado) {
        for (int i = 0; i < tamanhoTextos - 1; i++) {
            String bigrama = textoCifrado.substring(i, i + 2);
            if (!bigramasCifrados.containsKey(bigrama)) {
                bigramasCifrados.put(bigrama, 0);
            }
            bigramasCifrados.put(bigrama, bigramasCifrados.get(bigrama) + 1);
            totalBigramasCifrados++;
        }
    }
    public static void calcularFrequenciaBigramasTextos(String texto) {
        for (int i = 0; i < tamanhoTextos - 1; i++) {
            String bigrama = texto.substring(i, i + 2);
            if (!bigramasTextos.containsKey(bigrama)) {
                bigramasTextos.put(bigrama, 0);
            }
            bigramasTextos.put(bigrama, bigramasTextos.get(bigrama) + 1);
            totalBigramasTextos++;
        }
    }

    private static Map<Character, Integer> calcularFrequenciaLetras(String texto) {
        Map<Character, Integer> frequencias = new HashMap<>();
        for (char letra : texto.toLowerCase().toCharArray()) {
            //if (Collections.singletonList(alfabetos).contains(letra)) {
                frequencias.put(letra, frequencias.getOrDefault(letra, 0) + 1);
            //}
        }
        return frequencias;
    }

    public static String analisarBigramas() {
        StringBuilder analiseCifra = new StringBuilder();
        StringBuilder analiseTexto = new StringBuilder();

        // Ordenar bigramasCifrados por frequência decrescente
        List<Map.Entry<String, Integer>> bigramasCifradosOrdenados = new ArrayList<>(bigramasCifrados.entrySet());
        bigramasCifradosOrdenados.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        List<Map.Entry<String, Integer>> bigramasTextosOrdenados = new ArrayList<>(bigramasTextos.entrySet());
        bigramasTextosOrdenados.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Analisar os bigramasCifrados mais frequentes
        for (Map.Entry<String, Integer> entry : bigramasCifradosOrdenados) {
            String bigrama = entry.getKey();
            int frequencia = entry.getValue();
            double probabilidade = (double) frequencia / totalBigramasCifrados;
            analiseCifra.append(String.format("Bigrama: %s, Frequência: %d, Probabilidade: %.6f\n", bigrama, frequencia, probabilidade));
        }

        for (Map.Entry<String, Integer> entry : bigramasTextosOrdenados) {
            String bigrama = entry.getKey();
            int frequencia = entry.getValue();
            double probabilidade = (double) frequencia / totalBigramasCifrados;
            analiseTexto.append(String.format("Bigrama: %s, Frequência: %d, Probabilidade: %.6f\n", bigrama, frequencia, probabilidade));
        }
        System.out.println(analiseTexto);

        System.out.println("===============================================================================================");

        return analiseCifra.toString();
    }

    /*private static String inferirChave(String textoCifrado, Map<Character, Integer> frequenciasLetras, Map<String, Integer> bigramas) {
        // Tentar todas as combinações possíveis de letras maiúsculas como chave
        for (String chaveCandidata : gerarTodasChavesCandidatas()) {
            String textoDescriptografado = descriptografarVigenere(textoCifrado, chaveCandidata);

            // Analisar a qualidade do texto descriptografado
            Map<Character, Integer> frequenciasDescriptografadas = calcularFrequenciaLetras(textoDescriptografado);
            double chiQuadrado = calcularChiQuadrado(frequenciasLetras, frequenciasDescriptografadas);

            // Se o chi-quadrado for baixo, a chave candidata pode ser a correta
            if (chiQuadrado < CHAVE_CANDIDATA_ACEITAVEL) {
                System.out.println("Chave Candidata: " + chaveCandidata + ", Chi-Quadrado: " + chiQuadrado);
                return chaveCandidata;
            }

            System.out.println(chiQuadrado);
        }



        // Nenhuma chave candidata encontrada
        System.out.println("Nenhuma chave candidata encontrada.");
        return "";
    }*/

    private static String inferirChave(String textoCifrado, Map<Character, Integer> frequenciasLetras, Map<Character, Integer> bigramas) {
        // Criar uma chave candidata inicial aleatória
        String chaveCandidata = gerarChaveCandidataAleatoria();

        // Definir parâmetros do Hill-climbing
        int maxIteracoes = 1000; // Número máximo de iterações
        double taxaMutacao = 0.1; // Probabilidade de mutação de um caractere na chave

        // Realizar o Hill-climbing
        for (int iteracao = 0; iteracao < maxIteracoes; iteracao++) {
            // Avaliar a qualidade da chave candidata atual
            double chiQuadradoAtual = calcularChiQuadrado(frequenciasLetras, descriptografarVigenere(textoCifrado, chaveCandidata));

            // Tentar mutações na chave candidata
            String chaveMutante = gerarChaveMutante(chaveCandidata, taxaMutacao);
            double chiQuadradoMutante = calcularChiQuadrado(frequenciasLetras, descriptografarVigenere(textoCifrado, chaveMutante));

            // Se a mutação melhorar o chi-quadrado, atualizar a chave candidata
            if (chiQuadradoMutante < chiQuadradoAtual) {
                chaveCandidata = chaveMutante;
                System.out.println("Iteração: " + iteracao + ", Chi-Quadrado: " + chiQuadradoMutante + ", Chave: " + chaveCandidata);
            }
        }

        return chaveCandidata;
    }
    // Método para gerar uma chave candidata aleatória
    private static String gerarChaveCandidataAleatoria() {
        StringBuilder chaveCandidata = new StringBuilder();
        for (int i = 0; i < 56; i++) {
            chaveCandidata.append(alfabetos[new Random().nextInt(alfabetos.length)]);
        }
        return chaveCandidata.toString();
    }

    // Método para gerar uma chave mutante a partir da chave candidata atual
    private static String gerarChaveMutante(String chaveCandidata, double taxaMutacao) {
        StringBuilder chaveMutante = new StringBuilder(chaveCandidata);
        for (int i = 0; i < chaveCandidata.length(); i++) {
            if (Math.random() < taxaMutacao) {
                chaveMutante.setCharAt(i, alfabetos[new Random().nextInt(alfabetos.length)]);
            }
        }
        return chaveMutante.toString();
    }

    // Método para gerar todas as combinações possíveis de letras maiúsculas como chave
    private static List<String> gerarTodasChavesCandidatas() {
        List<String> chavesCandidatas = new ArrayList<>();
        StringBuilder chaveCandidata = new StringBuilder();

        // Criar uma chave candidata inicial com 56 caracteres vazios
        for (int i = 0; i < 56; i++) {
            chaveCandidata.append("-");
        }

        // Gerar todas as combinações recursivamente
        gerarChaveCandidataRecursiva(chavesCandidatas, chaveCandidata, 0);

        System.out.println("terminei de gerar as chaves");

        return chavesCandidatas;
    }

    private static void gerarChaveCandidataRecursiva(List<String> chavesCandidatas, StringBuilder chaveCandidata, int posicao) {
        //if (posicao!=56)
            //System.out.println("posição: "+posicao);
        if (posicao == 56) {
            // Uma chave candidata completa foi formada, adicionar à lista
            //System.out.println("Chave gerada: "+chaveCandidata);
            chavesCandidatas.add(chaveCandidata.toString());
            return;
        }

        // Substituir cada caractere "-" por uma letra maiúscula
        for (char letra : alfabetos) {
            chaveCandidata.setCharAt(posicao, letra);
            gerarChaveCandidataRecursiva(chavesCandidatas, chaveCandidata, posicao + 1);
            chaveCandidata.setCharAt(posicao, '-'); // Voltar para "-" para explorar outras letras
        }
    }

    // Método para descriptografar o texto cifrado usando a chave fornecida
    private static String descriptografarVigenere(String textoCifrado, String chave) {
        StringBuilder textoDescriptografado = new StringBuilder();
        for (int i = 0; i < textoCifrado.length(); i++) {
            char letraCifrada = textoCifrado.charAt(i);
            char letraChave = chave.charAt(i % chave.length());
            char letraOriginal = (char) ((letraCifrada - letraChave + 26) % 26 + 'A');
            textoDescriptografado.append(letraOriginal);
        }
        return textoDescriptografado.toString();
    }

    // Método para calcular o chi-quadrado entre duas frequências de letras
    private static double calcularChiQuadrado(Map<Character, Integer> frequenciaEsperada, Map<Character, Integer> frequenciaObservada) {
        double chiQuadradoTotal = 0.0;
        for (char letra : alfabetos) {
            int frequenciaEsperadaLetra = frequenciaEsperada.getOrDefault(letra, 0);
            int frequenciaObservadaLetra = frequenciaObservada.getOrDefault(letra, 0);
            double contribuicaoChiQuadrado = Math.pow((frequenciaEsperadaLetra - frequenciaObservadaLetra), 2) / frequenciaEsperadaLetra;
            chiQuadradoTotal += contribuicaoChiQuadrado;
        }
        return chiQuadradoTotal;
    }

    // Constante para definir o limite de chi-quadrado aceitável para uma chave candidata
    private static final double CHAVE_CANDIDATA_ACEITAVEL = 10.0;

    private static void inferirCifra() throws IOException {
        char[] frequenciaLetras = new char[] {'a', 'e', 'o', 's', 'r', 'i', 'n', 'd', 'm', 'u', 't', 'c', 'l', 'p', 'v', 'g', 'h', 'q', 'b', 'f', 'z', 'j', 'x', 'k', 'w', 'y'};
        String cifra1 = Files.readAllLines(Paths.get("cifras/cifra1.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        String cifra2 = Files.readAllLines(Paths.get("cifras/cifra2.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        String cifra3 = Files.readAllLines(Paths.get("cifras/cifra3.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        popularArray(cifra1.toCharArray());
        popularArray(cifra2.toCharArray());
        popularArray(cifra3.toCharArray());

        for(Character c : quantidadeLetrasCifrados.keySet()) {
            int i = quantidadeLetrasCifrados.get(c);

            float f = (float) i /tamanhoTextos*3;

            frequenciaLetrasCifrados.put(c, f);
        }

        frequenciaLetrasCifrados = sortByValue(frequenciaLetrasCifrados);

        for (Character i : quantidadeLetrasCifrados.keySet()) {
            System.out.println(i + " " + quantidadeLetrasCifrados.get(i));
        }


        StringBuilder sb = new StringBuilder();

        System.out.println("========================================================");

        for (Character i : frequenciaLetrasCifrados.keySet()) {
            System.out.println(i + " " + frequenciaLetrasCifrados.get(i));
        }


    }

    public static HashMap<Character, Float> sortByValue(HashMap<Character, Float> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Character, Float> > list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        Collections.reverse(list);

        // put data from sorted list to hashmap
        HashMap<Character, Float> temp = new LinkedHashMap<>();
        for (Map.Entry<Character, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static int diferencaEntreChars(char a, char b) {
        int ia = getIndexByChar(a);
        int ib = getIndexByChar(b);

        return Math.max(ia, ib) - Math.min(ia, ib);
    }

    private static void popularArray(char[] arrayCifra) {
        for (char c : arrayCifra) {
            if (!quantidadeLetrasCifrados.containsKey(c))
                quantidadeLetrasCifrados.put(c, 0);
            int i = quantidadeLetrasCifrados.get(c);
            quantidadeLetrasCifrados.put(c, i+1);
        }
    }

    private static void gravarCifra(String texto, String nomeArquivo) throws IOException {
        FileWriter cifra = new FileWriter(nomeArquivo);
        cifra.write(retornaTextoCifrado(texto));
        cifra.close();
    }

    private static void retornaChaveCompleta() throws IOException {
        String chaveTmp = Files.readAllLines(Paths.get("chaves/chave.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        StringBuilder sb = new StringBuilder();
        sb.append(chaveTmp);
        char[] letrasDaChave = chaveTmp.toCharArray();

        while (sb.length() < tamanhoTextos) {
            sb.append(letrasDaChave[sb.length() % 5]);
        }
        chave = sb.toString();
    }

    static int getIndexByChar(char c) {

        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else {
            return -1;
        }
    }

    static char getCharByIndex(int c) {

        if (c >= 0 && c <= 25) {
            return (char) ('a' + c);
        } else if (c >= 'A' && c <= 'Z') {
            return (char) ('A' + c);
        } else {
            return Character.highSurrogate(483758934);
        }
    }

    static String retornaTextoCifrado(String texto) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {

            int r = getIndexByChar(chave.charAt(i)) + getIndexByChar(texto.charAt(i));

            while (r >= 26) r-=26;
            sb.append(alfabetos[r]);
        }

        return sb.toString();
    }
}
