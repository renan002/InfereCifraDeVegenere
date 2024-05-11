import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Cifra {

    static String alfabeto = "abcdefghijklmnopqrstuvwxyz";
    static char[] alfabetos = alfabeto.toCharArray();
    static String chave;
    static int tamanhoTextos = 0;
    static int[] quantidadeLetras = new int[26];

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

        for (int i = 0; i < quantidadeLetras.length; i++) {
            System.out.println(i + " " + quantidadeLetras[i]);
        }

        StringBuilder sb = new StringBuilder();

        System.out.println("========================================================");

        for (int i = 0; i < quantidadeLetras.length; i++) {
            int maior = 0;
            int index = -1;
            for (int j = 0; j < quantidadeLetras.length; j++) {
                //System.out.println(j+" "+quantidadeLetras[j]);
                if (quantidadeLetras[j] >= maior) {
                    maior = quantidadeLetras[j];
                    index = j;
                }
            }
            quantidadeLetras[index] = 0;

            System.out.println(i+" "+getCharByIndex(index) + " " + maior);
            //System.out.println((char) ('a'-1));

        }



    }

    private static int diferencaEntreChars(char a, char b) {
        return getIndexByChar(a) - getIndexByChar(b);
    }

    private static void popularArray(char[] arrayCifra) {
        for (int i = 0; i < tamanhoTextos; i++) {
            int index = getIndexByChar(arrayCifra[i]);
            quantidadeLetras[index] += 1;
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
            System.out.println("chave "+getIndexByChar(chave.charAt(i)));
            System.out.println("texto "+getIndexByChar(texto.charAt(i)));
            System.out.println(i);
            System.out.println(texto.charAt(i));

            while (r >= 26) r-=26;
            sb.append(alfabetos[r]);
        }

        return sb.toString();
    }
}
