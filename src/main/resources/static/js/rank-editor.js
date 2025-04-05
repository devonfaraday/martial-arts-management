/**
 * Rank Editor JavaScript
 * This file contains the functionality for the rank editor page,
 * including color picker and belt preview.
 */

document.addEventListener('DOMContentLoaded', function() {
    // Get references to elements
    const beltColorPicker = document.getElementById('beltColorPicker');
    const beltColorInput = document.getElementById('beltColor');
    const maxStripesInput = document.getElementById('maxStripes');
    const beltPreview = document.getElementById('beltPreview');
    const stripePreview = document.getElementById('stripePreview');
    
    // Initialize preview
    updateBeltPreview();
    
    // Set up event listeners
    beltColorPicker.addEventListener('input', function() {
        // Update the hidden input field with the selected color
        beltColorInput.value = this.value;
        updateBeltPreview();
    });
    
    // Also update the color picker when the page loads
    if (beltColorInput.value) {
        beltColorPicker.value = beltColorInput.value;
    } else {
        // Set default value if none exists
        beltColorInput.value = beltColorPicker.value;
    }
    
    maxStripesInput.addEventListener('input', updateBeltPreview);
    
    /**
     * Updates the belt preview based on the selected color and max stripes
     */
    function updateBeltPreview() {
        // Update belt color
        const beltColor = beltColorPicker.value || '#ffffff';
        beltColorInput.value = beltColor; // Keep the hidden input in sync
        beltPreview.style.backgroundColor = beltColor;
        
        // Create a text color that contrasts with the belt color
        const colorValue = hexToRgb(beltColor);
        const textColor = isLightColor(colorValue) ? '#000000' : '#ffffff';
        
        // Add the belt name as text
        const beltName = document.getElementById('name').value || 'Belt Preview';
        beltPreview.innerHTML = `<span style="color: ${textColor}; font-weight: bold;">${beltName}</span>`;
        
        // Update stripe preview
        updateStripePreview();
    }
    
    /**
     * Updates the stripe preview based on the max stripes
     */
    function updateStripePreview() {
        stripePreview.innerHTML = '';
        
        const maxStripes = parseInt(maxStripesInput.value) || 0;
        
        // Create stripe preview with different colors based on position
        for (let i = 0; i < maxStripes; i++) {
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
        
        // Add text indicating max stripes
        if (maxStripes > 0) {
            const label = document.createElement('div');
            label.className = 'ml-2 text-sm text-gray-600';
            label.textContent = `Max ${maxStripes} stripe${maxStripes > 1 ? 's' : ''}`;
            stripePreview.appendChild(label);
        } else {
            const label = document.createElement('div');
            label.className = 'text-sm text-gray-600';
            label.textContent = 'No stripes for this rank';
            stripePreview.appendChild(label);
        }
    }
    
    /**
     * Converts a hex color to RGB
     */
    function hexToRgb(hex) {
        // Remove # if present
        hex = hex.replace(/^#/, '');
        
        // Parse the hex values
        const r = parseInt(hex.substring(0, 2), 16);
        const g = parseInt(hex.substring(2, 4), 16);
        const b = parseInt(hex.substring(4, 6), 16);
        
        return { r, g, b };
    }
    
    /**
     * Determines if a color is light or dark
     * Returns true if the color is light
     */
    function isLightColor(color) {
        // Calculate the perceived brightness using the formula
        // (0.299*R + 0.587*G + 0.114*B)
        const brightness = (0.299 * color.r + 0.587 * color.g + 0.114 * color.b) / 255;
        return brightness > 0.5;
    }
});
