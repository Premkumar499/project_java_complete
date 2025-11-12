// KSR Cinemas - Main JavaScript with Backend Integration
console.log('KSR Cinemas Website Loaded with Backend Integration');

// Backend API configuration
const API_BASE_URL = 'http://localhost:8081/api';

// Smooth scrolling for anchor links
document.addEventListener('DOMContentLoaded', function() {
    // Check authentication before initializing app
    checkAuthenticationAndRedirect();
    
    // Initialize app
    initializeApp();
    
    // Smooth scrolling for navigation links
    const links = document.querySelectorAll('a[href^="#"]');
    
    links.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Add active class to navigation items
    const navLinks = document.querySelectorAll('.nav-menu a');
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPage) {
            link.classList.add('active');
        }
    });

    // Form validation if forms exist
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            handleFormSubmit(this);
        });
    });
    
    // Add logout button functionality
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            handleLogout();
        });
    }
    
    // Load movies if on movies page or home page
    if (window.location.pathname.includes('movies.html') || window.location.pathname.includes('home.html')) {
        loadMoviesFromAPI();
    }
});

// Check authentication and redirect accordingly
function checkAuthenticationAndRedirect() {
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    const currentUser = getStoredUser();
    
    // If accessing root directory or index without authentication, redirect to login
    if (!currentUser && (currentPage === '' || currentPage === 'index.html' || window.location.pathname === '/')) {
        console.log('Accessing root without authentication, redirecting to login page');
        window.location.href = 'login.html';
        return;
    }
    
    // If user is not logged in and not on login/signup page, redirect to login
    if (!currentUser && !currentPage.includes('login.html') && !currentPage.includes('signup.html')) {
        console.log('User not authenticated, redirecting to login page');
        window.location.href = 'login.html';
        return;
    }
    
    // If user is logged in and on login page, redirect to home
    if (currentUser && currentPage.includes('login.html')) {
        console.log('User already authenticated, redirecting to home page');
        window.location.href = 'home.html';
        return;
    }
}

// Initialize application
function initializeApp() {
    // Check if user is logged in
    const currentUser = getStoredUser();
    if (currentUser) {
        updateUIForLoggedInUser(currentUser);
    }
}

// API Helper Functions
async function apiCall(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    const finalOptions = { ...defaultOptions, ...options };
    
    try {
        const response = await fetch(url, finalOptions);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('API Error:', error);
        showMessage('Connection error. Please check if the server is running.', 'error');
        return { status: 'error', message: 'Connection failed' };
    }
}

// Enhanced Form handling with backend integration
async function handleFormSubmit(form) {
    const formId = form.id;
    
    switch(formId) {
        case 'loginForm':
            await handleLogin(form);
            break;
        case 'signupForm':
            await handleSignup(form);
            break;
        default:
            validateForm(form);
    }
}

// Handle user login with backend
async function handleLogin(form) {
    const formData = new FormData(form);
    const credentials = {
        email: formData.get('email'),
        password: formData.get('password')
    };
    
    if (!credentials.email || !credentials.password) {
        showMessage('Please fill in all fields', 'error');
        return;
    }
    
    showMessage('Verifying credentials with database...', 'info');
    
    const response = await apiCall('/login', {
        method: 'POST',
        body: JSON.stringify(credentials)
    });
    
    console.log('Login response:', response);
    
    if (response.status === 'success') {
        // Store user data
        storeUser(response.user);
        showMessage('Login successful! Redirecting to home page...', 'success');
        setTimeout(() => {
            window.location.href = 'home.html';
        }, 1500);
    } else {
        showMessage(response.message || 'Invalid email or password. Please try again.', 'error');
    }
}

// Handle user registration with backend
async function handleSignup(form) {
    // Validate form first
    if (!validateSignupForm(form)) {
        return;
    }
    
    const formData = new FormData(form);
    const userData = {
        fullName: formData.get('fullName'),
        email: formData.get('email'),
        phone: formData.get('phone'),
        password: formData.get('password')
    };
    
    showMessage('Creating account...', 'info');
    
    const response = await apiCall('/register', {
        method: 'POST',
        body: JSON.stringify(userData)
    });
    
    if (response.status === 'success') {
        showMessage(response.message, 'success');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
    } else {
        showMessage(response.message || 'Registration failed', 'error');
    }
}

// Validate signup form
function validateSignupForm(form) {
    const password = form.querySelector('#password').value;
    const confirmPassword = form.querySelector('#confirmPassword').value;
    const phone = form.querySelector('#phone').value;
    
    // Phone validation
    const phoneRegex = /^[0-9]{10}$/;
    if (!phoneRegex.test(phone.replace(/\D/g, ''))) {
        showError(form.querySelector('#phone'), 'Please enter a valid 10-digit phone number');
        return false;
    }
    
    // Password confirmation
    if (password !== confirmPassword) {
        showError(form.querySelector('#confirmPassword'), 'Passwords do not match');
        return false;
    }
    
    return true;
}

// Load movies from backend API
async function loadMoviesFromAPI() {
    const moviesContainer = document.querySelector('.movies-grid');
    if (!moviesContainer) return;
    
    showMessage('Loading movies...', 'info');
    
    const response = await apiCall('/movies');
    
    if (response && response.movies) {
        displayMovies(response.movies);
        hideMessage();
    } else if (response && Array.isArray(response)) {
        displayMovies(response);
        hideMessage();
    } else {
        // Fallback to static content if API fails
        showMessage('Unable to load movies. Please try again later.', 'error');
        setTimeout(() => hideMessage(), 3000);
    }
}

// Display movies in the grid
function displayMovies(movies) {
    const moviesContainer = document.querySelector('.movies-grid');
    if (!moviesContainer) return;
    
    moviesContainer.innerHTML = movies.map(movie => `
        <div class="movie-card" data-movie-id="${movie.id}">
            <div class="movie-poster">${movie.title}</div>
            <div class="movie-info">
                <h3>${movie.title}</h3>
                <p>${movie.description || 'An exciting movie experience awaits you.'}</p>
                <p><strong>Duration:</strong> ${movie.duration} min | <strong>Genre:</strong> ${movie.genre || 'Entertainment'}</p>
                <a href="show.html?movieId=${movie.id}" class="btn btn-primary">Book Tickets</a>
            </div>
        </div>
    `).join('');
}

// User management functions
function storeUser(userData) {
    localStorage.setItem('currentUser', JSON.stringify(userData));
}

function getStoredUser() {
    const userData = localStorage.getItem('currentUser');
    return userData ? JSON.parse(userData) : null;
}

function clearStoredUser() {
    localStorage.removeItem('currentUser');
}

function updateUIForLoggedInUser(user) {
    // Update navigation to show logout instead of login/signup
    const navMenu = document.querySelector('.nav-menu');
    if (navMenu) {
        const loginLink = navMenu.querySelector('a[href="login.html"]');
        const signupLink = navMenu.querySelector('a[href="signup.html"]');
        
        if (loginLink) {
            loginLink.textContent = `Welcome, ${user.fullName || user.username || user.email}`;
            loginLink.href = '#';
            loginLink.style.color = '#e94560';
            loginLink.style.cursor = 'default';
        }
        
        if (signupLink) {
            signupLink.textContent = 'Logout';
            signupLink.href = '#';
            signupLink.style.color = '#fff';
            signupLink.style.backgroundColor = '#e94560';
            signupLink.style.padding = '8px 15px';
            signupLink.style.borderRadius = '5px';
            signupLink.addEventListener('click', function(e) {
                e.preventDefault();
                handleLogout();
            });
        }
    }
}

function handleLogout() {
    clearStoredUser();
    showMessage('Logged out successfully', 'success');
    setTimeout(() => {
        window.location.href = 'login.html';
    }, 1000);
}

function clearStoredUser() {
    localStorage.removeItem('currentUser');
}

function updateUIForLoggedInUser(user) {
    // Update navigation to show user info
    const navMenu = document.querySelector('.nav-menu');
    if (navMenu) {
        const loginLink = navMenu.querySelector('a[href="login.html"]');
        if (loginLink) {
            loginLink.innerHTML = `Welcome, ${user.username}`;
            loginLink.href = '#';
            loginLink.addEventListener('click', handleLogout);
        }
    }
}

function handleLogout(e) {
    e.preventDefault();
    clearStoredUser();
    showMessage('Logged out successfully', 'success');
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 1500);
}

// Enhanced booking function with backend integration
async function proceedToBooking() {
    const selectedSeats = Array.from(document.querySelectorAll('.seat.selected')).map(seat => seat.textContent);
    const currentUser = getStoredUser();
    
    if (!currentUser) {
        showMessage('Please login to book tickets', 'error');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
        return;
    }
    
    if (selectedSeats.length === 0) {
        showMessage('Please select at least one seat', 'error');
        return;
    }
    
    const bookingData = {
        userId: currentUser.id,
        showtimeId: getSelectedShowtimeId(), // You'll need to implement this
        seatNumbers: selectedSeats,
        totalPrice: selectedSeats.length * 150
    };
    
    showMessage('Processing booking...', 'info');
    
    const response = await apiCall('/book', {
        method: 'POST',
        body: JSON.stringify(bookingData)
    });
    
    if (response.status === 'success') {
        // Store booking details for confirmation page
        localStorage.setItem('bookingDetails', JSON.stringify({
            movie: 'Action Adventure', // You'll get this from the page
            showtime: '7:00 PM',
            seats: selectedSeats,
            totalPrice: bookingData.totalPrice,
            bookingId: response.bookingId
        }));
        
        showMessage('Booking successful!', 'success');
        setTimeout(() => {
            window.location.href = 'confirmation.html';
        }, 1500);
    } else {
        showMessage(response.message || 'Booking failed', 'error');
    }
}

// Helper function to get selected showtime (you'll need to implement based on your UI)
function getSelectedShowtimeId() {
    // This should return the ID of the selected showtime
    // For now, return a default value
    return 1;
}

// Form validation function (existing)
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            showError(input, 'This field is required');
            isValid = false;
        } else {
            clearError(input);
        }
        
        // Email validation
        if (input.type === 'email' && input.value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(input.value)) {
                showError(input, 'Please enter a valid email address');
                isValid = false;
            }
        }
        
        // Password validation
        if (input.type === 'password' && input.value) {
            if (input.value.length < 6) {
                showError(input, 'Password must be at least 6 characters long');
                isValid = false;
            }
        }
    });
    
    if (isValid) {
        showMessage('Form submitted successfully!', 'success');
    }
}

// Show error message
function showError(input, message) {
    clearError(input);
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    errorDiv.style.color = '#e94560';
    errorDiv.style.fontSize = '0.9rem';
    errorDiv.style.marginTop = '0.25rem';
    input.parentNode.appendChild(errorDiv);
    input.style.borderColor = '#e94560';
}

// Clear error message
function clearError(input) {
    const errorMsg = input.parentNode.querySelector('.error-message');
    if (errorMsg) {
        errorMsg.remove();
    }
    input.style.borderColor = '#e1e1e1';
}

// Show success/info message
function showMessage(message, type = 'info') {
    // Remove any existing messages
    const existingMessage = document.querySelector('.message');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `message message-${type}`;
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        font-weight: 500;
        z-index: 1000;
        animation: slideIn 0.3s ease;
        max-width: 300px;
    `;
    
    if (type === 'success') {
        messageDiv.style.background = '#4caf50';
    } else if (type === 'error') {
        messageDiv.style.background = '#e94560';
    } else {
        messageDiv.style.background = '#2196f3';
    }
    
    document.body.appendChild(messageDiv);
    
    if (type !== 'info') {
        setTimeout(() => {
            messageDiv.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => messageDiv.remove(), 300);
        }, 3000);
    }
}

function hideMessage() {
    const messageDiv = document.querySelector('.message');
    if (messageDiv) {
        messageDiv.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => messageDiv.remove(), 300);
    }
}

// Seat selection for seats.html
function initSeatSelection() {
    const seats = document.querySelectorAll('.seat.available');
    const selectedSeats = [];
    
    seats.forEach(seat => {
        seat.addEventListener('click', function() {
            if (this.classList.contains('selected')) {
                this.classList.remove('selected');
                const index = selectedSeats.indexOf(this.textContent);
                if (index > -1) {
                    selectedSeats.splice(index, 1);
                }
            } else {
                this.classList.add('selected');
                selectedSeats.push(this.textContent);
            }
            updateBookingInfo(selectedSeats);
        });
    });
}

// Update booking information
function updateBookingInfo(seats) {
    const bookingInfo = document.querySelector('.booking-info');
    const proceedBtn = document.getElementById('proceedBtn');
    
    if (bookingInfo) {
        const seatCount = seats.length;
        const pricePerSeat = 150;
        const totalPrice = seatCount * pricePerSeat;
        
        bookingInfo.innerHTML = `
            <h3>Booking Summary</h3>
            <p>Selected Seats: ${seats.join(', ') || 'None'}</p>
            <p>Number of Seats: ${seatCount}</p>
            <p>Price per Seat: ₹${pricePerSeat}</p>
            <p><strong>Total Price: ₹${totalPrice}</strong></p>
        `;
        
        if (proceedBtn) {
            proceedBtn.disabled = seatCount === 0;
        }
    }
}

// Initialize seat selection if on seats page
if (window.location.pathname.includes('seats.html')) {
    document.addEventListener('DOMContentLoaded', initSeatSelection);
}

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .nav-menu a.active {
        color: #e94560 !important;
        background-color: rgba(233, 69, 96, 0.1) !important;
    }
`;
document.head.appendChild(style);