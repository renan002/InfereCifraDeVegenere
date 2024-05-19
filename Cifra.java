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
    static HashMap<String, ArrayList<Repeticoes>> indexRepeticoesCifradas = new HashMap<>();
    static char[] frequenciaLetrasAlfabeto = new char[] {'a', 'e', 'o', 's', 'r', 'i', 'n', 'd', 'm', 'u', 't', 'c', 'l', 'p', 'v', 'g', 'h', 'q', 'b', 'f', 'z', 'j', 'x', 'k', 'w', 'y'};
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

        String[] cifras = new String[]{cifra1, cifra2, cifra3};

        String[] textos = new String[]{texto1, texto2, texto3};

        Map[] frequenciasTextos = new HashMap[textos.length];

        for (int i = 0; i < textos.length; i++) {
            frequenciasTextos[i] = calcularFrequenciaLetras(textos[i]);
        }

        System.out.println("===============================================================================================");

        int tamanhoFinal = estimarTamanhoDaChave(cifras);

        System.out.printf("Tamanho final: %d\n", tamanhoFinal);

        int[][] fatiasCifradas = fatiarTextos(cifras, tamanhoFinal);

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        for (int k = 0; k < fatiasCifradas.length; k++) {
            int[] i = fatiasCifradas[k];
            char charMaior = 'a';
            int freqMaior = 0;
            for (int i1 = 0; i1 < i.length; i1++) {
                int j = i[i1];
                System.out.printf("Key: %s - char: %c - Quantidade: %s \n", k, getCharByIndex(i1), j);
                if (j>freqMaior) {
                    freqMaior = j;
                    charMaior = getCharByIndex(i1);
                }
            }
            sb1.append(obterCharFinal(charMaior, frequenciaLetrasAlfabeto[k]));
            sb.append(charMaior);
            System.out.println("=======================================================================");
        }
        //System.out.println(sb1);
        System.out.println(sb);

        inferirChaveHill(cifras, frequenciasTextos, sb.toString());

    }

    private static int estimarTamanhoDaChave(String[] cifras) {
        HashMap<String, Integer> repeticoesCifradas = obterRepticoesCifradas(cifras);
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

        Object key0 = votos.keySet().toArray()[0];
        if(votos.size()>1) {
            Object key1 = votos.keySet().toArray()[1];

            Integer tamanho = votos.get(key0);
            if (tamanho.equals(votos.get(key1))) {
                System.out.printf("Tamanho parcial: %s\n", key0);
                quantidadeDeRepeticoesAnalisadas += 5;
                //minRepeticoes++;
                key0 = estimarTamanhoDaChave(cifras);
            }
        }

        return (int) key0;
    }

    static char obterCharFinal(char a, char b) {
        int ai = getIndexByChar(a);
        int bi = getIndexByChar(b);

        int r = Math.max(ai, bi) - Math.min(ai, bi);

        //while (r >= 26) r-=26;

        return getCharByIndex(r);
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

    private static HashMap<String, Integer> obterRepticoesCifradas(String[] cifras) {
        System.out.printf("Obtendo repetições... Time: %s\n", new Date());
        HashMap<String, Integer> retorno = new HashMap<>();
        for (String cifra : cifras) {
            for (int j = minRepeticoes; j < 50; j++) {
                for (int i = 0; i < cifra.length() - j + 1; i++) {
                    String sequencia = cifra.substring(i, i + j);

                    // Verificar se a sequência já está no mapa
                    if (retorno.containsKey(sequencia)) {
                        retorno.put(sequencia, retorno.get(sequencia) + 1);
                        indexRepeticoesCifradas.get(sequencia).add(new Repeticoes(i, i + j));
                    } else {
                        retorno.put(sequencia, 1);
                        ArrayList<Repeticoes> l = new ArrayList<>();
                        l.add(new Repeticoes(i, i + j));
                        indexRepeticoesCifradas.put(sequencia, l);
                    }
                }
            }
        }

        System.out.printf("Repetições finalizadas Time: %s\n", new Date());

        return sortByValueS(retorno);
    }

    private static int[][] fatiarTextos(String[] cifras, int tamanhoChave) {
        int quantidadeFatias = tamanhoTextos/tamanhoChave;
        String[] fatias = new String[quantidadeFatias];
        int[][] retorno = new int[tamanhoChave][26];
        for (String cifra : cifras) {
            int init = 0;
            for (int i = 0; i < quantidadeFatias; i++) {
                fatias[i] = cifra.substring(init, init += tamanhoChave);
                System.out.println(fatias[i]);
            }

            for (int i = 0; i < tamanhoChave; i++) {
                for (String s : fatias){
                    char c = s.charAt(i);
                    retorno[i][getIndexByChar(c)]++;
                }
            }
        }

        return retorno;
    }

    private static Map<Character, Integer> calcularFrequenciaLetras(String texto) {
        Map<Character, Integer> frequencias = new HashMap<>();
        for (char letra : texto.toLowerCase().toCharArray())
            frequencias.put(letra, frequencias.getOrDefault(letra, 0) + 1);
        return frequencias;
    }

    private static String inferirChaveHill(String[] textosCifrados, Map<Character, Integer>[] frequenciasLetras, String chaveInicial) {

        // Definir parâmetros do Hill-climbing
        int maxIteracoes = 1000; // Número máximo de iterações
        double taxaMutacao = 0.1; // Probabilidade de mutação de um caractere na chave

        // Realizar o Hill-climbing
        for (int iteracao = 0; iteracao < maxIteracoes; iteracao++) {
            // Avaliar a qualidade da chave candidata em todos os textos
            double chiQuadradoTotal = 0.0;
            for (int i = 0; i < textosCifrados.length; i++) {
                String textoCifrado = textosCifrados[i];
                chiQuadradoTotal += calcularChiQuadrado(frequenciasLetras[i], descriptografarVigenere(textoCifrado, chaveInicial));
            }

            // Tentar mutações na chave candidata
            String chaveMutante = gerarChaveMutante(chaveInicial, taxaMutacao);
            double chiQuadradoMutanteTotal = 0.0;
            for (int i = 0; i < textosCifrados.length; i++) {
                String textoCifrado = textosCifrados[i];
                chiQuadradoMutanteTotal += calcularChiQuadrado(frequenciasLetras[i], descriptografarVigenere(textoCifrado, chaveMutante));
            }

            // Se a mutação melhorar o chi-quadrado total, atualizar a chave candidata
            if (chiQuadradoMutanteTotal < chiQuadradoTotal) {
                chaveInicial = chaveMutante;
                System.out.println("Iteração: " + iteracao + ", Chi-Quadrado Total: " + chiQuadradoMutanteTotal + ", Chave: " + chaveInicial);
            }
        }

        return chaveInicial;
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
            int frequenciaEsperadaLetra = frequenciaEsperada.getOrDefault(letra, 0); //TODO frequenciaEsperadaLetra pode ser 0 se não tiver nenhuma ocorrencia da letra no texto
            int frequenciaObservadaLetra = frequenciaObservada.getOrDefault(letra, 0);
            double contribuicaoChiQuadrado = Math.pow((frequenciaEsperadaLetra - frequenciaObservadaLetra), 2) / frequenciaEsperadaLetra;
            chiQuadradoTotal += contribuicaoChiQuadrado;
        }
        return chiQuadradoTotal;
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
