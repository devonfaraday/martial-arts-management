// csrf-config.js

// Wait for the document to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Get CSRF token and header name from meta tags
    const token = document.querySelector('meta[name="csrf-token"]').content;
    const header = document.querySelector('meta[name="csrf-header"]').content;

    // Configure HTMX to send CSRF token with every request
    document.body.addEventListener('htmx:configRequest', function(event) {
        event.detail.headers[header] = token;
    });

    // Optional: Handle session timeouts or authentication failures
    document.body.addEventListener('htmx:responseError', function(event) {
        if (event.detail.xhr.status === 403) {
            if (event.detail.xhr.getResponseHeader('HX-Refresh')) {
                window.location.reload();
            }
        }
    });
});