import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Cifra {

    static String alfabeto = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) throws IOException {

        char[] alfabetos = alfabeto.toCharArray();

        String texto1 = Files.readAllLines(Paths.get("textos/Texto1.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        System.out.println(texto1);
        String texto2 = Files.readAllLines(Paths.get("textos/Texto2.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        System.out.println(texto2);
        String texto3 = Files.readAllLines(Paths.get("textos/Texto3.txt"), Charset.defaultCharset()).get(0).toLowerCase();
        System.out.println(texto3);

        String chave = Files.readAllLines(Paths.get("textos/chave.txt"), Charset.defaultCharset()).get(0).toLowerCase();

        char a = 'a';
        char z = 'z';
        char ii = 'i';
        char c = 'c';
        char l = 'k';

        System.out.println(Character.getNumericValue(a));
        System.out.println(Character.getNumericValue(z));

        System.out.println(Character.getNumericValue('i'));
        System.out.println(Character.getNumericValue('c'));

        System.out.println(chave);
        StringBuilder sb = new StringBuilder();
        sb.append(chave);
        char[] letrasDaChave = chave.toCharArray();
        char[] letrasDoTexto1 = texto1.toCharArray();
        char[] letrasDoTexto2 = texto2.toCharArray();
        char[] letrasDoTexto3 = texto3.toCharArray();

        while (sb.length() < texto1.length()) {
            sb.append(letrasDaChave[sb.length() % 5]);
        }
        chave = sb.toString();

        System.out.println(chave);

        char chave0 = chave.charAt(0);
        char texto10 = texto1.charAt(0);

        int r = chave0 + texto10;

        while (r > 123) r-=25;

        System.out.println(getCharIndex('z'));
    }

    static int getCharIndex(char c) {
        int asciiValue = (int) c;

        if (asciiValue >= 'a' && asciiValue <= 'z') {
            return asciiValue - 'a';
        } else if (asciiValue >= 'A' && asciiValue <= 'Z') {
            return asciiValue - 'A';
        } else {
            return -1;
        }
    }
}
