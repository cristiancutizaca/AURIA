package com.luc4n3x.levyra.ui.i18n

import com.luc4n3x.levyra.ui.theme.AuriaVisualMode

internal data class AuriaVisualCopy(
    val heading: String,
    val description: String,
    val hero: String,
    val radio: String,
    private val modes: List<String>
) {
    fun descriptionOf(mode: AuriaVisualMode): String = modes[mode.ordinal]

    companion object {
        fun from(code: String): AuriaVisualCopy = when (code.substringBefore('-').lowercase()) {
            "es" -> AuriaVisualCopy(
                "Tu estilo AURIA", "Cuatro experiencias distintas con la misma música y tus ajustes guardados.",
                "Tu música, tu órbita", "MIX PERSONAL",
                listOf("Inmersivo y fluido", "Espacial y luminoso", "Rápido y compacto", "Mezcla y control")
            )
            "it" -> AuriaVisualCopy(
                "Il tuo stile AURIA", "Quattro esperienze, la stessa musica e le tue preferenze.",
                "La tua musica, la tua orbita", "MIX PERSONALE",
                listOf("Fluido e immersivo", "Spaziale e luminoso", "Rapido e compatto", "Mix e controllo")
            )
            else -> AuriaVisualCopy(
                "Your AURIA style", "Four different experiences, with the same music and saved settings.",
                "Your music, your orbit", "PERSONAL MIX",
                listOf("Fluid and immersive", "Celestial and luminous", "Fast and compact", "Mix and control")
            )
        }
    }
}
