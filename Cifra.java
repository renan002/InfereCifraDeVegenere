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

        System.out.printf("Inferindo cifra... Time: %s\n", new Date());

        for (int i = 0; i < textos.length; i++) {
            frequenciasTextos[i] = calcularFrequenciaLetras(textos[i]);
        }

        int tamanhoFinal = estimarTamanhoDaChave(cifras);

        System.out.printf("Tamanho final: %d\n", tamanhoFinal);

        int[][] fatiasCifradas = fatiarTextos(cifras, tamanhoFinal);

        String chaveInicial = obterChaveInicial(fatiasCifradas);

        String chaveFinal = inferirChaveHill(cifras, frequenciasTextos, chaveInicial);

        System.out.printf("Chave Final: %s\n", chaveFinal);

        System.out.printf("Termino da Inferencia Time: %s\n", new Date());
    }

    private static String obterChaveInicial(int[][] fatiasCifradas) {
        StringBuilder sb = new StringBuilder();

        for (int k = 0; k < fatiasCifradas.length; k++) {
            int[] i = fatiasCifradas[k];
            char charMaior = 'a';
            int freqMaior = 0;
            for (int i1 = 0; i1 < i.length; i1++) {
                int j = i[i1];
                //System.out.printf("Key: %s - char: %c - Quantidade: %s \n", k, getCharByIndex(i1), j);
                if (j>freqMaior) {
                    freqMaior = j;
                    charMaior = getCharByIndex(i1);
                }
            }
            sb.append(charMaior);
        }
        return sb.toString();
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

        HashMap<String, Integer> retorno = new HashMap<>();
        for (String cifra : cifras) {
            for (int j = minRepeticoes; j < 50; j++) {
                for (int i = 0; i < cifra.length() - j + 1; i++) {
                    String sequencia = cifra.substring(i, i + j);

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

        int maxIteracoes = 10;
        double chiQuadradoTotal = 0.0;
        int offset = 0;
        do {
            Set<Integer> aumentados = new HashSet<>();
            Set<Integer> diminuidos = new HashSet<>();

            for (int iteracao = 0; iteracao < maxIteracoes; iteracao++) {

                chiQuadradoTotal = 0.0;
                for (int i = 0; i < textosCifrados.length; i++) {
                    String textoCifrado = textosCifrados[i];
                    chiQuadradoTotal += calcularChiQuadrado(frequenciasLetras[i], descriptografarVigenere(textoCifrado, chaveInicial));
                }

                for (int j = 0; j < chaveInicial.length(); j++) {
                    String chaveMutanteMenos = "";
                    String chaveMutanteMais = "";
                    if (!aumentados.contains(j) && !diminuidos.contains(j)) {
                        chaveMutanteMenos = gerarChaveMutante(chaveInicial, j, false, 1+ offset);
                        chaveMutanteMais = gerarChaveMutante(chaveInicial, j, true, 1+ offset);
                    } else if (aumentados.contains(j)) {
                        chaveMutanteMenos = gerarChaveMutante(chaveInicial, j, true, 1+ offset);
                        chaveMutanteMais = gerarChaveMutante(chaveInicial, j, true, 2+ offset);
                    } else if (diminuidos.contains(j)) {
                        chaveMutanteMenos = gerarChaveMutante(chaveInicial, j, false, 1+ offset);
                        chaveMutanteMais = gerarChaveMutante(chaveInicial, j, false, 2+ offset);
                    }
                    double chiQuadradoMutanteTotalMais = 0.0;
                    double chiQuadradoMutanteTotalMenos = 0.0;
                    for (int i = 0; i < textosCifrados.length; i++) {
                        String textoCifrado = textosCifrados[i];
                        chiQuadradoMutanteTotalMais += calcularChiQuadrado(frequenciasLetras[i], descriptografarVigenere(textoCifrado, chaveMutanteMais));
                    }
                    for (int i = 0; i < textosCifrados.length; i++) {
                        String textoCifrado = textosCifrados[i];
                        chiQuadradoMutanteTotalMenos += calcularChiQuadrado(frequenciasLetras[i], descriptografarVigenere(textoCifrado, chaveMutanteMenos));
                    }

                    if (chiQuadradoMutanteTotalMais < chiQuadradoTotal && chiQuadradoMutanteTotalMais < chiQuadradoMutanteTotalMenos) {
                        chaveInicial = chaveMutanteMais;
                        if (!diminuidos.contains(j))
                            aumentados.add(j);
                        //System.out.println("Offet: "+offset+" MAIS - Iteração: " + iteracao + ", Chi-Quadrado Total: " + chiQuadradoMutanteTotalMais + ", Chave: " + chaveInicial);
                        chiQuadradoTotal = chiQuadradoMutanteTotalMais;

                    } else if (chiQuadradoMutanteTotalMenos < chiQuadradoTotal && chiQuadradoMutanteTotalMenos < chiQuadradoMutanteTotalMais) {
                        chaveInicial = chaveMutanteMenos;
                        if (!aumentados.contains(j))
                            diminuidos.add(j);
                        //System.out.println("Offet: "+offset+" MENOS - Iteração: " + iteracao + ", Chi-Quadrado Total: " + chiQuadradoMutanteTotalMenos + ", Chave: " + chaveInicial);
                        chiQuadradoTotal = chiQuadradoMutanteTotalMenos;
                    } else {
                        //diminuidos.remove(j);
                        //aumentados.remove(j);
                    }
                }
            }
            offset++;

            if (offset>tamanhoTextos) break;
        } while (chiQuadradoTotal!=0);

        return chaveInicial;
    }

    private static String gerarChaveMutante(String chaveCandidata, int alterarIndex, boolean aumentar, int quantidade) {
        StringBuilder chaveMutante = new StringBuilder(chaveCandidata);
        char c = chaveCandidata.charAt(alterarIndex);
        if (!aumentar ) {
            chaveMutante.setCharAt(alterarIndex, alfabetos[getIndexByIndexCiclico(getIndexByChar(c)-quantidade)]);
        } else {
            chaveMutante.setCharAt(alterarIndex, alfabetos[getIndexByIndexCiclico(getIndexByChar(c)+quantidade)]);
        }

        return chaveMutante.toString();
    }

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

    private static double calcularChiQuadrado(Map<Character, Integer> frequenciaEsperada, String texto) {
        double chiQuadradoTotal = 0.0;
        Map<Character, Integer> frequenciaObservada = calcularFrequenciaLetras(texto);
        for (char letra : alfabetos) {
            int frequenciaEsperadaLetra = frequenciaEsperada.getOrDefault(letra, 1);
            int frequenciaObservadaLetra = frequenciaObservada.getOrDefault(letra, 1);
            double contribuicaoChiQuadrado = Math.pow((frequenciaEsperadaLetra - frequenciaObservadaLetra), 2) / frequenciaEsperadaLetra;
            chiQuadradoTotal += contribuicaoChiQuadrado;
        }
        return chiQuadradoTotal;
    }

    private static HashMap<String, Integer> sortByValueS(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer> > list =
                new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.comparingByValue());

        Collections.reverse(list);

        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static HashMap<Integer, Integer> sortByValueII(HashMap<Integer, Integer> hm) {
        List<Map.Entry<Integer, Integer> > list =
                new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.comparingByValue());

        Collections.reverse(list);

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

    static int getIndexByIndexCiclico(int c) {

        if (c >= 0 && c <= 25) {
            return c;
        } else if (c < 0 ) {
            while (c < 0) c+=26;
            return c;
        } else {
            while (c >= 26) c-=26;
            return c;
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
