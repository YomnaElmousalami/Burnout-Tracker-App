# App for Learning

Just for learning purposes (only works with my Google Calendar):

Deployed on Railway: https://burnout-tracker-app-production.up.railway.app/

AI Burnout Detector: a web app where you log in with your Google account, it reads your last 2 weeks of calendar events, and an AI analyzes the patterns (back-to-back meetings, late nights, no breaks, weekend work) to give you a burnout risk score and personalized recommendations.


| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Framework | Spring Boot | 3.3.5 |
| Auth / Google Login | Spring Security OAuth2 Client | included in Spring Boot 3.3.5 |
| Calendar Data | Google Calendar API v3 | free |
| AI Framework | Spring AI | 1.0.0 |
| AI Model | Groq + llama3-8b | free tier |
| Database | H2 (dev) → PostgreSQL (prod) | — |
| Frontend | Thymeleaf | included in Spring Boot 3.3.5 |
| Build Tool | Maven | 3.x |
| Cloud Hosting | Railway | free tier |
| Cloud Database | Railway PostgreSQL | free tier |
