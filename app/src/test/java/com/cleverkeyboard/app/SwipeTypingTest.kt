package com.cleverkeyboard.app

import com.cleverkeyboard.app.autocorrect.AutoCorrectManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SwipeTypingTest {

    @Before
    fun setup() {
        AutoCorrectManager.setTestWordsList(
            listOf(
                "hello",
                "help",
                "world",
                "word",
                "computer",
                "quick",
                "brown",
                "fox",
                "keyboard",
                "clever"
            )
        )
    }

    @Test
    fun testSwipeHelloMatchesHello() {
        val trajectory = listOf('h', 'e', 'l', 'o')
        val candidates = AutoCorrectManager.matchSwipeTrajectory(null, trajectory)
        assertTrue(candidates.contains("hello"))
        assertEquals("hello", candidates.first())
    }

    @Test
    fun testSwipeWorldMatchesWorld() {
        val trajectory = listOf('w', 'o', 'r', 'l', 'd')
        val candidates = AutoCorrectManager.matchSwipeTrajectory(null, trajectory)
        assertTrue(candidates.contains("world"))
    }

    @Test
    fun testSwipeCleverMatchesClever() {
        val trajectory = listOf('c', 'l', 'e', 'v', 'e', 'r')
        val candidates = AutoCorrectManager.matchSwipeTrajectory(null, trajectory)
        assertTrue(candidates.contains("clever"))
    }

    @Test
    fun testShortTrajectoryReturnsEmpty() {
        val trajectory = listOf('a')
        val candidates = AutoCorrectManager.matchSwipeTrajectory(null, trajectory)
        assertTrue(candidates.isEmpty())
    }
}
