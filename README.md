# AWS-S3-Implementation

A full-stack customer management application built with Spring Boot and React, extended with AWS S3 profile image storage and deployed to AWS using Docker and CI/CD.

The application allows users to register and log in, manage customer accounts, edit customer information, delete customers, and upload/view profile pictures.

This project was created as a learning and portfolio project to gain practical experience with Spring Boot, AWS cloud services, Docker, CI/CD, REST APIs, and cloud deployment.

---

## Live Application

- **Frontend:** [https://main.d17l22xsh2zrjs.amplifyapp.com/](https://main.d17l22xsh2zrjs.amplifyapp.com/)
- **Backend:** [http://danielradu-api-env.eba-ynwmmxpe.eu-central-1.elasticbeanstalk.com/](http://danielradu-api-env.eba-ynwmmxpe.eu-central-1.elasticbeanstalk.com/)

---

## Features

### Authentication
- User registration & login
- Email and password authentication
- JWT-based authentication
- Spring Security integration

### Customer Management
- Create customer accounts
- View customer details
- Edit customer information
- Delete customer accounts
- Manage customer name, email, and age

### Profile Images
- Upload profile pictures
- Store profile pictures in Amazon S3
- Retrieve and display profile pictures
- Associate profile images with individual customer records

---

## What I Implemented

The original application was based on the [Amigoscode Spring Boot Full Stack project](https://amigoscode.com/).

My main contribution was implementing the profile image functionality using Amazon S3 and integrating it into the existing application.

This included:
- Adding profile image support to the customer model
- Adding a `profile_image_id` column to the database
- Implementing profile image upload functionality
- Implementing profile image download functionality
- Connecting the Spring Boot application to Amazon S3
- Configuring AWS IAM permissions
- Configuring the application for deployment on AWS
- Configuring Nginx on Elastic Beanstalk to support larger image uploads
- Writing integration tests for profile image upload and download
- Deploying the application to AWS
- Creating the CI/CD pipeline for automated backend deployments

---

## Architecture

The deployed application uses the following architecture:

```text
                         ┌─────────────────────┐
                         │       React         │
                         │      Frontend       │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    AWS Amplify      │
                         │   Frontend Hosting  │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    CloudFront       │
                         │   HTTPS / CDN       │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ Elastic Beanstalk   │
                         │    Spring Boot      │
                         │      Backend        │
                         └──────┬───────┬──────┘
                                │       │
                    ┌───────────┘       └────────────┐
                    ▼                                ▼
          ┌─────────────────┐               ┌─────────────────┐
          │   Amazon RDS    │               │    Amazon S3    │
          │   PostgreSQL    │               │ Profile Images  │
          └─────────────────┘               └─────────────────┘
```

### Request Flow

**For normal API requests:**
```text
React ──> CloudFront ──> Elastic Beanstalk ──> Spring Boot REST API ──> PostgreSQL (RDS)
```

**For profile image uploads:**
```text
React ──> CloudFront ──> Elastic Beanstalk ──> Spring Boot ──> Amazon S3
```

The application uses AWS IAM to control access between the deployed Spring Boot application and the S3 bucket.

---

## Technology Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Maven
- REST API

### Frontend
- React
- Vite
- JavaScript

### AWS
- **Amazon S3** — Profile image storage
- **Amazon RDS** — PostgreSQL database
- **AWS Elastic Beanstalk** — Backend hosting
- **Amazon CloudFront** — HTTPS and CDN
- **AWS Amplify** — Frontend hosting
- **AWS IAM** — Permissions and access control

### DevOps
- Docker
- Docker Hub
- GitHub Actions
- Slack
- Jib

---

## Database

The deployed application uses PostgreSQL hosted on Amazon RDS.

The customer data includes information such as:
- Customer ID
- Name
- Email
- Age
- Profile image ID

A `profile_image_id` column was added to associate a customer with their profile image stored in Amazon S3. Database migrations are managed using Flyway.

---

## Amazon S3 Profile Image Implementation

Profile images are stored separately from the PostgreSQL database using Amazon S3.

Instead of storing the actual image data inside PostgreSQL, the application stores the image in S3 and keeps the corresponding image identifier in the customer database record.

### S3 Object Structure
```text
fs-danielradu-customer-test/
└── profile-images/
    └── {customer-id}/
        └── {profile-image-id}
```

### Backend Responsibilities
1. Receiving the uploaded image
2. Generating an image identifier
3. Uploading the image to S3
4. Associating the image with the customer record
5. Retrieving the image from S3 when requested

AWS IAM is used to provide the Elastic Beanstalk application with the required S3 permissions.

---

## Testing

Integration tests were implemented for the profile image functionality.

The tests cover:
- Uploading a customer profile image
- Downloading the uploaded profile image
- Verifying that the image can be successfully retrieved through the REST API

These tests helped verify that the complete profile image flow worked correctly between the API and storage layer.

---

## CI/CD Pipeline

The backend uses GitHub Actions for continuous integration and deployment. The deployment workflow is triggered when changes are pushed to the main branch affecting the backend.

```text
Git Push
  ↓
GitHub Actions
  ↓
Start PostgreSQL Test Container
  ↓
Run Maven Tests
  ↓
Build Spring Boot Application
  ↓
Build Docker Image
  ↓
Push Image to Docker Hub
  ↓
Create Elastic Beanstalk Deployment Package
  ↓
Deploy to AWS Elastic Beanstalk
  ↓
Commit Updated Docker Image Tag
  ↓
Send Slack Notifications
```

### Pipeline Key Features
- **Tools:** GitHub Actions, Maven, Docker/Jib, Docker Hub, AWS Elastic Beanstalk, Slack
- **Slack Notifications:** Sent throughout the deployment process to provide status updates.
- **Versioning:** Docker images are tagged using a timestamp-based build number, ensuring unique versions per deployment.

---

## Docker

The backend is containerized using Docker. **Jib** is used to build and push the Docker image directly from Maven without requiring a local Docker daemon during the Maven build.

The resulting image is pushed to Docker Hub and pulled by Elastic Beanstalk during deployment.

---

## Project Structure

```text
AWS-S3-Implementation/
│
├── .github/
│   └── workflows/
│       └── backend-deployment.yml
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── pom.xml
│   └── ...
│
├── frontend/
│   └── react/
│       ├── src/
│       ├── public/
│       ├── package.json
│       └── ...
│
├── .platform/
│   └── nginx/
│       └── conf.d/
│
├── Dockerrun.aws.json
├── docker-compose.yml
└── README.md
```

---

## Running Locally

### Prerequisites
Make sure you have the following installed:
- Java 17
- Maven
- Node.js
- Docker

### 1. Start PostgreSQL
Start a local PostgreSQL instance using Docker:
```bash
docker compose up -d
```

### 2. Start the Backend
Navigate to the backend directory and run the Spring Boot application:
```bash
cd backend
mvn spring-boot:run
```
The backend will start at `http://localhost:8080`.

### 3. Start the Frontend
Navigate to the React application, install dependencies, and start the development server:
```bash
cd frontend/react
npm install
npm run dev
```

---

## Environment Variables

The application uses environment variables for configuration. Set these locally or in your deployment environment:

```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/customer
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# AWS Configuration
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_REGION=eu-central-1
AWS_S3_BUCKET_NAME=your_bucket_name

# Frontend Configuration
VITE_API_BASE_URL=http://localhost:8080
```

*Note: Never commit sensitive credentials to source control.*

---

## AWS Services Summary

| Service | Purpose |
| :--- | :--- |
| **AWS Amplify** | Hosts the React frontend |
| **Amazon CloudFront** | Provides HTTPS and routes requests |
| **AWS Elastic Beanstalk** | Hosts the Spring Boot backend |
| **Amazon RDS** | Hosts the PostgreSQL database |
| **Amazon S3** | Stores customer profile images |
| **AWS IAM** | Controls AWS resource permissions |

---

## What I Learned

Through building and deploying this project, I gained practical experience with:

- Building REST APIs with Spring Boot & Spring Data JPA
- Securing applications with Spring Security & JWT authentication
- Integrating Amazon S3 for cloud object storage
- Managing AWS IAM roles and permissions
- Provisioning and configuring Amazon RDS & Elastic Beanstalk
- Routing traffic and enabling HTTPS with Amazon CloudFront
- Hosting modern web apps with AWS Amplify
- Containerizing applications using Docker & Jib
- Automating testing and deployment with GitHub Actions & Slack notifications
- Designing and implementing full-stack cloud application architecture

---

## Acknowledgements

- The initial full-stack application foundation was based on the [Amigoscode](https://amigoscode.com/) Spring Boot Full Stack course.
- Extended with custom Amazon S3 profile image functionality, AWS cloud infrastructure deployment, CI/CD pipelines, integration testing, and production optimization.
