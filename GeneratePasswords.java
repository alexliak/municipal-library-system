import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswords {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("-- Passwords for Municipal Library --");
        System.out.println();
        
        // Generate ALL passwords
        String adminHash = encoder.encode("admin123");
        String librarianHash = encoder.encode("librarian123");  
        String memberHash = encoder.encode("member123");
        
        System.out.println("USE library_db;");
        System.out.println();
        System.out.println("UPDATE users SET password = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password = '" + librarianHash + "' WHERE username = 'librarian';");
        System.out.println("UPDATE users SET password = '" + memberHash + "' WHERE username = 'member';");
    }
}
