// Encrypt password before sending
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('form.user');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Get form data
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            // In a real application, you would:
            // 1. Hash the password client-side (using crypto-js or similar)
            // 2. Send the hash, not the plain password
            // 3. Use HTTPS for transmission
            
            // For now, we'll at least encode it
            const encodedPassword = btoa(password); // Base64 encode (NOT secure, just for demo)
            
            // Create hidden form with encoded data
            const hiddenForm = document.createElement('form');
            hiddenForm.method = 'POST';
            hiddenForm.action = loginForm.action;
            
            // Add CSRF token
            const csrfInput = loginForm.querySelector('input[name="_csrf"]');
            if (csrfInput) {
                hiddenForm.appendChild(csrfInput.cloneNode());
            }
            
            // Add username
            const usernameInput = document.createElement('input');
            usernameInput.type = 'hidden';
            usernameInput.name = 'username';
            usernameInput.value = username;
            hiddenForm.appendChild(usernameInput);
            
            // Add encoded password
            const passwordInput = document.createElement('input');
            passwordInput.type = 'hidden';
            passwordInput.name = 'password';
            passwordInput.value = encodedPassword;
            hiddenForm.appendChild(passwordInput);
            
            // Submit
            document.body.appendChild(hiddenForm);
            hiddenForm.submit();
        });
    }
});
