# 🚀 Scalable URL Shortener

A production-style URL shortening service built using Spring Boot, Redis, PostgreSQL, and Docker.

---

## 📌 Features

- Shortens long URLs using Base62 encoding
- Redirects short URLs efficiently
- Redis caching for faster lookups
- Rate limiting to prevent abuse
- Click analytics tracking
- Dockerized setup for easy deployment

---

## 🏗️ Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- Redis
- Docker & Docker Compose
- Maven

---

## 🏛️ Architecture

User → Spring Boot API → Redis Cache → PostgreSQL  
Analytics → Stored in Database  

- Redis improves read latency  
- PostgreSQL ensures durability  
- Docker ensures containerized deployment  

---

## ▶️ Running Locally

```bash
docker-compose up --build
```

App runs at:

```
http://localhost:8080
```

---

## 📊 Sample API

### Shorten URL

POST `/api/shorten`

```json
{
  "originalUrl": "https://example.com"
}
```

### Redirect

GET `/{shortCode}`

---

## 🚀 Future Improvements

- Custom aliases
- Expiration for short URLs
- Load balancing
- Horizontal scaling
- AWS deployment

---

## 👨‍💻 Author

Animesh Vaish