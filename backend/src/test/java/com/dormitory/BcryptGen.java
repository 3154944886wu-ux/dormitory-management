import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        for (String arg : args) {
            System.out.println(arg + " -> " + encoder.encode(arg));
        }
    }
}
