package fr.enssat.babelblock.jourdren_duchene.services.translation

import com.google.mlkit.nl.translate.TranslateLanguage
import java.util.*

class Language {
    companion object {
        // generate the list of locale (from the list of MLKit translator available language)
        fun generateMLKitAvailableLocales(): MutableList<Locale> {
            var locales = mutableListOf<Locale>()
            for(tlSearching in TranslateLanguage.getAllLanguages()) {
                for(localeInComp in Locale.getAvailableLocales()) {
                    if(localeInComp.language == tlSearching) {
                        locales.add(localeInComp)
                        break
                    }
                }
            }

            return locales
        }
    }
}