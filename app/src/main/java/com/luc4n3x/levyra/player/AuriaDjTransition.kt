package com.luc4n3x.levyra.player

data class AuriaDjTransition(
    val sourceId: String,
    val targetId: String,
    val sourceArtwork: String,
    val targetArtwork: String,
    val sourceTitle: String,
    val targetTitle: String,
    val targetArtist: String,
    val durationMs: Long
)
