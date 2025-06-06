# Client-Side Password Hashing Implementation

## Enterprise Systems Development - Academic Project

### Overview
This document describes the client-side password hashing implementation added to the Municipal Library Management System as part of the Enterprise Systems Development course requirements.

### Why Client-Side Hashing?

While HTTPS encryption provides the primary security for password transmission in production systems, this academic implementation demonstrates:

1. **Security Awareness**: Understanding of password visibility in browser developer tools
2. **Cryptographic Concepts**: Practical application of SHA-256 hashing
3. **Enterprise Patterns**: Custom authentication providers in Spring Security
4. **Academic Excellence**: Going beyond basic requirements to explore advanced security concepts

### Implementation Details

#### 1. Client-Side (login.html)
- **Library**: CryptoJS 4.2.0 (CDN hosted)
- **Algorithm**: SHA-256
- **Process**: Password is hashed before form submission
- **Result**: Network tab shows hash instead of plaintext password

```javascript
var hashedPassword = CryptoJS.SHA256(password).toString();
```

#### 2. Server-Side (HashedPasswordAuthenticationProvider.java)
- **Pattern**: Custom Spring Security Authentication Provider
- **Verification**: Compares client hash with server-computed hash
- **Limitation**: Works with known demo passwords only
- **Purpose**: Academic demonstration of the concept

### Security Considerations

**IMPORTANT**: This implementation is for ACADEMIC PURPOSES ONLY and should NOT be used in production because:

1. **No Salt**: The hash uses no salt, making it vulnerable to rainbow table attacks
2. **Client Trust**: Server must trust the client's hashing implementation
3. **Replay Attacks**: Hashed password can be captured and replayed
4. **Limited Scope**: Only works with predefined demo passwords

### Production Recommendations

For real-world enterprise applications:
1. Use HTTPS for all authentication endpoints
2. Implement server-side BCrypt hashing (already present in the project)
3. Consider OAuth2 or JWT tokens for stateless authentication
4. Never rely on client-side security measures alone

### Educational Value

This implementation demonstrates:
- Understanding of browser security model
- Password handling in modern web applications
- Spring Security customization capabilities
- Trade-offs between security and usability

### Testing

1. Open Chrome Developer Tools → Network tab
2. Login with demo credentials (e.g., admin/admin123)
3. Observe the Form Data in the login request
4. Password field now shows SHA-256 hash instead of "admin123"

### References

1. OWASP Authentication Cheat Sheet
2. Spring Security Reference Documentation
3. CryptoJS Documentation
4. Enterprise Application Security Best Practices

---

*Note: This implementation was created for the SWE6002 Enterprise Systems Development module to demonstrate security concepts in an academic context.*
