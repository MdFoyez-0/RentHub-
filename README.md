RentHub – Rental Property Management System
Overview

RentHub is a full-stack rental property management system built to manage interactions between landlords, tenants, and administrators.
The system allows property listing, rental requests, and administrative moderation through a centralized platform.

This project demonstrates Spring Boot backend development, RESTful APIs, and static frontend integration using HTML, CSS, and JavaScript.

## Features
- **👨‍💼 Landlord Dashboard**: Add/edit properties, manage rental requests, view statistics
- **👤 Tenant Dashboard**: Browse approved properties, submit rental requests, favorite listings
- **🔧 Admin Dashboard**: Approve/reject properties, manage users, system oversight
- **🔐 Role-based Authentication**: Secure login for each user type
- **🏠 Property Management**: Full CRUD operations for property listings
- **📋 Rental Requests**: Submit, approve, or reject rental applications
- **📊 Real-time Statistics**: Interactive dashboards with live data
- **📱 Responsive Design**: Works on desktop, tablet, and mobile

Tech Stack
Backend

Java

Spring Boot

Spring Data JPA (Hibernate)

MySQL

Maven

Frontend

HTML

CSS

JavaScript

Project Structure
RentHub/
├── frontend/
│   ├── index.html
│   ├── css/
│   ├── js/
│   └── assets/
│
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── application.properties
│
├── screenshots/
│   └── (screenshots go here)
│
├── README.md
└── .gitignore

Setup Instructions (Local Development)
Prerequisites

Java 17+

Maven

MySQL

Git

Backend Setup

Clone the repository:

git clone https://github.com/MdFoyez-0/RentHub-.git
cd RentHub/backend


Create database:

CREATE DATABASE renthub;


Configure application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/renthub
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true


Run backend:

mvn clean install
mvn spring-boot:run


Backend runs at:

http://localhost:8080

Frontend Setup

Navigate to frontend folder:

cd ../frontend


Open index.html in browser
(or serve via a local static server)

API Integration

Update API URLs in frontend JS files when switching environments:

// Local
fetch("http://localhost:8080/api/...")

// Production
fetch("https://<backend-url>/api/...")

Screenshots
RentHub Dashboard

<img width="1426" height="948" alt="image" src="https://github.com/user-attachments/assets/9f5e4df3-23ec-4be1-be2a-15bcf4f9a172" />


Login Page
<img width="513" height="818" alt="image" src="https://github.com/user-attachments/assets/fbd2fca2-b043-45c1-9540-fb0f0d7d3186" />

Admin-panel
<img width="1394" height="938" alt="image" src="https://github.com/user-attachments/assets/1cdaaa84-1310-4b82-a7e5-ce1253c1bf53" />
<img width="1466" height="403" alt="image" src="https://github.com/user-attachments/assets/604b5162-8a8e-4dd5-a49a-b3858ff02128" />

LandLord-Panel
<img width="1402" height="938" alt="image" src="https://github.com/user-attachments/assets/07dde179-38f6-4c62-8989-1dd30c8d3b3b" />



Future Improvements

JWT authentication

Input validation and error handling

Pagination and search

Backend unit & integration tests

UI/UX improvements

Payment system integration

Author

Md. Foyez
