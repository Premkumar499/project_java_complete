// Profile Modal Functionality
function initializeProfileNavbar() {
    // Load user info and update profile button
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
    const profileBtn = document.getElementById('profileBtn');
    
    if (currentUser.username) {
        profileBtn.textContent = currentUser.username;
    } else {
        profileBtn.textContent = 'Profile';
    }
    
    // Smart logo functionality
    const smartLogo = document.getElementById('smartLogo');
    if (smartLogo) {
        smartLogo.addEventListener('click', function() {
            const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
            if (currentUser.id) {
                window.location.href = 'home.html';
            } else {
                window.location.href = 'index.html';
            }
        });
    }
    
    // Profile button click to show modal
    if (profileBtn) {
        profileBtn.addEventListener('click', function(e) {
            e.preventDefault();
            showProfileModal();
        });
    }
    
    // Handle logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            localStorage.removeItem('currentUser');
            localStorage.removeItem('bookingDetails');
            alert('Logged out successfully!');
            window.location.href = 'login.html';
        });
    }
}

function showProfileModal() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
    
    // Create modal HTML
    const modalHTML = `
        <div id="profileModal" class="profile-modal">
            <div class="profile-modal-content">
                <div class="profile-modal-header">
                    <h2>User Profile</h2>
                    <span class="profile-close" onclick="closeProfileModal()">&times;</span>
                </div>
                <div class="profile-modal-body">
                    <div class="profile-section">
                        <h3>Personal Information</h3>
                        <div class="profile-info-grid">
                            <div class="profile-info-item">
                                <span class="profile-info-label">Full Name:</span>
                                <span class="profile-info-value">${currentUser.username || 'Not provided'}</span>
                            </div>
                            <div class="profile-info-item">
                                <span class="profile-info-label">Email:</span>
                                <span class="profile-info-value">${currentUser.email || 'Not provided'}</span>
                            </div>
                            <div class="profile-info-item">
                                <span class="profile-info-label">Phone:</span>
                                <span class="profile-info-value">${currentUser.phoneNumber || 'Not provided'}</span>
                            </div>
                            <div class="profile-info-item">
                                <span class="profile-info-label">Member Since:</span>
                                <span class="profile-info-value">November 2024</span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="profile-actions">
                        <button class="profile-action-btn btn-primary-profile" onclick="editProfile()">Edit Profile</button>
                        <button class="profile-action-btn btn-secondary-profile" onclick="viewBookingHistory()">Booking History</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to body
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    
    // Show modal
    document.getElementById('profileModal').style.display = 'block';
}

function closeProfileModal() {
    const modal = document.getElementById('profileModal');
    if (modal) {
        modal.style.display = 'none';
        modal.remove();
    }
}

function editProfile() {
    alert('Edit Profile feature coming soon!');
    closeProfileModal();
}

function viewBookingHistory() {
    closeProfileModal();
    showBookingHistoryModal();
}

async function showBookingHistoryModal() {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
    
    if (!currentUser.id) {
        alert('Please login to view booking history');
        return;
    }
    
    try {
        const response = await fetch(`http://localhost:8081/api/booking-history?userId=${currentUser.id}`);
        const result = await response.json();
        
        if (!result.success) {
            throw new Error(result.message || 'Failed to fetch booking history');
        }
        
        const bookings = result.data || [];
        
        let bookingHistoryHTML = '';
        if (bookings.length === 0) {
            bookingHistoryHTML = '<p style="text-align: center; color: #666; padding: 2rem;">No booking history found.</p>';
        } else {
            bookingHistoryHTML = bookings.map(booking => `
                <div class="booking-item">
                    <div class="booking-header">
                        <h4>${booking.movieTitle}</h4>
                        <span class="booking-status status-${booking.status.toLowerCase()}">${booking.status}</span>
                    </div>
                    <div class="booking-details">
                        <div class="booking-info">
                            <span class="booking-label">Theater:</span>
                            <span class="booking-value">${booking.theaterName}</span>
                        </div>
                        <div class="booking-info">
                            <span class="booking-label">Show Time:</span>
                            <span class="booking-value">${formatDateTime(booking.showTime)}</span>
                        </div>
                        <div class="booking-info">
                            <span class="booking-label">Seats:</span>
                            <span class="booking-value">${booking.seatNumbers}</span>
                        </div>
                        <div class="booking-info">
                            <span class="booking-label">Amount:</span>
                            <span class="booking-value">₹${booking.totalAmount}</span>
                        </div>
                        <div class="booking-info">
                            <span class="booking-label">Booked On:</span>
                            <span class="booking-value">${formatDateTime(booking.bookingTime)}</span>
                        </div>
                    </div>
                    ${booking.status === 'CONFIRMED' ? `
                        <div class="booking-actions">
                            <button class="cancel-booking-btn" onclick="cancelBooking(${booking.id})">Cancel Booking</button>
                        </div>
                    ` : ''}
                </div>
            `).join('');
        }
        
        const modalHTML = `
            <div id="bookingHistoryModal" class="profile-modal">
                <div class="profile-modal-content booking-history-content">
                    <div class="profile-modal-header">
                        <h2>Booking History</h2>
                        <span class="profile-close" onclick="closeBookingHistoryModal()">&times;</span>
                    </div>
                    <div class="booking-history-body">
                        ${bookingHistoryHTML}
                    </div>
                </div>
            </div>
        `;
        
        // Add modal to body
        document.body.insertAdjacentHTML('beforeend', modalHTML);
        
        // Show modal
        document.getElementById('bookingHistoryModal').style.display = 'block';
        
    } catch (error) {
        console.error('Error fetching booking history:', error);
        alert('Error loading booking history: ' + error.message);
    }
}

function closeBookingHistoryModal() {
    const modal = document.getElementById('bookingHistoryModal');
    if (modal) {
        modal.style.display = 'none';
        modal.remove();
    }
}

async function cancelBooking(bookingId) {
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
    
    if (!confirm('Are you sure you want to cancel this booking?')) {
        return;
    }
    
    try {
        const response = await fetch(`http://localhost:8081/api/booking/${bookingId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('Booking cancelled successfully!');
            closeBookingHistoryModal();
            showBookingHistoryModal(); // Refresh the history
        } else {
            alert('Failed to cancel booking: ' + result.message);
        }
        
    } catch (error) {
        console.error('Error cancelling booking:', error);
        alert('Error cancelling booking: ' + error.message);
    }
}

function formatDateTime(dateTimeString) {
    if (!dateTimeString) return 'N/A';
    
    try {
        const date = new Date(dateTimeString);
        return date.toLocaleString('en-IN', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: true
        });
    } catch (error) {
        return dateTimeString;
    }
}

// Close modal when clicking outside
document.addEventListener('click', function(event) {
    const profileModal = document.getElementById('profileModal');
    const bookingModal = document.getElementById('bookingHistoryModal');
    
    if (event.target == profileModal) {
        closeProfileModal();
    }
    
    if (event.target == bookingModal) {
        closeBookingHistoryModal();
    }
});