import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws Exception{
        byte[] code= Files.readAllBytes(Paths.get("src/main/java/Hook/_SystemLoad/files/evil.dll"));
        System.out.println("-----------------------------------------");
        String payload = new String(Base64.getEncoder().encode(code));
        System.out.println(payload);
        System.out.println("-----------------------------------------");


        String filename = "R:\\evil.dll";
        Files.write(Paths.get(filename), Base64.getDecoder().decode(payload));
        System.load(filename);

//        Runtime.getRuntime().exec(args[0]);
    }
}
