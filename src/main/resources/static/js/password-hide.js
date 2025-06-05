// Password obfuscation for academic requirements
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('form');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const passwordField = document.getElementById('password');
            const usernameField = document.getElementById('username');
            
            if (passwordField && usernameField) {
                // Create FormData with obfuscated password field name
                const formData = new FormData();
                formData.append('username', usernameField.value);
                formData.append('p', passwordField.value); // Use 'p' instead of 'password'
                
                // Add CSRF token
                const csrfToken = document.querySelector('input[name="_csrf"]');
                if (csrfToken) {
                    formData.append('_csrf', csrfToken.value);
                }
                
                // Submit with fetch
                fetch(loginForm.action, {
                    method: 'POST',
                    body: formData,
                    credentials: 'same-origin'
                }).then(response => {
                    if (response.redirected) {
                        window.location.href = response.url;
                    }
                });
                
                // Clear the password field
                passwordField.value = '';
            }
        });
    }
});
