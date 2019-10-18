package com.johnriggsdev.inspiringapps

class TestData {
    companion object {
        private const val TEST_KEY_0 = "testKey0"
        private const val TEST_KEY_1 = "testKey0"
        private const val TEST_KEY_2 = "testKey0"
        private const val TEST_KEY_3 = "testKey0"

        private val TEST_MAP = mutableMapOf(
            TEST_KEY_0 to 5,
            TEST_KEY_1 to 4,
            TEST_KEY_2 to 3,
            TEST_KEY_3 to 2
        )

        val TEST_SEQUENCES_LIST = TEST_MAP.entries.toList()

        const val SOME_ERROR_STRING = "Some error"
        const val EMPTY_STRING = ""
        const val TEST_PROGRESS_10 = "10"
        const val TEST_PROGRESS_100 = "100"
    }
}