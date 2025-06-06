// Debug script for checkout form
$(document).ready(function() {
    console.log("Checkout debug script loaded");
    
    // Log form submission
    $('form').on('submit', function(e) {
        console.log("Form submission triggered");
        console.log("Form action:", $(this).attr('action'));
        console.log("Form method:", $(this).attr('method'));
        console.log("Form data:", $(this).serialize());
        
        // Check CSRF token
        var csrfToken = $('input[name="_csrf"]').val();
        console.log("CSRF token present:", !!csrfToken);
        
        // Don't prevent default - let the form submit normally
    });
    
    // Monitor any AJAX errors
    $(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
        console.error("AJAX Error:", thrownError);
        console.error("Status:", jqXHR.status);
        console.error("Response:", jqXHR.responseText);
    });
});
