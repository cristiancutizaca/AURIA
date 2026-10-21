package com.luc4n3x.levyra.data.locallibrary

import org.junit.Assert.assertEquals
import org.junit.Test

class LocalGenreLabelsTest {
    @Test
    fun groupsMultiValueGenresWithoutRepeatedLabels() {
        assertEquals(
            listOf("Rock", "Pop", "Jazz"),
            localGenreLabels(" Rock ; Pop | rock, Jazz ")
        )
    }

    @Test
    fun skipsEmptyAndPlaceholderGenres() {
        assertEquals(emptyList<String>(), localGenreLabels(" ; Unknown | Desconocido "))
        assertEquals(listOf("R&B/Soul"), localGenreLabels("R&B/Soul"))
    }
}
