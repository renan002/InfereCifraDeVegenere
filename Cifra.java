import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Cifra {

    static String alfabeto = "abcdefghijklmnopqrstuvwxyz";
    static char[] alfabetos = alfabeto.toCharArray();
    static String chave;
    static int tamanhoTextos = 0;
    static HashMap<Character, Integer> quantidadeLetrasCifrados = new HashMap<Character, Integer>();
    static HashMap<Character, Float> frequenciaLetrasCifrados = new HashMap<Character, Float>();

    public static void main(String[] args) throws IOException {

        String texto1 = Files.readAllLines(Paths.get("textos/Texto1.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        String texto2 = Files.readAllLines(Paths.get("textos/Texto2.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        String texto3 = Files.readAllLines(Paths.get("textos/Texto3.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        tamanhoTextos = texto1.length();

        retornaChaveCompleta();

        gravarCifra(texto1, "cifras/cifra1.txt");
        gravarCifra(texto2, "cifras/cifra2.txt");
        gravarCifra(texto3, "cifras/cifra3.txt");

        inferirCifra();
    }

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
