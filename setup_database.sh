#!/bin/bash

echo "Setting up KSR Cinema Database..."
echo "Please enter MySQL root password when prompted:"

mysql -u root -p << EOF
SOURCE /home/premkumar/Downloads/KSR-Cinemas-Web/sql/theatre_schema.sql;
SHOW DATABASES;
USE ksr_cinema;
SHOW TABLES;
SELECT COUNT(*) as 'Total Movies' FROM movies;
SELECT COUNT(*) as 'Total Users' FROM users;
SELECT COUNT(*) as 'Total Showtimes' FROM showtimes;
EOF

echo "Database setup completed!"