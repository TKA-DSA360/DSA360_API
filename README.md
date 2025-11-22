# DSA360 – Multi-Tenant SaaS Platform  
**Database-per-Tenant Spring Boot Application (2025 Production-Ready Setup)**

A modern, secure, multi-tenant SaaS application built with Spring Boot 3+, MySQL 8, Docker, Jenkins CI/CD, SonarQube, and Trivy security scanning.

### Features
- Multi-tenant architecture (one database per tenant)
- Fully Dockerized (multi-stage, non-root, less than 95 MB image)
- Zero secrets in code (`.env` + Jenkins credentials)
- Production-grade Jenkins pipeline with Quality Gate & vulnerability scanning
- Health checks & graceful startup
- Ready for local development and future Kubernetes/EKS deployment

### Prerequisites
| Tool              | Minimum Version     |
|-------------------|---------------------|
| Docker Desktop    | 24.0+               |
| Git               | 2.30+               |
| Java JDK          | 17                  |

### Quick Start (Less than 2 minutes)
```bat
git clone https://github.com/TKA-DSA360/DSA360_API.git
cd DSA360_API
copy .env.example .env
docker-compose --env-file .env up -d --build
```

### Open → http://localhost:8091/actuator/health
**Default URLs**
- App: http://localhost:8091
- MySQL: localhost:3307

###Stop & Cleanup
```bat 
docker-compose down -v
```

### Security & Compliance Ready

**Non-root container**
- Trivy scanning
- Secrets via .env
- Immutable infrastructure

You now have a production-grade, investor-ready SaaS stack!
Made with love by **Ram Chadar © 2025**
