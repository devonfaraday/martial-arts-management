document.addEventListener('DOMContentLoaded', function() {
    // Enable/disable the Connect with Stripe button based on checkbox
    const termsCheckbox = document.getElementById('stripeTerms');
    const connectButton = document.getElementById('connectButton');
    
    if (termsCheckbox && connectButton) {
        termsCheckbox.addEventListener('change', function() {
            connectButton.disabled = !this.checked;
        });
    }
    
    // Handle copy to clipboard functionality
    window.copyToClipboard = function(element) {
        if (element) {
            element.select();
            document.execCommand('copy');
            
            // Show a tooltip or some indication that the copy was successful
            const tooltip = document.createElement('div');
            tooltip.className = 'tooltip bg-dark text-white px-2 py-1 rounded position-absolute';
            tooltip.style.zIndex = '1000';
            tooltip.textContent = 'Copied!';
            
            element.parentNode.appendChild(tooltip);
            
            // Position the tooltip
            const rect = element.getBoundingClientRect();
            tooltip.style.top = (rect.top - 30) + 'px';
            tooltip.style.left = (rect.left + rect.width / 2 - 30) + 'px';
            
            // Remove the tooltip after a short delay
            setTimeout(function() {
                tooltip.remove();
            }, 1500);
        }
    };
});
