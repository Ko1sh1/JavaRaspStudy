package Hook._SystemLoad;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

public class Test {
    public static void main(String[] args) throws Throwable{

        byte[] code= Files.readAllBytes(Paths.get("src/main/java/Hook/_SystemLoad/files/evil.dll"));
        System.out.println(new String(Base64.getEncoder().encode(code)));
//        System.load("R:/languages/Java/study/Rasp/src/main/java/Hook/_SystemLoad/files/evil.dll");
    }
}
