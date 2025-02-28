document.addEventListener('DOMContentLoaded', async function() {
    try {
        // Get the school ID from the URL
        const pathParts = window.location.pathname.split('/');
        const schoolIdIndex = pathParts.indexOf('embedded-onboard') - 1;
        const schoolId = pathParts[schoolIdIndex];
        
        if (!schoolId) {
            showError('Could not determine school ID');
            return;
        }
        
        // Get CSRF token
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        
        // Create session
        const response = await fetch(`/schools/connect/${schoolId}/create-session`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to create onboarding session');
        }
        
        const data = await response.json();
        
        if (!data.url) {
            throw new Error('No URL returned from server');
        }
        
        // Instead of embedding, redirect to the Stripe-hosted onboarding
        window.location.href = data.url;
        
    } catch (error) {
        console.error('Error:', error);
        showError(error.message || 'An error occurred during onboarding setup');
    }
});

function showError(message) {
    const container = document.getElementById('onboarding-container');
    if (container) {
        container.innerHTML = `
            <div class="flex items-center justify-center h-full">
                <div class="text-center">
                    <div class="text-red-500 mb-4">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                    <p class="text-red-600 font-medium">${message}</p>
                    <p class="text-gray-500 mt-2">Please try again or contact support.</p>
                </div>
            </div>
        `;
    }
}
