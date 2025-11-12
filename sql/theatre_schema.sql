-- KSR Cinemas Database Schema

CREATE DATABASE IF NOT EXISTS ksr_cinema;
USE ksr_cinema;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Movies table
CREATE TABLE movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    genre VARCHAR(50),
    duration INT NOT NULL, -- in minutes
    release_date DATE,
    language VARCHAR(30),
    rating VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Theaters/Screens table
CREATE TABLE theaters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    total_seats INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Showtimes table
CREATE TABLE showtimes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    theater_name VARCHAR(100) NOT NULL,
    show_time DATETIME NOT NULL,
    price DECIMAL(8,2) NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Seats table
CREATE TABLE seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    is_booked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_seat_showtime (showtime_id, seat_number)
);

-- Bookings table
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    showtime_id INT NOT NULL,
    seat_numbers VARCHAR(255) NOT NULL, -- comma separated seat numbers
    total_amount DECIMAL(10,2) NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('CONFIRMED', 'CANCELLED') DEFAULT 'CONFIRMED',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE
);

-- Insert sample data
INSERT INTO movies (title, description, genre, duration, release_date, language, rating) VALUES
('Avengers: Endgame', 'The culmination of the Marvel Cinematic Universe saga.', 'Action', 181, '2019-04-26', 'English', 'PG-13'),
('Pushpa: The Rise', 'A rugged laborer rises through the ranks of a red sandalwood smuggling syndicate.', 'Action', 179, '2021-12-17', 'Telugu', 'U/A'),
('RRR', 'A fictional story about two legendary revolutionaries and their journey away from home.', 'Period Drama', 187, '2022-03-25', 'Telugu', 'U/A'),
('Spider-Man: No Way Home', 'Peter Parker seeks Doctor Strange\'s help to make everyone forget his identity as Spider-Man.', 'Action', 148, '2021-12-17', 'English', 'PG-13'),
('KGF Chapter 2', 'Rocky continues his rise to power as the king of the Kolar Gold Fields.', 'Action', 168, '2022-04-14', 'Kannada', 'U/A');

INSERT INTO theaters (name, total_seats) VALUES
('Screen 1', 150),
('Screen 2', 200),
('Screen 3', 120);

-- Insert showtimes for today and tomorrow
INSERT INTO showtimes (movie_id, theater_name, show_time, price, total_seats, available_seats) VALUES
-- Today's shows
(1, 'Screen 1', CONCAT(CURDATE(), ' 10:00:00'), 250.00, 150, 150),
(1, 'Screen 1', CONCAT(CURDATE(), ' 14:00:00'), 300.00, 150, 150),
(1, 'Screen 1', CONCAT(CURDATE(), ' 18:00:00'), 350.00, 150, 150),
(2, 'Screen 2', CONCAT(CURDATE(), ' 11:00:00'), 200.00, 200, 200),
(2, 'Screen 2', CONCAT(CURDATE(), ' 15:00:00'), 250.00, 200, 200),
(2, 'Screen 2', CONCAT(CURDATE(), ' 19:00:00'), 300.00, 200, 200),
(3, 'Screen 3', CONCAT(CURDATE(), ' 12:00:00'), 280.00, 120, 120),
(3, 'Screen 3', CONCAT(CURDATE(), ' 16:00:00'), 320.00, 120, 120),
(3, 'Screen 3', CONCAT(CURDATE(), ' 20:00:00'), 380.00, 120, 120),
-- Tomorrow's shows
(4, 'Screen 1', CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 10:30:00'), 220.00, 150, 150),
(4, 'Screen 1', CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 14:30:00'), 270.00, 150, 150),
(4, 'Screen 1', CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 18:30:00'), 320.00, 150, 150),
(5, 'Screen 2', CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 11:30:00'), 240.00, 200, 200),
(5, 'Screen 2', CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 15:30:00'), 290.00, 200, 200),
(5, 'Screen 2', CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 19:30:00'), 340.00, 200, 200);

-- Generate seats for each showtime
DELIMITER //
CREATE PROCEDURE GenerateSeats()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE showtime_id INT;
    DECLARE total_seats INT;
    DECLARE seat_counter INT;
    DECLARE row_letter CHAR(1);
    DECLARE seat_num INT;
    
    DECLARE showtime_cursor CURSOR FOR
        SELECT id, total_seats FROM showtimes;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN showtime_cursor;
    
    showtime_loop: LOOP
        FETCH showtime_cursor INTO showtime_id, total_seats;
        
        IF done THEN
            LEAVE showtime_loop;
        END IF;
        
        SET seat_counter = 1;
        
        WHILE seat_counter <= total_seats DO
            SET row_letter = CHAR(65 + FLOOR((seat_counter - 1) / 10)); -- A, B, C, etc.
            SET seat_num = ((seat_counter - 1) % 10) + 1; -- 1 to 10
            
            INSERT INTO seats (showtime_id, seat_number, is_booked)
            VALUES (showtime_id, CONCAT(row_letter, seat_num), FALSE);
            
            SET seat_counter = seat_counter + 1;
        END WHILE;
        
    END LOOP;
    
    CLOSE showtime_cursor;
END//
DELIMITER ;

CALL GenerateSeats();
DROP PROCEDURE GenerateSeats;

-- Create a sample user for testing
INSERT INTO users (username, email, password, phone_number) VALUES
('testuser', 'test@example.com', 'password123', '1234567890'),
('admin', 'admin@ksrcinemas.com', 'admin123', '9876543210');