import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Cifra {

    static String alfabeto = "abcdefghijklmnopqrstuvwxyz";
    static char[] alfabetos = alfabeto.toCharArray();
    static String chave;
    static final int tamanhoTextos = 16;

    public static void main(String[] args) throws IOException {

        String texto1 = Files.readAllLines(Paths.get("textos/Texto1.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        String texto2 = Files.readAllLines(Paths.get("textos/Texto2.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        String texto3 = Files.readAllLines(Paths.get("textos/Texto3.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        retornChaveCompleta();

        gravarCifra(texto1, "cifras/cifra1.txt");
        gravarCifra(texto2, "cifras/cifra2.txt");
        gravarCifra(texto3, "cifras/cifra3.txt");

    }

    private static void gravarCifra(String texto, String nomeArquivo) throws IOException {
        FileWriter cifra = new FileWriter(nomeArquivo);
        cifra.write(retornaTextoCifrado(texto));
        cifra.close();
    }

    private static void retornChaveCompleta() throws IOException {
        String chaveTmp = Files.readAllLines(Paths.get("textos/chave.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        StringBuilder sb = new StringBuilder();
        sb.append(chaveTmp);
        char[] letrasDaChave = chaveTmp.toCharArray();

        while (sb.length() < tamanhoTextos) {
            sb.append(letrasDaChave[sb.length() % 5]);
        }
        chave = sb.toString();
    }

    static int getCharIndex(char c) {

        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else {
            return -1;
        }
    }

    static String retornaTextoCifrado(String texto) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {

            int r = getCharIndex(chave.charAt(i)) + getCharIndex(texto.charAt(i));

            while (r >= 26) r-=26;
            sb.append(alfabetos[r]);
        }

        return sb.toString();
    }
}
