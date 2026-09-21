# CampusFind AI — Smart Campus Lost & Found Management System

![CampusFind AI Architecture](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.4-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)

> An enterprise-grade, privacy-first Smart Campus Lost & Found ecosystem powered by **Multimodal Computer Vision**, **Explainable Multi-Signal Semantic Matching**, and **Cryptographic QR + OTP Handover Verification**.

---

## 🌟 Highlights & Key Innovations

1. **Multimodal AI Vision Feature Extraction**:
   - Analyzes uploaded photos to extract item category, dominant color tones, brand emblems, and materials.
   - Distinct separation between **Observed Visual Evidence** (100% confidence) and **Inferred Attributes** (probabilistic context).
   - Generates anti-fraud **Blind Verification Questions** (e.g., hidden stickers, internal lining, serial numbers).

2. **Explainable Multi-Signal Semantic Matching Engine**:
   - Calculates weighted confidence scores across six core signals:
     - **Category Match**: 25%
     - **Color Compatibility**: 15%
     - **Brand Alignment**: 15%
     - **Campus Location & Proximity**: 15%
     - **Loss vs. Found Time Delta**: 10%
     - **Semantic NLP Token Overlap**: 20%
   - Provides clear, human-readable justification checklists (`✓ Same Category`, `✓ Matching Brand: Nike`, `✓ Found within 24h`) rather than black-box scores.
   - Automatically issues notifications when match confidence exceeds **70%**.

3. **Zero-Knowledge Anti-Fraud & Privacy Masking**:
   - Public feeds and search results strictly mask private identifying traits (e.g., wallet contents, student ID numbers `STU-****-8841`).
   - Claimants must answer blind verification questions to prove authentic ownership before any approval or appointment booking.

4. **Contactless Pickup Pass & Digital Handover Receipt**:
   - Generates dynamic ZXing QR codes + synchronized 6-digit OTP (`482731`).
   - Staff collection desk performs instant verification.
   - Generates immutable digital receipts stamped with a **SHA-256 cryptographic signature** for campus audit compliance.

5. **Campus Operations & Hotspot Analytics**:
   - Real-time campus heatmap highlighting high-frequency loss zones (Central Library, Cafeteria, Tech Quad, Sports Complex).
   - Staff review moderation queues and an immutable audit log capturing every report, match, claim, and handover.

---

## 📐 System Architecture

```mermaid
flowchart TD
    subgraph Client["Frontend (React 19 + TypeScript + Tailwind)"]
        UI_Home["Hero & Discovery Feed"]
        UI_Report["AI Photo & Natural Language Intake"]
        UI_Match["Match Center & Dial Checklist"]
        UI_Claim["Blind Verification & Claim Portal"]
        UI_Pickup["QR Pass & OTP Handover Desk"]
        UI_Admin["Analytics, Hotspots & Moderation"]
    end

    subgraph Backend["Spring Boot 3.3.4 (Java 21)"]
        Sec["Spring Security + JJWT (Stateless)"]
        Ctrl["REST API Controllers"]
        
        subgraph Engine["AI & Business Logic Services"]
            VisionSvc["Multimodal Vision Analyzer"]
            MatchSvc["Multi-Signal Weighted Matcher"]
            NLPSvc["Conversational Description Parser"]
            ClaimSvc["Anti-Fraud Consistency Evaluator"]
            PickupSvc["ZXing QR & SHA-256 Receipt Generator"]
            AuditSvc["Campus Security Audit Service"]
        end
    end

    subgraph Storage["Data Tier"]
        PG[(PostgreSQL 16\nRelational DB)]
        Disk[(Local Sanitzed Storage\nEXIF-Stripped Uploads)]
    end

    Client <-->|REST / JSON + JWT| Sec
    Sec --> Ctrl
    Ctrl --> Engine
    Engine --> PG
    VisionSvc --> Disk
```

---

## 🔄 End-to-End Handover Flow

```mermaid
sequenceDiagram
    autonumber
    actor Student as Student (Finder / Owner)
    participant UI as CampusFind Web App
    participant AI as Spring Boot AI Engine
    participant Staff as Campus Staff Desk
    participant DB as PostgreSQL 16

    Note over Student, UI: 1. Reporting a Found Item
    Student->>UI: Uploads item photo
    UI->>AI: POST /api/ai/analyze-image
    AI-->>UI: Returns detected category, color, brand, observed vs inferred traits
    Student->>UI: Confirms location (Central Library) & submits
    UI->>DB: Persists FoundReport with reference LF-2026-XXXXXX

    Note over AI, DB: 2. Semantic Matching
    AI->>DB: Queries active LostReports
    AI->>AI: Calculates 6-signal weighted score & explanation checklist
    AI->>DB: Stores MatchCandidate (Score >= 70%) and triggers notification

    Note over Student, Staff: 3. Claiming & Verification
    Student->>UI: Answers blind verification question
    UI->>AI: POST /api/claims/submit
    AI->>DB: Flags consistency rating (HIGH)
    Staff->>UI: Reviews & approves claim
    UI-->>Student: Issues QR Code & 6-digit OTP

    Note over Student, Staff: 4. Physical Collection & Handover
    Student->>Staff: Presents OTP / QR at Student Center Room 102
    Staff->>UI: Submits OTP verification
    UI->>AI: POST /api/pickup/verify
    AI->>DB: Marks item COLLECTED & generates SHA-256 Digital Receipt
    UI-->>Student: Displays printable verifiable receipt
```

---

## 🗂️ Project Structure

```
smart-campus-lost-found/
├── backend/                                # Spring Boot 3.3.4 Application
│   ├── pom.xml                             # Dependencies: JPA, Security, JJWT, ZXing, Postgres
│   └── src/
│       ├── main/java/com/campusfind/
│       │   ├── config/                     # Storage and Cors configuration
│       │   ├── controller/                 # 12 REST Controllers
│       │   ├── dto/                        # Clean typed request/response transfer objects
│       │   ├── exception/                  # GlobalExceptionHandler & AppException
│       │   ├── model/                      # JPA Entities & Enums (FoundReport, Claim, etc.)
│       │   ├── repository/                 # Spring Data JPA Repositories (with Specifications)
│       │   ├── security/                   # JwtTokenProvider, Filter, CustomUserDetailsService
│       │   └── service/                    # Business services, AI Vision & Matching engines
│       └── test/java/com/campusfind/       # Unit & Integration test suite (100% pass)
│
└── frontend/                               # React 19 + TypeScript + Vite + Tailwind CSS
    ├── index.html                          # Semantic HTML5 entry with modern typography
    ├── package.json                        # Dependencies (Lucide icons, Canvas Confetti)
    ├── vite.config.ts                      # Dev server proxying /api & /uploads to :8080
    └── src/
        ├── components/                     # Navbar, HeroSection, Modals, DemoBanner
        ├── views/                          # 7 Primary Page Views
        │   ├── ReportFoundView.tsx         # AI photo scan, observed vs inferred, presets
        │   ├── ReportLostView.tsx          # Conversational AI assistant
        │   ├── BrowseItemsView.tsx         # Privacy-masked directory, filters, search
        │   ├── MatchCenterView.tsx         # Side-by-side comparison, match dials, breakdown
        │   ├── PickupDeskView.tsx          # Student QR pass & Staff Handover terminal
        │   ├── MyActivityView.tsx          # Student portal for submissions & claims
        │   └── AdminDashboardView.tsx      # KPIs, Campus Heatmap, Moderation & Audit
        ├── services/api.ts                 # Typed Axios REST API client
        └── types/index.ts                  # Comprehensive TypeScript models
```

---

## ⚡ Quick Start Guide

### Prerequisites
- **Java 21 JDK** installed (`java -version`)
- **Maven 3.8+** installed (`mvn -version`)
- **Node.js 18+** & **npm 9+** installed (`node -v`)
- **PostgreSQL 16** running on `localhost:5432` with database `campusfind_db`

### 1. Database Setup
```sql
CREATE DATABASE campusfind_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE campusfind_db TO postgres;
```

### 2. Run Backend (Port 8080)
```bash
cd smart-campus-lost-found/backend

# Run test suite
mvn test

# Start the Spring Boot server
mvn spring-boot:run
```
*The application initializes campus reference data, seed users, 8 buildings, 11 categories, and active test items automatically on first start.*

### 3. Run Frontend (Port 5173)
```bash
cd smart-campus-lost-found/frontend

# Install dependencies
npm install

# Start Vite development server
npm run dev
```
Open **`http://localhost:5173`** in your browser.

---

## 🔑 Demo Personas & Fast Role Switcher

You can switch between personas directly in the UI using the top navigation switcher or demo banner:

| Persona | Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- | :--- |
| **Alex Chen** | `ROLE_STUDENT` | `student@campus.edu` | `Password123!` | Report lost/found, submit claims, view QR pass |
| **Sarah Jenkins** | `ROLE_STAFF` | `staff@campus.edu` | `Password123!` | Verify OTPs, issue digital receipts, claim review |
| **Campus Administrator**| `ROLE_ADMIN` | `admin@campus.edu` | `Password123!` | Incident heatmap, audit logs, user management |
| **Priya Sharma** | `ROLE_STUDENT` | `priya@campus.edu` | `Password123!` | Peer student claimant |

---

## 📡 Key API Endpoints

| Method | Endpoint | Access | Purpose |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | Authenticate user and receive JWT bearer token |
| `GET` | `/api/locations` | Public | Retrieve 8 campus building zones and coordinates |
| `GET` | `/api/categories` | Public | Retrieve item taxonomy |
| `POST` | `/api/ai/analyze-image` | Public | Multimodal visual attribute extraction & validation |
| `POST` | `/api/ai/assist-description`| Public | Conversational description parsing |
| `GET` | `/api/found` | Public | Privacy-masked list of found items |
| `POST` | `/api/found` | Student/Staff | Submit a new found item with secret verification details |
| `GET` | `/api/matches/all` | Public | View AI match candidates with score breakdown |
| `POST` | `/api/claims/submit` | Student | Submit claim with blind verification answers |
| `POST` | `/api/pickup/verify` | Staff/Admin | Verify 6-digit OTP and generate signed receipt |
| `GET` | `/api/admin/dashboard` | Staff/Admin | Retrieve real-time KPIs and building incident heatmap |
| `GET` | `/api/admin/audit-logs` | Staff/Admin | Query compliance audit log |

---

## 🛡️ Privacy & Security Features
- **Stateless JJWT Authentication**: HS-512 signatures with 24-hour expiration.
- **EXIF Sanitization**: Camera metadata, GPS coordinates, and device identifiers are stripped from uploaded images before storage.
- **Zero Exposure**: Secret identifying markers (`privateVerificationDetails`) are sanitized from all public DTOs via Criteria API and projection.
- **Cryptographic Receipt Signature**: SHA-256 HMAC digest generated using secret campus salt, claimant ID, staff ID, and timestamp to prevent counterfeit collection receipts.

---

## 🧪 Testing & Quality Assurance
Run backend automated tests:
```bash
cd smart-campus-lost-found/backend
mvn test
```
**Test Coverage:**
- `AiMatchingEngineTest`: Verifies multi-signal mathematical weights, category gating, time penalty, and color compatibility.
- `AuthAndSecurityTest`: Verifies password hashing, JWT claims, and unauthorized rejection.
- `WorkflowIntegrationTest`: Verifies full end-to-end flow: Found Report creation $\rightarrow$ Semantic Match detection $\rightarrow$ Claim submission $\rightarrow$ Handover verification $\rightarrow$ Cryptographic receipt generation.

---

## 📄 License
This project is open-source and built for university campus deployment under the MIT License.
