/**
 * Rank Management JavaScript
 * This file contains the minimal JavaScript needed for the rank management functionality.
 * It handles the stripe preview functionality when selecting ranks and stripes.
 */

// Function to update the stripe preview based on selected rank and stripe count
function updateStripePreview() {
    const stripesInput = document.getElementById('stripes');
    const rankSelect = document.getElementById('rankId');
    const stripePreview = document.getElementById('stripePreview');
    
    if (!stripesInput || !rankSelect || !stripePreview) {
        console.error('Required elements not found for stripe preview');
        return;
    }
    
    const stripeCount = parseInt(stripesInput.value) || 0;
    stripePreview.innerHTML = '';
    
    // Get selected rank's max stripes
    const selectedOption = rankSelect.options[rankSelect.selectedIndex];
    const maxStripes = selectedOption ? parseInt(selectedOption.getAttribute('data-max-stripes')) || 0 : 0;
    
    // Update max attribute on input
    stripesInput.setAttribute('max', maxStripes);
    
    // If stripes is more than max, reset to max
    if (stripeCount > maxStripes) {
        stripesInput.value = maxStripes;
    }
    
    // Create stripe preview with different colors based on position
    for (let i = 0; i < Math.min(stripeCount, maxStripes); i++) {
        const stripe = document.createElement('div');
        
        // Determine stripe color based on position
        let stripeColor = 'bg-white';
        if (i >= 8) {
            // Stripes 9-12 are black
            stripeColor = 'bg-black';
        } else if (i >= 4) {
            // Stripes 5-8 are red
            stripeColor = 'bg-red-600';
        }
        
        stripe.className = `w-1.5 h-6 ${stripeColor} border border-gray-300 mx-0.5`;
        stripePreview.appendChild(stripe);
    }
    
    // No legend text needed
}

// Set up event listeners when the page loads
document.addEventListener('DOMContentLoaded', function() {
    // Initialize stripe preview when the page loads
    updateStripePreview();
    
    // Close modal when pressing Escape key
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            document.getElementById('rankModal').classList.add('hidden');
        }
    });
    
    // Close modal when clicking outside of it
    const modals = document.querySelectorAll('.fixed.inset-0');
    modals.forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.add('hidden');
            }
        });
    });
});
