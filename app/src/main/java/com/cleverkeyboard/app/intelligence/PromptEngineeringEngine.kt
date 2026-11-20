package com.cleverkeyboard.app.intelligence

import java.util.Locale

/**
 * Intelligent 6-Section Deterministic Prompt Engineering Engine.
 *
 * Refactored to eliminate enterprise spec bloat, stack ambiguity, and missing asset dependencies.
 * Produces concise, deterministic, and immediately executable prompts adhering to 5 core rules:
 * 1. Single-Stack Lock-In (ONE modern tool per layer; zero "or" branching)
 * 2. Phase-1 MVP Scoping (3–4 critical runnable files; advanced features deferred to Phase 2)
 * 3. Zero-Dependency Runnability (Procedural geometry/textures + self-contained seed data; zero remote 404s)
 * 4. Compact 6-Section Structure (Role, Objective, Stack, Features, Guardrails, Execution Rules)
 * 5. Actionable Execution Rule (Complete runnable code, exact file paths, terminal run commands)
 */
object PromptEngineeringEngine {

    enum class PromptDomain {
        WEB_DEV,
        MOBILE_DEV,
        BACKEND_API,
        DATA_AI,
        BUSINESS_MARKETING,
        PROFESSIONAL_EMAIL,
        CONTENT_WRITING,
        FITNESS_HEALTH,
        EDUCATION_TUTORING,
        GENERAL
    }

    /**
     * Generates a concise, deterministic, and immediately executable 6-section prompt.
     */
    fun generateStructuredPrompt(rawIdea: String): String {
        val trimmed = rawIdea.trim()
        if (trimmed.isEmpty()) {
            return stripBoundaryMarkers(generateDefaultPrompt())
        }

        val domain = detectDomain(trimmed)
        val title = generateTitle(trimmed, domain)

        val rawPrompt = when (domain) {
            PromptDomain.WEB_DEV -> buildWebDevPrompt(title, trimmed)
            PromptDomain.MOBILE_DEV -> buildMobileDevPrompt(title, trimmed)
            PromptDomain.BACKEND_API -> buildBackendPrompt(title, trimmed)
            PromptDomain.DATA_AI -> buildAiDataPrompt(title, trimmed)
            PromptDomain.BUSINESS_MARKETING -> buildBusinessPrompt(title, trimmed)
            PromptDomain.PROFESSIONAL_EMAIL -> buildEmailPrompt(title, trimmed)
            PromptDomain.CONTENT_WRITING -> buildWritingPrompt(title, trimmed)
            PromptDomain.FITNESS_HEALTH -> buildFitnessPrompt(title, trimmed)
            PromptDomain.EDUCATION_TUTORING -> buildEducationPrompt(title, trimmed)
            PromptDomain.GENERAL -> buildGeneralPrompt(title, trimmed)
        }
        return stripBoundaryMarkers(rawPrompt)
    }

    /**
     * Strips stray leading 'B' and trailing 'E' boundary characters, enclosing markdown fences,
     * and accidental surrounding quotes/bold formatting.
     */
    fun stripBoundaryMarkers(raw: String): String {
        var text = raw.trim()
        var changed = true
        var passes = 0

        while (changed && passes < 10) {
            val prev = text
            passes++
            text = text.trim()

            // Strip markdown block fences
            if (text.startsWith("```markdown", ignoreCase = true)) {
                text = text.substring(11).trim()
            } else if (text.startsWith("```")) {
                text = text.substring(3).trim()
            }
            if (text.endsWith("```")) {
                text = text.substring(0, text.length - 3).trim()
            }

            // Strip quotes and bold
            while (
                (text.startsWith("**") && text.endsWith("**") && text.length >= 4) ||
                (text.startsWith("*") && text.endsWith("*") && text.length >= 2) ||
                (text.startsWith("\"") && text.endsWith("\"") && text.length >= 2) ||
                (text.startsWith("'") && text.endsWith("'") && text.length >= 2)
            ) {
                if (text.startsWith("**") && text.endsWith("**") && text.length >= 4) {
                    text = text.substring(2, text.length - 2).trim()
                } else if (text.startsWith("*") && text.endsWith("*") && text.length >= 2) {
                    text = text.substring(1, text.length - 1).trim()
                } else if (text.startsWith("\"") && text.endsWith("\"") && text.length >= 2) {
                    text = text.substring(1, text.length - 1).trim()
                } else if (text.startsWith("'") && text.endsWith("'") && text.length >= 2) {
                    text = text.substring(1, text.length - 1).trim()
                }
            }

            // Strip stray leading 'B' or 'B\n' boundary marker
            if (text.startsWith("B\r\n") || text.startsWith("B\n") || text.startsWith("B ") || text.startsWith("B#")) {
                text = text.replaceFirst(Regex("^(?i)B\\s*"), "").trim()
            } else if (text.matches(Regex("(?s)^(?i)B\\s*#.*"))) {
                text = text.replaceFirst(Regex("^(?i)B\\s*"), "").trim()
            }

            // Strip stray trailing 'E' or '\nE' boundary marker
            if (text.endsWith("\r\nE", ignoreCase = true) || text.endsWith("\nE", ignoreCase = true) ||
                text.endsWith(" E", ignoreCase = true) || text.equals("E", ignoreCase = true)
            ) {
                text = text.replace(Regex("(?i)\\s*E$"), "").trim()
            }

            changed = (prev != text)
        }

        return text.trim()
    }

    private fun detectDomain(text: String): PromptDomain {
        val lower = text.lowercase(Locale.ROOT)
        return when {
            lower.contains("restaurant") || lower.contains("website") || lower.contains("landing page") ||
                    lower.contains("frontend") || lower.contains("html") || lower.contains("css") ||
                    lower.contains("react") || lower.contains("ui") || lower.contains("ux") ||
                    lower.contains("web app") || lower.contains("portfolio") || lower.contains("ecommerce") ||
                    lower.contains("shop") || lower.contains("store") || lower.contains("three") ||
                    lower.contains("3d") || lower.contains("supermarket") || lower.contains("webgl") -> PromptDomain.WEB_DEV

            lower.contains("android") || lower.contains("ios") || lower.contains("mobile app") ||
                    lower.contains("flutter") || lower.contains("kotlin") || lower.contains("swift") -> PromptDomain.MOBILE_DEV

            lower.contains("backend") || lower.contains("api") || lower.contains("database") ||
                    lower.contains("sql") || lower.contains("fastapi") || lower.contains("django") ||
                    lower.contains("node") || lower.contains("server") || lower.contains("scrape") ||
                    lower.contains("microservice") -> PromptDomain.BACKEND_API

            lower.contains("machine learning") || lower.contains("ai") || lower.contains("model") ||
                    lower.contains("neural") || lower.contains("data analysis") || lower.contains("dataset") ||
                    lower.contains("llm") || lower.contains("rag") -> PromptDomain.DATA_AI

            lower.contains("email") || lower.contains("mail") || lower.contains("salary") ||
                    lower.contains("leave letter") || lower.contains("resignation") || lower.contains("application") -> PromptDomain.PROFESSIONAL_EMAIL

            lower.contains("business") || lower.contains("marketing") || lower.contains("pitch") ||
                    lower.contains("startup") || lower.contains("sales") || lower.contains("campaign") ||
                    lower.contains("strategy") || lower.contains("growth") -> PromptDomain.BUSINESS_MARKETING

            lower.contains("workout") || lower.contains("gym") || lower.contains("diet") ||
                    lower.contains("fitness") || lower.contains("calories") || lower.contains("weight loss") -> PromptDomain.FITNESS_HEALTH

            lower.contains("explain") || lower.contains("teach") || lower.contains("learn") ||
                    lower.contains("study plan") || lower.contains("homework") || lower.contains("lesson") ||
                    lower.contains("course") -> PromptDomain.EDUCATION_TUTORING

            lower.contains("blog") || lower.contains("article") || lower.contains("story") ||
                    lower.contains("script") || lower.contains("write a") || lower.contains("essay") ||
                    lower.contains("copywriting") -> PromptDomain.CONTENT_WRITING

            else -> PromptDomain.GENERAL
        }
    }

    private fun generateTitle(idea: String, domain: PromptDomain): String {
        val clean = idea.replace(Regex("(?i)^(build|create|design|write|generate|make|develop|plan)\\s+(a\\s+|an\\s+)?"), "")
            .trim()
            .split(" ")
            .take(5)
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

        return if (clean.isNotBlank()) clean else when (domain) {
            PromptDomain.WEB_DEV -> "Web Application"
            PromptDomain.MOBILE_DEV -> "Mobile Application"
            PromptDomain.BACKEND_API -> "Backend Service & API"
            PromptDomain.DATA_AI -> "AI & Data Pipeline"
            PromptDomain.BUSINESS_MARKETING -> "Business Strategy Blueprint"
            PromptDomain.PROFESSIONAL_EMAIL -> "Executive Correspondence"
            PromptDomain.CONTENT_WRITING -> "High-Impact Content Piece"
            PromptDomain.FITNESS_HEALTH -> "Fitness & Health Protocol"
            PromptDomain.EDUCATION_TUTORING -> "Educational Masterclass Guide"
            PromptDomain.GENERAL -> "Project Execution Plan"
        }
    }

    private fun buildWebDevPrompt(title: String, idea: String): String {
        val isSpatialOr3D = idea.contains("3d", ignoreCase = true) ||
                idea.contains("supermarket", ignoreCase = true) ||
                idea.contains("three", ignoreCase = true) ||
                idea.contains("webgl", ignoreCase = true) ||
                idea.contains("spatial", ignoreCase = true) ||
                idea.contains("virtual", ignoreCase = true)

        return if (isSpatialOr3D) {
            """
# $title — Phase 1 MVP Specification

## 1. Role
Act as an elite Principal Full-Stack Engineer and Lead WebGL/Spatial Web Developer specializing in high-performance browser 3D apps.

## 2. Core Objective (Phase 1 MVP Scope)
Build a complete, fully working Phase 1 MVP for: "$idea".
- **Phase 1 Deliverable:** Self-contained, runnable 3D scene with keyboard/mouse navigation, procedural aisles, raycast inspection, cart HUD, and 2D grid toggle across 4 files:
  1. `src/app/page.tsx` (Layout, HUD overlay, 3D canvas viewport)
  2. `src/components/StoreScene.tsx` (Three.js WebGL canvas, lighting, procedural aisles, camera controls)
  3. `src/components/CartDrawer.tsx` (Cart drawer, item counter, subtotal, 2D grid fallback)
  4. `src/store/useCartStore.ts` (Zustand state store with embedded seed items)
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Backend API & Real-Time Sync):* Fastify/Next.js API routes, PostgreSQL schema with Prisma ORM, and WebSocket real-time inventory updates.
  - *Phase 3 (Payments, Admin & Polish):* Stripe checkout gateway, customer authentication, admin inventory dashboard, and LOD asset optimization.
- **Next-Phase Trigger Rule:** After outputting all Phase 1 code, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to scaffold the backend API, database schema, and server endpoints."*).

## 3. Locked Tech Stack
- Framework: Next.js 14 (App Router, TypeScript)
- 3D Rendering: Three.js with @react-three/fiber and @react-three/drei
- State: Zustand (persisted) | Styling: Tailwind CSS & Lucide React | Package Manager: npm

## 4. Key Features & UX
- **Navigation:** First-person movement (WASD + mouse drag) with bounds collision preventing shelf clipping.
- **Procedural 3D Assets:** Generate aisles, shelves, and products procedurally using Three.js primitives (`BoxGeometry`, `CylinderGeometry`) with canvas label textures. Strictly forbid external `.glb`/`.gltf` asset loading to eliminate remote 404 crashes.
- **Raycasting:** Hover highlights product outlines; click opens inspection modal with "Add to Cart".
- **HUD & Cart:** Non-blocking 2D HUD with mini-map, cart counter badge, slide-out drawer with subtotal, and 2D Grid fallback button.
- **Seed Catalog:** In-memory dataset of 8 distinct grocery items (Produce, Bakery, Dairy, Pantry) with coordinates.

## 5. Technical & Fallback Guardrails
- **Zero-Dependency Runnability:** Runs immediately on `npm run dev` with zero missing remote assets or broken external links.
- **Viewport Resize Handling:** Explicitly handle window and container resize events by updating `camera.aspect = width / height`, calling `camera.updateProjectionMatrix()`, and updating `renderer.setSize(width, height)`.
- **Memory & Resource Disposal:** Dispose geometries (`geometry.dispose()`), materials (`material.dispose()`), and textures (`texture.dispose()`) ONLY on component unmount / cleanup in `useEffect` (do NOT dispose resources during the render or animation frame loop).
- **Performance & Fallback:** Locked 60 FPS using instancing and low-poly primitives; accessible 2D grid toggle for low-spec devices.

## 6. Execution & Output Rules
- Output complete, runnable code for each of the 4 source files with exact file paths.
- Zero placeholders or stubs (`// TODO`); all features fully coded.
- Terminal commands: `npm install three @types/three @react-three/fiber @react-three/drei zustand lucide-react && npm run dev`.
""".trimIndent()
        } else {
            """
# $title — Phase 1 MVP Specification

## 1. Role
Act as a Principal Full-Stack Web Architect and Lead Frontend Engineer specializing in high-performance modern web applications.

## 2. Core Objective (Phase 1 MVP Scope)
Build a complete, production-ready Phase 1 Working MVP for: "$idea".
- **Phase 1 Scope:** Deliver a self-contained, fully interactive web application (responsive landing layout, interactive core workflows, local state management, and embedded seed data) across 4 cohesive source files:
  1. `src/app/page.tsx` (Hero section, value proposition, and main view)
  2. `src/components/CatalogSection.tsx` (Interactive categorized items, filters, and detail modal)
  3. `src/components/CartDrawer.tsx` (Slide-out drawer, item counters, subtotal, and checkout preview)
  4. `src/store/useAppStore.ts` (Zustand state store with embedded seed items and local persistence)
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Backend API & Database):* Next.js API route handlers, PostgreSQL database schema with Prisma ORM, and search indexing.
  - *Phase 3 (Payments, Auth & Admin):* Stripe payment gateway, OAuth customer authentication, and admin analytics dashboard.
- **Next-Phase Trigger Rule:** After outputting all Phase 1 code, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to scaffold the backend API, database schema, and server endpoints."*).

## 3. Locked Tech Stack
- Framework: Next.js 14 (App Router, TypeScript)
- Styling & Design Tokens: Tailwind CSS
- UI Icons: Lucide React
- State Management: Zustand (client-side persisted)
- Package Manager: npm

## 4. Key Features & UX
- **Responsive Layout:** Mobile-first responsive hierarchy: Hero banner with direct CTA, categorized interactive offerings with rich cards, and clean footer.
- **Interactive Workflows:** Filter items by category, search by keyword, view item details in modal, and adjust quantities in real time.
- **Embedded Seed Data:** Self-contained in-memory dataset of 8–10 realistic items with prices, categories, tags, and status metadata.
- **Micro-Interactions:** Smooth CSS transitions, active press states, and zero layout shift (CLS < 0.05).

## 5. Technical & Fallback Guardrails
- **Zero-Dependency Runnability:** Runs immediately upon `npm run dev` with zero missing remote assets or broken external links.
- **Semantic Standards:** 100% semantic HTML5 landmarks, WCAG 2.1 AA accessible color contrast, and keyboard-trappable modals.
- **Error Boundaries:** Graceful empty states for zero search results and resilient fallback handlers.

## 6. Execution & Output Rules
- Output complete, production-ready code with exact file paths for all 4 source files.
- Avoid all placeholder stubs (`// TODO`, `// implement later`); every workflow must be fully functional.
- Provide exact terminal setup and run commands (`npm install zustand lucide-react && npm run dev`).
""".trimIndent()
        }
    }

    private fun buildMobileDevPrompt(title: String, idea: String): String {
        return """
# $title — Phase 1 MVP Specification

## 1. Role
Act as a Principal Mobile Systems Architect and Lead Native Android Engineer specializing in modern Jetpack Compose applications.

## 2. Core Objective (Phase 1 MVP Scope)
Architect and deliver a fully functional Phase 1 Working MVP for: "$idea".
- **Phase 1 Scope:** Deliver a self-contained, runnable Android app (navigation graph, core screen composables, interactive state hoisting, and local in-memory data repository) across 4 clean Kotlin source files:
  1. `MainActivity.kt` (Entry point and root Compose Scaffold)
  2. `HomeScreen.kt` (Core catalog view, category filtering, and item list)
  3. `DetailSheet.kt` (Modal bottom sheet with item inspection and action button)
  4. `MainViewModel.kt` (StateFlow state management and embedded seed data)
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Persistence & Networking):* Room database caching, Retrofit/Ktor REST API client, and offline-first repository synchronization.
  - *Phase 3 (Auth, Payments & Polish):* Firebase/OAuth authentication, Google Play In-App Billing, and push notification delivery.
- **Next-Phase Trigger Rule:** After outputting all Phase 1 code, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to scaffold Room database persistence and Retrofit network integration."*).

## 3. Locked Tech Stack
- Language: Kotlin 1.9+
- UI Framework: Jetpack Compose with Material 3
- Architecture: MVVM with StateFlow & ViewModel
- Dependency Injection: Manual constructor injection / Koin
- Local State: In-Memory Repository with SharedPreferences fallback

## 4. Key Features & UX
- **Navigation & Flow:** Compose Navigation connecting Home, Filter Sheet, and Action screens.
- **Modern UI Components:** Material 3 TopAppBar, Scaffold, LazyColumn with animated item placement, and modal bottom sheet.
- **Stateful Interaction:** Unidirectional Data Flow (UDF) with immediate tactile feedback on user actions.
- **Embedded Seed Data:** Pre-populated in-memory repository with 8–10 realistic entity models.

## 5. Technical & Fallback Guardrails
- **Zero-Crash Runnability:** App must compile and run on emulator/device immediately without missing drawables or network errors.
- **Performance:** 60fps fluid UI with proper `key()` parameters in lazy layouts to prevent recomposition churn.
- **Accessibility:** Minimum touch targets (48dp) and explicit `contentDescription` on all icon buttons.

## 6. Execution & Output Rules
- Output complete, idiomatic Kotlin code with package declarations and imports.
- Provide exact file paths for all 4 Kotlin files.
- Provide Gradle dependencies and launch instructions.
""".trimIndent()
    }

    private fun buildBackendPrompt(title: String, idea: String): String {
        return """
# $title — Phase 1 MVP Specification

## 1. Role
Act as a Principal Backend Architect and Distributed Systems Engineer specializing in high-throughput, type-safe API services.

## 2. Core Objective (Phase 1 MVP Scope)
Design and implement a complete, production-ready Phase 1 API service for: "$idea".
- **Phase 1 Scope:** Core RESTful API endpoints with schema validation, transactional operations, error handling middleware, and self-contained seed data across 4 structured files:
  1. `src/server.ts` (Fastify application setup, plugins, and startup logic)
  2. `src/routes/api.ts` (RESTful route handlers for core resources)
  3. `src/schemas/validation.ts` (Zod schemas for request validation)
  4. `src/data/seed.ts` (Self-contained in-memory database and seed records)
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Database Persistence & Auth):* PostgreSQL integration via Prisma ORM, database migrations, and JWT authentication middleware.
  - *Phase 3 (Distributed Caching & Telemetry):* Redis cache layer, BullMQ background job queues, and OpenTelemetry distributed tracing.
- **Next-Phase Trigger Rule:** After outputting all Phase 1 code, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to scaffold Prisma database migrations and JWT auth middleware."*).

## 3. Locked Tech Stack
- Runtime: Node.js 20+ with TypeScript
- Web Framework: Fastify
- Validation: Zod
- Database Layer: In-Memory Repository with SQLite (Prisma ORM) compatibility
- Package Manager: npm

## 4. Key Features & UX
- **CRUD Endpoints:** Strongly-typed route handlers for core resources with status codes (200, 201, 400, 404, 500).
- **Request Validation:** Strict Zod schema validation for params, query, and request bodies with structured error responses.
- **Health Check & Diagnostics:** Expose `/health` with uptime and system diagnostic telemetry.
- **Seed Script:** Embedded seed records populating initial items for instant testability with curl.

## 5. Technical & Fallback Guardrails
- **Zero-Dependency Runnability:** Service boots with `npm run dev` and answers requests immediately with zero external DB setup required.
- **Security Guardrails:** Helmet headers, parameterized data access (zero SQL injection), and global unhandled error interceptor.
- **Input Sanitization:** Stripped unexpected payload keys and strict type coercion.

## 6. Execution & Output Rules
- Provide complete runnable TypeScript files with exact file paths.
- Provide exact terminal setup commands (`npm install fastify zod ...`, `npm run dev`) and example curl verification commands.
""".trimIndent()
    }

    private fun buildAiDataPrompt(title: String, idea: String): String {
        return """
# $title — Phase 1 MVP Pipeline

## 1. Role
Act as a Lead AI/ML Scientist and Production ML Engineer specializing in generative AI, data pipelines, and intelligent automation.

## 2. Core Objective (Phase 1 MVP Scope)
Architect and develop a complete, executable Python AI processing pipeline for: "$idea".
- **Phase 1 Scope:** Self-contained executable pipeline (data ingestion, vector indexing, grounded prompt generation, and REST endpoint) across 3 clean files:
  1. `main.py` (FastAPI app exposing `/process` and `/health`)
  2. `pipeline.py` (Ingestion, semantic vector search, and model inference)
  3. `sample_data.py` (Embedded sample documents and seed questions)
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Persistent Vector DB & Fine-Tuning):* Qdrant/Pinecone persistent storage, async task queue with Celery, and evaluation telemetry.
  - *Phase 3 (Multi-Modal & Human-in-the-Loop):* Vision/Audio multimodal processing, human review dashboard, and rate-limited API gateway.
- **Next-Phase Trigger Rule:** After outputting all Phase 1 code, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to connect persistent vector storage and Celery task queues."*).

## 3. Locked Tech Stack
- Language: Python 3.11+
- API Framework: FastAPI with Uvicorn
- AI/LLM SDK: google-genai (Gemini 2.5 Flash)
- Vector Search: ChromaDB (in-memory mode)
- Data Validation: Pydantic v2

## 4. Key Features & Pipeline
- **Document Ingestion:** Clean document chunking with metadata tagging.
- **Hybrid Retrieval:** Dense semantic search coupled with grounded prompt construction.
- **Structured Output:** Pydantic schema enforcing structured JSON output for all inferences.
- **Embedded Seed Data:** Self-contained text documents and test queries for immediate execution.

## 5. Technical & Fallback Guardrails
- **Zero Hallucination Guardrails:** Strict prompt boundaries requiring source citation; fallback to "Insufficient context" when confidence is low.
- **Offline Runnability:** Fallback mock embeddings for offline execution without requiring an active API key during local tests.
- **Error Handling:** Graceful API timeout handling with exponential backoff retries.

## 6. Execution & Output Rules
- Output complete, runnable Python files with exact file paths.
- Provide exact terminal commands (`pip install -r requirements.txt`, `uvicorn main:app --reload`).
""".trimIndent()
    }

    private fun buildBusinessPrompt(title: String, idea: String): String {
        return """
# $title — 30-Day Launch Blueprint

## 1. Role
Act as a Chief Strategy Officer and Growth Marketing Partner specializing in rapid market validation and product launch execution.

## 2. Core Objective (Phase 1 Scope)
Formulate an actionable, 30-day market entry and customer acquisition blueprint for: "$idea".
- **Phase 1 Deliverables:** Core Ideal Customer Profile (ICP), validated value proposition, high-converting offer structure, unit economics model, and 30-day execution checklist.
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Channel Scaling & Paid Acquisition):* Programmatic SEO, high-intent Google/Meta paid ads engine, and automated referral loop.
  - *Phase 3 (Enterprise Tiers & Partnerships):* Enterprise custom pricing tiers, reseller partner program, and international localization.
- **Next-Phase Trigger Rule:** After outputting the Phase 1 blueprint, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to generate the paid acquisition ad copy and programmatic SEO strategy."*).

## 3. Locked Strategy Framework
- Business Model: Subscription / Value-based transactional model with 70%+ gross margin target.
- Target Market: High-intent early adopters with acute pain points and proven willingness to pay.
- Unit Economics Baseline: Target Customer Acquisition Cost (CAC) to Lifetime Value (LTV) ratio > 3:1 with payback period < 9 months.

## 4. Key Features & Go-To-Market
- **Core Value Proposition:** Single punchy hook clearly articulating the 10x improvement over the status quo.
- **Acquisition Funnel:** Primary organic acquisition loop (content/SEO) paired with targeted high-intent outbound messaging.
- **Conversion Flow:** Frictionless landing page architecture with social proof, transparent pricing, and instant self-serve onboarding.

## 5. Financial & Risk Guardrails
- **Capital Discipline:** Zero unvalidated upfront ad spend; require 10 pre-orders/commitments before scaling.
- **Churn Mitigation:** Onboarding activation sequence ensuring users achieve the "aha" moment within 5 minutes.

## 6. Execution & Actionable Plan
- Prioritized 30-day execution sprint broken into 4 weekly milestones (Week 1: Positioning, Week 2: MVP Landing Page, Week 3: Alpha Outreach, Week 4: Launch).
- Clear, actionable steps with specific owner roles and success metrics.
""".trimIndent()
    }

    private fun buildEmailPrompt(title: String, idea: String): String {
        return """
# $title — Executive Communication Strategy

## 1. Role
Act as an Executive Communications Director and Master Corporate Copywriter specializing in high-stakes corporate correspondence.

## 2. Core Objective
Draft a concise, compelling, results-driven professional email regarding: "$idea".
- **Primary Goal:** Convey essential context, establish credibility, and prompt immediate action while minimizing the recipient's cognitive load.
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Objection Handling & Follow-Up Sequence):* Response scripts for objections, counteroffers, or non-responsiveness.
  - *Phase 3 (Executive Alignment Deck):* 5-slide alignment presentation and follow-up contract/proposal draft.
- **Next-Phase Trigger Rule:** After outputting the primary email draft, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to generate the objection-handling scripts and follow-up sequences."*).

## 3. Locked Communication Framework
- Length: Strictly 150 to 220 words for the main email body.
- Structure: Context Hook (Sentence 1-2) -> Value Case / Evidence (Bullet points) -> Single Frictionless Call to Action.
- Register: Confident, respectful, collaborative, and executive-level clarity.

## 4. Key Deliverables & Drafts
- **Subject Lines:** Provide 3 distinct, high-open variations:
  1. *Direct & Urgent* (Action-focused)
  2. *Value-Centric* (Outcome-focused)
  3. *Contextual* (Referencing specific project/event)
- **Main Email Body:** Crisp, ready-to-send draft with bracketed personalization tokens clearly identified.
- **Follow-Up Sequence:** Polite 3-day follow-up message if no response is received.

## 5. Guardrails & Tone Audit
- Zero passive voice, corporate clichés ("touch base", "circle back"), or defensive language.
- Explicit single next step with a specific proposed date/time window.

## 6. Execution & Output Rules
- Output the complete, copy-paste ready email text with subject lines, main body, and follow-up template.
""".trimIndent()
    }

    private fun buildWritingPrompt(title: String, idea: String): String {
        return """
# $title — High-Impact Content Architecture

## 1. Role
Act as an Award-Winning Author, Senior Editorial Director, and Master Narrative Strategist.

## 2. Core Objective
Craft a compelling, deeply engaging, and memorable written piece on: "$idea".
- **Deliverable:** A complete, publication-ready article/essay featuring an arresting opening hook, structured arguments with concrete analogies, and a resonant conclusion.
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Distribution & Social Syndication):* Viral social thread adaptation (X/LinkedIn), newsletter summary, and email pitch.
  - *Phase 3 (Multimedia Repurposing):* Podcast/video script outlines, infographic concept briefs, and discussion prompts.
- **Next-Phase Trigger Rule:** After outputting the full article draft, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to adapt this piece into social threads and newsletter variants."*).

## 3. Locked Narrative Framework
- Structure: The Arresting Paradox (Opening Hook) -> The Escalating Tension (Core Conflict) -> The Key Revelation (Central Insight) -> The Actionable Takeaway (Resolution).
- Pacing: Rhythmic sentence variation mixing punchy short observations with lyrical, immersive details.

## 4. Key Content Sections
- **Title & Subtitle:** High-curiosity, benefit-driven headline.
- **The Hook:** Counterintuitive observation or vivid scene in the opening 2 paragraphs.
- **Body Sections:** 3 clearly titled subheadings each advancing a distinct, evidence-backed argument with real-world analogies.
- **The Climax & Takeaway:** Quotable synthesis delivering lasting intellectual value.

## 5. Style & Quality Guardrails
- Active voice exclusively; eliminate filler adjectives and redundant adverbs.
- Ground abstract assertions in concrete, sensory real-world examples.

## 6. Execution & Output Rules
- Output the complete, un-truncated draft ready for immediate publishing with formatting and pull-quotes.
""".trimIndent()
    }

    private fun buildFitnessPrompt(title: String, idea: String): String {
        return """
# $title — 4-Week Protocol

## 1. Role
Act as an Elite Strength & Conditioning Specialist and Clinical Sports Nutritionist.

## 2. Core Objective (Phase 1 Scope)
Design a practical, scientifically validated 4-week fitness and nutrition protocol for: "$idea".
- **Phase 1 Scope:** Deliver a structured 4-week progressive overload training schedule, daily macronutrient breakdown, and recovery protocol.
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Advanced Periodization & Nutrition):* Undulating periodization, autoregulation protocols, and specialized hypertrophy blocks.
  - *Phase 3 (Peak Performance & Longevity):* Deload peaking, bloodwork biomarker tracking, and mobility routines.
- **Next-Phase Trigger Rule:** After outputting the Phase 1 protocol, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to build the undulating periodization and advanced hypertrophy blocks."*).

## 3. Locked Training & Nutrition Framework
- Training Split: 3-Day Full-Body Compound Lift Split (Monday / Wednesday / Friday).
- Progression Model: Double progression (hit upper rep target before adding load).
- Nutrition: Whole-food focused with high protein (1.8g to 2.2g per kg bodyweight) and structured hydration targets.

## 4. Weekly Workout & Nutrition Schedule
- **Workout Sessions:** Detailed tables listing exercise, primary movement pattern, sets (3-4), reps (6-12), RPE (Rate of Perceived Exertion: 7-9), and rest periods (90-120s).
- **Daily Meal Plan:** Macro targets and 3 sample meal configurations with nutrient timing around training.

## 5. Safety & Recovery Guardrails
- Form cues and joint-friendly movement regressions for all compound exercises.
- Deload trigger: Reduce volume by 40% if joint discomfort or sleep disruption exceeds 48 hours.

## 6. Execution & Tracking Rules
- Provide clear workout tracking tables and progress evaluation criteria (weekly weight averages and strength logs).
""".trimIndent()
    }

    private fun buildEducationPrompt(title: String, idea: String): String {
        return """
# $title — Concept Mastery Guide

## 1. Role
Act as a Distinguished Feynman-Technique Educator renowned for explaining complex concepts with intuitive clarity.

## 2. Core Objective
Design a complete, step-by-step conceptual mastery masterclass for: "$idea".
- **Goal:** Break down the topic from first principles so any curious learner achieves deep, practical intuition.
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Interactive Exercises & Problem Sets):* Graded practice challenges, coding katas / real-world case scenarios, and detailed solution rubrics.
  - *Phase 3 (Advanced Deep Dive & Edge Cases):* Performance trade-offs, theoretical limits, and industry case studies.
- **Next-Phase Trigger Rule:** After outputting the core masterclass guide, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to generate the interactive problem sets and graded practice challenges."*).

## 3. Locked Pedagogical Framework
- Method: Intuition First -> Relatable Analogy -> Formal Definition -> Worked Demonstration.
- Language: Plain, vivid English; define every necessary technical term immediately upon introduction.

## 4. Key Curriculum Sections
- **The Core Intuition:** First-principles explanation answering "Why does this exist?" in 3 paragraphs.
- **The Relatable Analogy:** A memorable real-world metaphor illustrating the underlying mechanism.
- **Worked Practical Example:** Step-by-step walkthrough of a concrete problem with clear reasoning.
- **Common Misconceptions:** 3 subtle mistakes beginners make and how to avoid them.

## 5. Guardrails & Comprehension Checks
- Zero hand-waving or unexplained jargon.
- Include a 3-question self-test with solutions to verify deep understanding.

## 6. Execution & Output Rules
- Deliver the complete, structured masterclass guide with a 1-page summary cheat sheet.
""".trimIndent()
    }

    private fun buildGeneralPrompt(title: String, idea: String): String {
        return """
# $title — Phase 1 Action Plan

## 1. Role
Act as an Elite Systems Architect and Strategic Operations Specialist.

## 2. Core Objective (Phase 1 Scope)
Architect and deliver an actionable Phase 1 execution plan for: "$idea".
- **Phase 1 Scope:** Deliver a concrete, self-contained operational blueprint detailing the immediate critical deliverables and step-by-step procedures.
- **Upcoming Phases Roadmap:**
  - *Phase 2 (Automation & Scaling):* Automated workflows, integration pipelines, and performance optimization.
  - *Phase 3 (Enterprise Hardening & Polish):* Long-term stability, advanced governance, and expansion roadmap.
- **Next-Phase Trigger Rule:** After outputting the Phase 1 plan, end with a 1-sentence prompt the user can reply with to trigger Phase 2 (e.g., *"Reply with 'Proceed to Phase 2' to design the automation workflows and scale architecture."*).

## 3. Locked Framework & Approach
- Methodology: Direct execution model focused on core functional milestones.
- Resource Allocation: Prioritized 80/20 effort distribution maximizing immediate impact.

## 4. Key Deliverables & Procedures
- **Immediate Deliverables:** Tangible assets and workflows to establish first.
- **Step-by-Step Procedure:** Numbered chronological sequence of actions with clear completion criteria.
- **Verification Matrix:** Concrete checklist confirming operational readiness.

## 5. Quality & Risk Guardrails
- Measurable verification standards; eliminate ambiguity and vague advice.
- Explicit contingency steps for primary failure modes.

## 6. Execution & Output Rules
- Output a complete, self-contained implementation blueprint ready for immediate execution.
""".trimIndent()
    }

    private fun generateDefaultPrompt(): String {
        return buildGeneralPrompt("Universal Project Execution", "Plan and execute this objective with world-class excellence")
    }
}
