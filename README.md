# Badistration - Badminton Tournament Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17.0-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7.2-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)
[![Production](https://img.shields.io/badge/Status-Production-success.svg)](https://badistration.com)

> **Live Production:** [badistration.com](https://badistration.com)

A comprehensive full-stack web application for managing badminton tournament registrations. 
The platform enables players to register for tournaments, 
manage partnerships for doubles events, and allows tournament organizers to efficiently manage registrations 
and export data.

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Testing](#testing)
- [Project Structure](#project-structure)
---

## Overview

**Badistration** is a production-ready tournament management system specifically designed for badminton events. It streamlines the entire registration workflow, from player sign-up to data export for tournament organizers.

### Problem Statement

Traditional badminton tournament registration involves manual processes with spreadsheets, emails, and phone calls, leading to errors, confusion, and administrative overhead.

### Solution

This application provides:
- **Automated Registration:** Players can self-register and select categories
- **Partnership Management:** Intelligent system for managing doubles partnerships with conflict detection
- **Real-time Tracking:** Tournament organizers see registrations in real-time
- **Data Export:** One-click export to Excel for tournament brackets
- **Role-Based Access:** Separate interfaces for players, moderators, and administrators

---

## Key Features

### For Players
- **User Registration & Authentication** - Secure JWT-based authentication with email verification
- **Tournament Registration** - Browse and register for upcoming tournaments
- **Multi-Category Support** - Register for multiple categories (up to tournament limit)
- **Partnership System** - Create, accept, or decline partnership invitations for doubles events
- **Registration Management** - Add/remove categories before registration deadline
- **Profile Management** - Update personal information, team affiliation, and contact details
- **Tournament History** - View past and upcoming tournament participation

### For Tournament Organizers (Moderators)
- **Tournament Creation** - Configure tournaments with dates, categories, pricing, and limits
- **Registration Oversight** - Approve/reject registrations and track payment status
- **Category Pricing** - Set individual pricing for different categories
- **Partnership Monitoring** - View all partnerships within tournaments
- **Data Export** - Export registration data to Excel (.xlsx) format
- **Export History** - Track all data exports with timestamps

### For Administrators
- **User Management** - Manage all users and assign roles (USER, MODERATOR, ADMIN)
- **Platform Analytics** - Dashboard with statistics (users, tournaments, registrations)
- **System Oversight** - Full access to all tournaments and registrations
- **Role Assignment** - Promote users to moderators or admins

### Advanced Features
- **70+ Game Categories** - Comprehensive category support (age-based, skill-based, event types)
- **Smart Partnership Logic** - Prevents duplicate category partnerships and detects conflicts
- **Email Notifications** - Automated emails for password reset and confirmations
- **Scheduled Tasks** - Auto-close expired tournaments and clean up tokens
- **Redis Caching** - 10-minute TTL cache for improved performance
- **API Documentation** - Interactive Swagger UI in development mode

---

## Technology Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Core programming language |
| **Spring Boot** | 3.4.5 | Application framework |
| **Spring Data JPA** | - | ORM and database abstraction |
| **Spring Security** | - | Authentication and authorization |
| **PostgreSQL** | 17.0 | Primary relational database |
| **Redis** | 7.2 | Caching and session management |
| **Flyway** | 11.13.2 | Database migration management |
| **JJWT** | 0.11.5 | JWT token generation and validation |
| **Apache POI** | 5.4.1 | Excel file generation (.xlsx) |
| **Resend API** | - | Transactional email service |
| **SpringDoc OpenAPI** | 2.2.0 | API documentation (Swagger) |
| **Maven** | 3.9.9 | Build and dependency management |

### Infrastructure & DevOps
- **Docker** - Containerization with multi-stage builds
- **Docker Compose** - Orchestration for dev and production environments
- **RedisInsights** - Redis monitoring (development)

### Development Tools
- **Spring Boot DevTools** - Hot reload during development
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for tests

---

## System Architecture

### High-Level Architecture

```
┌─────────────────┐
│   Frontend      │ (Not included in this repo)
│   React/Vue/etc │
└────────┬────────┘
         │ HTTPS/REST
         ▼
┌─────────────────────────────────────────┐
│         Spring Boot Application         │
│  ┌─────────────────────────────────┐   │
│  │   Controllers (REST API)        │   │
│  │   - Auth, User, Tournament      │   │
│  │   - Partnership, Admin          │   │
│  └───────────┬─────────────────────┘   │
│              │                          │
│  ┌───────────▼─────────────────────┐   │
│  │   Services (Business Logic)     │   │
│  │   - JWT, Email, Export          │   │
│  │   - Tournament, Partnership     │   │
│  └───────────┬─────────────────────┘   │
│              │                          │
│  ┌───────────▼─────────────────────┐   │
│  │   Repositories (Data Access)    │   │
│  │   - Spring Data JPA             │   │
│  └───────────┬─────────────────────┘   │
└──────────────┼──────────────────────────┘
               │
       ┌───────┴────────┐
       │                │
   ┌───▼────┐     ┌─────▼─────┐
   │ Redis  │     │PostgreSQL │
   │ Cache  │     │ Database  │
   └────────┘     └───────────┘
```


---


## API Documentation

### Interactive Documentation

When running in **development mode**, access the interactive Swagger UI:

**URL:** http://localhost:8080/swagger-ui.html

### Endpoint Categories

#### 1. Authentication (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/login` | User login (returns JWT cookie) | No |
| POST | `/logout` | Clear authentication cookie | No |
| POST | `/register` | Create new user account | No |
| GET | `/me` | Get current user information | Yes |

#### 2. Password Reset (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/request-password-reset` | Initiate password reset | No |
| POST | `/verify-reset-code` | Verify reset token | No |
| POST | `/complete-password-reset` | Set new password | No |

#### 3. Tournaments (`/api/tournaments`)
| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| GET | `/` | Get all tournaments | No | - |
| GET | `/filter` | Filter tournaments by criteria | No | - |
| GET | `/upcoming?limit=5` | Get upcoming tournaments | No | - |
| GET | `/{id}` | Get tournament details | No | - |
| POST | `/` | Create tournament | Yes | MODERATOR/ADMIN |
| PUT | `/{id}` | Update tournament | Yes | MODERATOR/ADMIN |
| DELETE | `/{id}` | Delete tournament | Yes | MODERATOR/ADMIN |

#### 4. Registration (`/api/user-tournaments`)
| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| POST | `/register` | Register for tournament | Yes | USER |
| POST | `/add-categories` | Add more categories | Yes | USER |
| POST | `/remove-category` | Remove category (user) | Yes | USER |
| GET | `/my-registrations` | Get user's registrations | Yes | USER |
| DELETE | `/{id}/cancel` | Cancel registration | Yes | USER |
| GET | `/tournament/{id}` | Get tournament registrations | Yes | MODERATOR/ADMIN |
| PATCH | `/{id}/status` | Update registration status | Yes | MODERATOR/ADMIN |
| PATCH | `/{id}/payment` | Confirm payment | Yes | MODERATOR/ADMIN |

#### 5. Partnerships (`/api/partnerships`)
| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| POST | `/create` | Create partnership invitation | Yes | USER |
| POST | `/accept` | Accept partnership | Yes | USER |
| POST | `/decline` | Decline partnership | Yes | USER |
| GET | `/my-partnerships` | Get user's partnerships | Yes | USER |
| GET | `/tournament/{id}` | Get tournament partnerships | Yes | USER |

#### 6. Data Export (`/api/exportData`)
| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| POST | `/full/{tournamentId}` | Export to Excel | Yes | MODERATOR/ADMIN |

#### 7. Users (`/api/users`)
| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| GET | `/profile` | Get own profile | Yes | USER |
| PUT | `/profile` | Update own profile | Yes | USER |
| POST | `/change-password` | Change password | Yes | USER |
| GET | `/{id}` | Get user details | Yes | - |
| GET | `/{id}/tournaments` | Get user's tournament history | Yes | - |
| DELETE | `/{id}` | Delete user | Yes | ADMIN |
| POST | `/{id}/assign-role` | Assign role to user | Yes | ADMIN |

#### 8. Admin (`/api/admin`)
| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| GET | `/dashboard` | Get platform statistics | Yes | ADMIN |

#### 9. Public (`/api/public`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/stats` | Get public statistics | No |


---

## Security

### Authentication & Authorization

- **JWT-based Authentication** - Stateless token-based auth with 24-hour expiration
- **HTTP-Only Secure Cookies** - JWT stored in cookies (SameSite=None in prod)
- **BCrypt Password Hashing** - Secure password storage with strength 10
- **Role-Based Access Control (RBAC)** - Three roles: USER, MODERATOR, ADMIN


### Protected Endpoints

| Role | Access |
|------|--------|
| **USER** | Personal profile, tournament registration, partnerships |
| **MODERATOR** | Own tournaments, registrations, data export |
| **ADMIN** | All endpoints, user management, platform analytics |

### Password Reset Flow

1. User requests reset → Email with 6-digit code sent
2. User submits code → Token validated (15-min expiration)
3. User sets new password → Token consumed and deleted
4. **Scheduled cleanup** removes expired tokens daily

---

## Testing

### Test Coverage

Located in `src/test/java/com/helinok/pzbad_registration/`:

- **PartnershipControllerTest** - Partnership creation, acceptance, decline
- **UserControllerTest** - User management operations
- **ResetPasswordControllerTest** - Password reset flow
- **TournamentControllerTest** - Tournament CRUD operations

### Testing Framework

- **JUnit 5 (Jupiter)** - Test execution
- **Mockito** - Mocking dependencies

---

## Project Structure

```
PZBad_Registration/
├── src/
│   ├── main/
│   │   ├── java/com/helinok/pzbad_registration/
│   │   │   ├── Config/                  # Security, JWT, Redis, OpenAPI
│   │   │   ├── Controllers/             # REST API endpoints
│   │   │   │   ├── Auth/               # Authentication
│   │   │   │   ├── Public/             # Public APIs
│   │   │   │   ├── User/               # User operations
│   │   │   │   ├── Moderator/          # Tournament management
│   │   │   │   └── Admin/              # Admin operations
│   │   │   ├── Models/                 # JPA entities (9 models)
│   │   │   ├── Services/               # Business logic (17 services)
│   │   │   ├── Repositories/           # Data access (10 repositories)
│   │   │   ├── Dtos/                   # Data transfer objects (29 DTOs)
│   │   │   ├── Enums/                  # Enumerations (categories, statuses)
│   │   │   ├── Requests/               # API request models
│   │   │   ├── Responses/              # Standardized API responses
│   │   │   ├── Exceptions/             # Custom exceptions
│   │   │   ├── Facades/                # Complex operation orchestration
│   │   │   ├── Utils/                  # Scheduled tasks & utilities
│   │   │   └── PzBadRegistrationApplication.java
│   │   └── resources/
│   │       ├── application.yaml        # Main configuration
│   │       ├── application-dev.yaml    # Development profile
│   │       ├── application-prod.yaml   # Production profile
│   │       └── db/migration/           # Flyway migrations
│   └── test/
│       └── java/com/helinok/pzbad_registration/
│           └── Controllers/            # Unit tests
├── Dockerfile                          # Multi-stage build
├── compose.yaml                        # Docker Compose configuration
├── pom.xml                             # Maven dependencies
├── mvnw                                # Maven wrapper
└── README.md                           # This file
```

**Key Statistics:**
- **101** Java source files
- **29** DTO classes
- **17** Service implementations
- **10** Repository interfaces
- **9** REST Controllers
- **9** JPA Entity models
- **70+** Game categories
- **450+** lines of SQL (migrations)

---

<div align="center">

**Built with ❤️ for the Badminton Community**

</div>
