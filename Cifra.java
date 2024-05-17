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
    static HashMap<Character, Integer> quantidadeLetrasTextos = new HashMap<Character, Integer>();
    static HashMap<Character, Float> frequenciaLetrasCifrados = new HashMap<Character, Float>();
    static HashMap<Character, Float> frequenciaLetrasTextos = new HashMap<Character, Float>();
    private static HashMap<String, Integer> repeticoesCifradas = new HashMap<>();
    static HashMap<String, ArrayList<Repeticoes>> indexRepeticoesCifradas = new HashMap<>();
    static char[] frequenciaLetras = new char[] {'a', 'e', 'o', 's', 'r', 'i', 'n', 'd', 'm', 'u', 't', 'c', 'l', 'p', 'v', 'g', 'h', 'q', 'b', 'f', 'z', 'j', 'x', 'k', 'w', 'y'};
    static int minRepeticoes = 4;
    static int quantidadeDeRepeticoesAnalisadas = 15;

    static String cifra1;
    static String cifra2;
    static String cifra3;
    static String texto1;
    static String texto2;
    static String texto3;


    public static void main(String[] args) throws IOException {

        texto1 = Files.readAllLines(Paths.get("textos/Texto1.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        texto2 = Files.readAllLines(Paths.get("textos/Texto2.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        texto3 = Files.readAllLines(Paths.get("textos/Texto3.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        tamanhoTextos = texto1.length();

        retornaChaveCompleta();

        cifra1 = gravarCifra(texto1, "cifras/cifra1.txt");
        cifra2 = gravarCifra(texto2, "cifras/cifra2.txt");
        cifra3 = gravarCifra(texto3, "cifras/cifra3.txt");

        System.out.println("===============================================================================================");


        inferirCifra();

        repeticoesCifradas = obterRepticoesCifradas(cifra1);

        repeticoesCifradas.forEach((k, v) -> {
            if(v > 1)
                System.out.printf("Sequencia: %s - Repeticoes: %s\n", k, v);
        });

        System.out.println("============================================");

        //indexRepeticoesCifradas.forEach((k,v) -> indexRepeticoesCifradas.get(k).forEach(r -> System.out.println(k + " - "+r)));

        //inferirChaveHill(cifra1, quantidadeLetrasCifrados, bigramasCifrados);

        int tamanhoFinal = estimarTamanhoDaChave();

        System.out.printf("Tamanho final: %d\n", tamanhoFinal);
    }

    private static int estimarTamanhoDaChave() {
        HashMap<Integer, Integer> votos = new HashMap<>();

        int i = 0;
        for (String key : repeticoesCifradas.keySet()) {

            int v = mdcEntreIndexes(indexRepeticoesCifradas.get(key));

            if (v==1) continue;

            if (!votos.containsKey(v))
                votos.put(v, 0);

            votos.put(v, votos.get(v)+1);

            i++;
            if (i>quantidadeDeRepeticoesAnalisadas) break;
        }

        votos = sortByValueII(votos);

        votos.forEach((k,v) ->
                System.out.printf("key: %s - value: %s \n", k, v));

        Object key0 = votos.keySet().toArray()[0];
        if(votos.size()>1) {
            Object key1 = votos.keySet().toArray()[1];

            Integer tamanho = votos.get(key0);
            if (tamanho.equals(votos.get(key1))) {
                System.out.printf("Tamanho parcial: %s\n", key0);
                quantidadeDeRepeticoesAnalisadas += 5;
                key0 = estimarTamanhoDaChave();
            }
        }

        return (int) key0;
    }

    private static int mdcEntreIndexes(List<Repeticoes> lista) {
        int[] distancias = new int[lista.size()];
        for (int i = lista.size()-1; i > 0; i--) {
            //System.out.printf("lista.get(%d).getInicio(): %d\n",i,lista.get(i).getInicio());
            distancias[i] = lista.get(i).getInicio() - lista.get(i-1).getInicio();
        }

        int mdc = mdcAux(distancias[0], distancias[1]);

        //System.out.println("--------------------");

        for (int i = 2; i < distancias.length; i++) {
            //System.out.printf("distancia[%d]: %d\n",i,distancias[i]);
            mdc = mdcAux(mdc, distancias[i]);
        }

        return mdc;
    }

    private static int mdcAux(int a, int b) {
        while(b != 0){
            int r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    private static HashMap<String, Integer> obterRepticoesCifradas(String cifra) {
        HashMap<String, Integer> retorno = new HashMap<>();
        for (int j = minRepeticoes; j < tamanhoTextos; j++) {
            for (int i = 0; i < cifra.length() - j + 1; i++) {
                String sequencia = cifra.substring(i, i + j);

                // Verificar se a sequência já está no mapa
                if (retorno.containsKey(sequencia)) {
                    retorno.put(sequencia, retorno.get(sequencia) + 1);
                    indexRepeticoesCifradas.get(sequencia).add(new Repeticoes(i, i+j));
                } else {
                    retorno.put(sequencia, 1);
                    ArrayList<Repeticoes> l = new ArrayList<>();
                    l.add(new Repeticoes(i, i+j));
                    indexRepeticoesCifradas.put(sequencia, l);
                }
            }
        }

        return sortByValueS(retorno);
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

    private static String inferirChaveBruto(String textoCifrado, Map<Character, Integer> frequenciasLetras, Map<String, Integer> bigramas) {
        // Tentar todas as combinações possíveis de letras maiúsculas como chave
        for (String chaveCandidata : gerarTodasChavesCandidatas("p")) {
            String textoDescriptografado = descriptografarVigenere(textoCifrado, chaveCandidata);

            // Analisar a qualidade do texto descriptografado
            //Map<Character, Integer> frequenciasDescriptografadas = calcularFrequenciaLetras(textoDescriptografado);
            double chiQuadrado = calcularChiQuadrado(frequenciasLetras, textoDescriptografado);

            // Se o chi-quadrado for baixo, a chave candidata pode ser a correta
            if (chiQuadrado < CHAVE_CANDIDATA_ACEITAVEL) {
                System.out.println("Chave Candidata: " + chaveCandidata + ", Chi-Quadrado: " + chiQuadrado);
                return chaveCandidata;
            }

            //System.out.println(chiQuadrado);
        }



        // Nenhuma chave candidata encontrada
        System.out.println("Nenhuma chave candidata encontrada.");
        return "";
    }

    private static String inferirChaveHill(String textoCifrado, Map<Character, Integer> frequenciasLetras, Map<String, Integer> bigramas) {
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
    private static List<String> gerarTodasChavesCandidatas(String init) {
        List<String> chavesCandidatas = new ArrayList<>();
        StringBuilder chaveCandidata = new StringBuilder();
        chaveCandidata.append(init);

        // Criar uma chave candidata inicial com 56 caracteres vazios
        for (int i = 0; i < 5-init.length(); i++) {
            chaveCandidata.append("-");
        }

        // Gerar todas as combinações recursivamente
        gerarChaveCandidataRecursiva(chavesCandidatas, chaveCandidata, init.length());

        System.out.println("terminei de gerar as chaves");

        return chavesCandidatas;
    }

    private static void gerarChaveCandidataRecursiva(List<String> chavesCandidatas, StringBuilder chaveCandidata, int posicao) {
        //if (posicao!=10)
            //System.out.println("posição: "+posicao);
        if (posicao == 5) {
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
            char letraOriginal = (char) ((letraCifrada - letraChave + 26) % 26 + 'a');
            textoDescriptografado.append(letraOriginal);
        }
        return textoDescriptografado.toString();
    }

    // Método para calcular o chi-quadrado entre duas frequências de letras
    private static double calcularChiQuadrado(Map<Character, Integer> frequenciaEsperada, String texto) {
        double chiQuadradoTotal = 0.0;
        Map<Character, Integer> frequenciaObservada = calcularFrequenciaLetras(texto);
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

    private static void inferirCifra() {
        popularArrayCifrado(cifra1.toCharArray());
        popularArrayCifrado(cifra2.toCharArray());
        popularArrayCifrado(cifra3.toCharArray());

        popularArrayTextos(texto1.toCharArray());

        for(Character c : quantidadeLetrasCifrados.keySet()) {
            int i = quantidadeLetrasCifrados.get(c);

            float f = (float) i /tamanhoTextos*3;

            frequenciaLetrasCifrados.put(c, f);
        }

        frequenciaLetrasCifrados = Cifra.sortByValueF(frequenciaLetrasCifrados);

        for (Character i : quantidadeLetrasCifrados.keySet()) {
            System.out.println(i + " " + quantidadeLetrasCifrados.get(i));
        }


        StringBuilder sb = new StringBuilder();

        System.out.println("========================================================");

        for (Character i : frequenciaLetrasCifrados.keySet()) {
            System.out.println(i + " " + frequenciaLetrasCifrados.get(i));
        }


    }

    public static HashMap<Character, Float> sortByValueF(HashMap<Character, Float> hm)
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

    private static void popularArrayCifrado(char[] arrayCifra) {
        for (char c : arrayCifra) {
            if (!quantidadeLetrasCifrados.containsKey(c))
                quantidadeLetrasCifrados.put(c, 0);
            int i = quantidadeLetrasCifrados.get(c);
            quantidadeLetrasCifrados.put(c, i+1);
            quantidadeLetrasCifrados = sortByValue(quantidadeLetrasCifrados);
        }
    }

    private static void popularArrayTextos(char[] arrayCifra) {
        for (char c : arrayCifra) {
            if (!quantidadeLetrasTextos.containsKey(c))
                quantidadeLetrasTextos.put(c, 0);
            int i = quantidadeLetrasTextos.get(c);
            quantidadeLetrasTextos.put(c, i+1);
            quantidadeLetrasTextos = sortByValue(quantidadeLetrasTextos);
        }
    }

    private static HashMap<Character, Integer> sortByValue(HashMap<Character, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Character, Integer> > list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        Collections.reverse(list);

        // put data from sorted list to hashmap
        HashMap<Character, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static HashMap<String, Integer> sortByValueS(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        Collections.reverse(list);

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static HashMap<Integer, Integer> sortByValueII(HashMap<Integer, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, Integer> > list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        Collections.reverse(list);

        // put data from sorted list to hashmap
        HashMap<Integer, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static String gravarCifra(String texto, String nomeArquivo) throws IOException {
        String cifra = retornaTextoCifrado(texto);
        FileWriter cifraF = new FileWriter(nomeArquivo);
        cifraF.write(cifra);
        cifraF.close();

        return cifra;
    }

    private static void retornaChaveCompleta() throws IOException {
        String chaveTmp = Files.readAllLines(Paths.get("chaves/chave.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        int tamanhoIncialChave = chaveTmp.length();

        StringBuilder sb = new StringBuilder();
        sb.append(chaveTmp);
        char[] letrasDaChave = chaveTmp.toCharArray();

        while (sb.length() < tamanhoTextos) {
            sb.append(letrasDaChave[sb.length() % tamanhoIncialChave]);
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

class Repeticoes {
    private int inicio;
    private int fim;

    public Repeticoes(int inicio, int fim) {
        this.fim = fim;
        this.inicio = inicio;
    }

    public int getFim() {
        return fim;
    }

    public int getInicio() {
        return inicio;
    }

    @Override
    public String toString() {
        return "Repeticoes {" +
                "inicio=" + inicio +
                ", fim=" + fim +
                '}';
    }
}
