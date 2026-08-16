# Climate Platform - Production Deployment Guide

## Overview

This document provides comprehensive instructions for deploying the Climate Platform in a production environment using Docker and Docker Compose.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- At least 4GB RAM available for containers
- SSL certificates (Let's Encrypt recommended)
- Domain name configured (e.g., climate.tn and api.climate.tn)

## Deployment Steps

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/climate-platform.git
cd climate-platform
```

### 2. Configure Environment Variables

Create a `.env` file from the example:

```bash
cp .env.example .env
```

Edit the `.env` file with your production values:

```bash
# Database
DATABASE_USERNAME=secure_password_here
DATABASE_PASSWORD=secure_password_here

# JWT
JWT_SECRET=generate_a_secure_random_secret_at_least_256_bits

# AI Services
OPENAI_API_KEY=your_openai_api_key_here
ANTHROPIC_API_KEY=your_anthropic_api_key_here

# Grafana
GRAFANA_ADMIN_PASSWORD=secure_grafana_password
```

### 3. Configure SSL Certificates

Place your SSL certificates in `nginx/certs/`:

```bash
mkdir -p nginx/certs
# Copy your fullchain.pem and privkey.pem files
cp /path/to/fullchain.pem nginx/certs/
cp /path/to/privkey.pem nginx/certs/
```

### 4. Build and Deploy

```bash
# Build and start all services
docker-compose up -d --build

# Check service status
docker-compose ps

# Check logs
docker-compose logs -f
```

### 5. Verify Deployment

Check that all services are healthy:

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend
curl http://localhost:80/

# Prometheus
curl http://localhost:9090

# Grafana
curl http://localhost:3000
```

## Service Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Internet                                │
└───────────────────────┬───────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                    Nginx Reverse Proxy                       │
│                    Port 80, 443                             │
└───────────────────────┬───────────────────────────────────────┘
                        │
        ┌───────────────┴───────────────┐
        ▼                               ▼
┌─────────────────┐           ┌─────────────────┐
│   Frontend      │           │     Backend     │
│   (Port 80)     │           │   (Port 8080)    │
│   React App     │           │  Spring Boot    │
└─────────────────┘           └─────────────────┘
                                    │
                        ┌───────────┼───────────┐
                        ▼           ▼           ▼
                 ┌─────────┐ ┌──────────┐ ┌──────────┐
                 │PostgreSQL│ │  Redis   │ │  Prometheus│
                 │ Port 5432│ │ Port 6379│ │ Port 9090│
                 └─────────┘ └──────────┘ └──────────┘
                                    │
                                    ▼
                            ┌──────────────┐
                            │   Grafana    │
                            │ Port 3000    │
                            └──────────────┘
```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/climate_platform` | Database connection URL |
| `DATABASE_USERNAME` | `postgres` | Database username |
| `DATABASE_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | *(required)* | Secret key for JWT token generation |
| `JWT_EXPIRATION_MS` | `86400000` | JWT token expiration in milliseconds |
| `OPENAI_API_KEY` | *(optional)* | OpenAI API key for AI features |
| `ANTHROPIC_API_KEY` | *(optional)* | Anthropic API key for AI features |
| `AI_PROVIDER` | `openai` | AI provider to use (openai or anthropic) |
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring profile to activate |

### Database Configuration

The application uses PostgreSQL 15 with the following default settings:

- **Database Name**: `climate_platform`
- **Username**: `postgres` (change in production)
- **Connection Pool**: HikariCP with 20 max connections
- **DDL Mode**: `validate` (use `update` for development)

### Security Configuration

- **JWT**: HS256 algorithm with configurable secret
- **Password Encoding**: BCrypt with 12 rounds
- **CORS**: Configured for production domains
- **Security Headers**: HSTS, CSP, X-Frame-Options

## Monitoring

### Health Checks

The backend exposes health check endpoints:

```
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

### Prometheus Metrics

Metrics are available at:

```
GET /actuator/prometheus
```

### Grafana Dashboards

Access Grafana at `http://localhost:3000` with:
- Username: `admin`
- Password: Value from `GRAFANA_ADMIN_PASSWORD` environment variable

## Backup and Recovery

### Database Backup

```bash
# Create backup
docker exec climate-platform-db pg_dump -U postgres climate_platform > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore backup
docker exec -i climate-platform-db psql -U postgres climate_platform < backup.sql
```

### Automated Backups

Add to crontab:

```bash
0 2 * * * docker exec climate-platform-db pg_dump -U postgres climate_platform > /backups/backup_$(date +%Y%m%d).sql
```

## Scaling

### Horizontal Scaling

```bash
# Scale backend instances
docker-compose up -d --scale backend=3

# Scale with load balancer
docker-compose up -d --scale backend=3 --scale frontend=2
```

### Resource Limits

Update `docker-compose.yml` to add resource limits:

```yaml
backend:
  # ... existing config ...
  deploy:
    resources:
      limits:
        cpus: '1'
        memory: 1G
      reservations:
        cpus: '0.5'
        memory: 512M
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`
   - Ensure PostgreSQL container is running: `docker-compose ps`

2. **JWT Token Invalid**
   - Verify `JWT_SECRET` is at least 256 bits
   - Ensure consistent secret across all instances

3. **AI API Errors**
   - Verify API keys are set correctly
   - Check rate limits for API providers

4. **SSL Certificate Issues**
   - Ensure certificates are valid and not expired
   - Check file permissions in `nginx/certs/`

### Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### Health Checks

```bash
# Check container health
docker inspect --format='{{json .State.Health}}' climate-platform-backend

# Manual health check
curl http://localhost:8080/actuator/health
```

## Maintenance

### Updates

```bash
# Pull latest images
docker-compose pull

# Rebuild and restart
docker-compose up -d --build

# Clean up unused images
docker image prune -a
```

### Zero-Downtime Deployment

```bash
# Build new images
docker-compose build

# Update services one by one
docker-compose up -d --no-deps --scale backend=1 backend
docker-compose up -d --no-deps --scale frontend=1 frontend

# Scale back up
docker-compose up -d --no-deps --scale backend=3 backend
docker-compose up -d --no-deps --scale frontend=2 frontend
```

## Security Best Practices

1. **Use strong secrets**: Generate cryptographically secure secrets for JWT and database passwords
2. **Enable HTTPS**: Always use HTTPS in production with valid SSL certificates
3. **Regular updates**: Keep Docker images and dependencies updated
4. **Network isolation**: Use Docker networks to isolate services
5. **Secrets management**: Use Docker secrets or external vault for sensitive data
6. **Firewall rules**: Restrict access to only necessary ports

## Support

For deployment issues, check:
1. Application logs: `docker-compose logs -f`
2. Container health: `docker-compose ps`
3. Resource usage: `docker stats`
4. Network connectivity: `docker network ls`
