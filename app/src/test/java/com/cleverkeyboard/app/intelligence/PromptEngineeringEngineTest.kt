package com.cleverkeyboard.app.intelligence

import org.junit.Assert.*
import org.junit.Test

class PromptEngineeringEngineTest {

    @Test
    fun testWebDevPromptStructureAndRoadmap() {
        val prompt = PromptEngineeringEngine.generateStructuredPrompt("build a restaurant website")

        assertTrue("Should start with title heading", prompt.startsWith("# "))
        assertTrue("Must contain Section 1 Role", prompt.contains("## 1. Role"))
        assertTrue("Must contain Section 2 Core Objective", prompt.contains("## 2. Core Objective"))
        assertTrue("Must contain Section 3 Locked Tech Stack", prompt.contains("## 3. Locked Tech Stack"))
        assertTrue("Must contain Section 4 Key Features", prompt.contains("## 4. Key Features"))
        assertTrue("Must contain Section 5 Technical Guardrails", prompt.contains("## 5. Technical & Fallback Guardrails"))
        assertTrue("Must contain Section 6 Execution Rules", prompt.contains("## 6. Execution & Output Rules"))

        // Section 2 Roadmap & Trigger checks
        assertTrue("Must contain Upcoming Phases Roadmap in Section 2", prompt.contains("Upcoming Phases Roadmap:"))
        assertTrue("Must contain Phase 2 in Roadmap", prompt.contains("Phase 2"))
        assertTrue("Must contain Phase 3 in Roadmap", prompt.contains("Phase 3"))
        assertTrue("Must contain Next-Phase Trigger Rule in Section 2", prompt.contains("Next-Phase Trigger Rule:"))
        assertTrue("Must instruct 1-sentence prompt to trigger Phase 2", prompt.contains("Proceed to Phase 2"))

        // Word count check
        val wordCount = prompt.split(Regex("\\s+")).size
        assertTrue("Prompt should be concise and under 500 words (actual: $wordCount)", wordCount < 500)
    }

    @Test
    fun test3DSpatialPromptResizeAndDisposalGuardrails() {
        val prompt = PromptEngineeringEngine.generateStructuredPrompt("build a 3d supermarket ecommerce web application")

        assertTrue("Must contain Section 2 Core Objective", prompt.contains("## 2. Core Objective (Phase 1 MVP Scope)"))
        assertTrue("Must contain Upcoming Phases Roadmap in Section 2", prompt.contains("Upcoming Phases Roadmap:"))
        assertTrue("Must contain Phase 2 (Backend API & Real-Time Sync)", prompt.contains("Phase 2"))
        assertTrue("Must contain Phase 3 (Payments, Admin & Polish)", prompt.contains("Phase 3"))
        assertTrue("Must contain Next-Phase Trigger Rule", prompt.contains("Next-Phase Trigger Rule:"))

        // Section 5 WebGL Resize and Disposal Guardrails
        assertTrue("Must explicitly specify viewport resize handling", prompt.contains("Viewport Resize Handling:"))
        assertTrue("Must specify camera.aspect", prompt.contains("camera.aspect"))
        assertTrue("Must specify renderer.setSize", prompt.contains("renderer.setSize"))
        assertTrue("Must specify memory & resource disposal", prompt.contains("Memory & Resource Disposal:"))
        assertTrue("Must specify disposing geometries and materials", prompt.contains("geometry.dispose()"))
        assertTrue("Must specify disposing materials", prompt.contains("material.dispose()"))
        assertTrue("Must specify disposal strictly on unmount in useEffect", prompt.contains("useEffect"))
        assertTrue("Must forbid disposal during render loop", prompt.contains("do NOT dispose resources during the render or animation frame loop"))

        // Word count check
        val wordCount = prompt.split(Regex("\\s+")).size
        assertTrue("Spatial 3D prompt should be under 500 words (actual: $wordCount)", wordCount < 500)
    }

    @Test
    fun testMobileDevPromptRoadmap() {
        val prompt = PromptEngineeringEngine.generateStructuredPrompt("create an android kotlin food delivery app")

        assertTrue("Must contain Section 2 Core Objective", prompt.contains("## 2. Core Objective"))
        assertTrue("Must contain Upcoming Phases Roadmap", prompt.contains("Upcoming Phases Roadmap:"))
        assertTrue("Must contain Phase 2", prompt.contains("Phase 2"))
        assertTrue("Must contain Phase 3", prompt.contains("Phase 3"))
        assertTrue("Must contain Next-Phase Trigger Rule", prompt.contains("Next-Phase Trigger Rule:"))
    }

    @Test
    fun testBackendPromptRoadmap() {
        val prompt = PromptEngineeringEngine.generateStructuredPrompt("develop a fastify rest api backend service")

        assertTrue("Must contain Section 2 Core Objective", prompt.contains("## 2. Core Objective"))
        assertTrue("Must contain Upcoming Phases Roadmap", prompt.contains("Upcoming Phases Roadmap:"))
        assertTrue("Must contain Phase 2", prompt.contains("Phase 2"))
        assertTrue("Must contain Phase 3", prompt.contains("Phase 3"))
        assertTrue("Must contain Next-Phase Trigger Rule", prompt.contains("Next-Phase Trigger Rule:"))
    }

    @Test
    fun testAiDataPromptRoadmap() {
        val prompt = PromptEngineeringEngine.generateStructuredPrompt("build a python rag semantic vector search pipeline")

        assertTrue("Must contain Section 2 Core Objective", prompt.contains("## 2. Core Objective"))
        assertTrue("Must contain Upcoming Phases Roadmap", prompt.contains("Upcoming Phases Roadmap:"))
        assertTrue("Must contain Phase 2", prompt.contains("Phase 2"))
        assertTrue("Must contain Phase 3", prompt.contains("Phase 3"))
        assertTrue("Must contain Next-Phase Trigger Rule", prompt.contains("Next-Phase Trigger Rule:"))
    }

    @Test
    fun testStripBoundaryMarkersLeadingBAndTrailingE() {
        val samplePrompt = "# Web Application — Phase 1 MVP\n\n## 1. Role\nEngineer\n\n## 2. Core Objective\nBuild MVP"

        // Test lone leading B line and lone trailing E line
        val withBoundaryLines = "B\n$samplePrompt\nE"
        val cleanedLines = PromptEngineeringEngine.stripBoundaryMarkers(withBoundaryLines)
        assertEquals(samplePrompt, cleanedLines)

        // Test B with space and trailing E with space
        val withBoundarySpaces = "B $samplePrompt E"
        val cleanedSpaces = PromptEngineeringEngine.stripBoundaryMarkers(withBoundarySpaces)
        assertEquals(samplePrompt, cleanedSpaces)

        // Test B attached directly to #
        val withAttachedB = "B$samplePrompt\nE"
        val cleanedAttached = PromptEngineeringEngine.stripBoundaryMarkers(withAttachedB)
        assertEquals(samplePrompt, cleanedAttached)

        // Test markdown code fence wrapping with B and E
        val withFence = "```markdown\nB\n$samplePrompt\nE\n```"
        val cleanedFence = PromptEngineeringEngine.stripBoundaryMarkers(withFence)
        assertEquals(samplePrompt, cleanedFence)

        // Test quotes wrapping with B and E
        val withQuotes = "\"B\n$samplePrompt\nE\""
        val cleanedQuotes = PromptEngineeringEngine.stripBoundaryMarkers(withQuotes)
        assertEquals(samplePrompt, cleanedQuotes)
    }

    @Test
    fun testEmptyInputGeneratesUniversalPlanWithRoadmap() {
        val prompt = PromptEngineeringEngine.generateStructuredPrompt("")
        assertTrue("Must contain Section 2 Core Objective", prompt.contains("## 2. Core Objective"))
        assertTrue("Must contain Upcoming Phases Roadmap", prompt.contains("Upcoming Phases Roadmap:"))
        assertTrue("Must contain Phase 2", prompt.contains("Phase 2"))
        assertTrue("Must contain Phase 3", prompt.contains("Phase 3"))
        assertTrue("Must contain Next-Phase Trigger Rule", prompt.contains("Next-Phase Trigger Rule:"))
    }
}
