package com.example.birthdaylist.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Period

/**
 * Data Transfer Object (DTO) der repræsenterer en Person i REST API'et.
 * Denne klasse er en direkte spejling af de data, som lærerens server sender og modtager (JSON format).
 */
data class PersonDto(

    // Unikt ID for personen. 
    // Feltet er 'nullable' (Int?), fordi når vi OPRETTER en person, kender vi ikke ID'et endnu.
    // Det er serveren, der tildeler ID'et og sender det tilbage til os.
    val id: Int? = null,

    // ID på den bruger (dig), som denne person "tilhører".
    // I denne app bruger vi dit Firebase UserID. Det sikrer, at du kun ser dine egne venners fødselsdage.
    val userId: String,

    // Navnet på din ven. Påkrævet felt.
    val name: String,

    // Fødselsår (f.eks. 1995).
    val birthYear: Int,

    // Fødselsmåned (1-12).
    val birthMonth: Int,

    // Dag i måneden (1-31).
    val birthDayOfMonth: Int,

    // En valgfri bemærkning eller note om personen (kan være null).
    val remarks: String? = null,

    // URL til et billede. Ikke et krav i opgaven, men API'et understøtter det.
    val pictureUrl: String? = null,

    // Alder beregnes automatisk af serveren (Backend).
    // Vi sender ALDRIG dette felt til serveren; vi læser det kun (Read-only).
    val age: Int? = null
) {
    /**
     * En "computed property" (beregnet værdi), der finder ud af, hvor mange dage
     * der er til personens næste fødselsdag.
     */
    val daysUntilBirthday: Int
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val today = LocalDate.now()
            
            // Vi opretter en dato for fødselsdagen i DETTE år.
            var nextBirthday = LocalDate.of(
                today.year,
                birthMonth,
                birthDayOfMonth
            )

            // Hvis fødselsdagen i år ALLEREDE er passeret, kigger vi på næste år i stedet.
            if (nextBirthday.isBefore(today)) {
                nextBirthday = nextBirthday.plusYears(1)
            }

            // Period.between finder forskellen mellem to datoer.
            return Period.between(today, nextBirthday).days
        }

    /**
     * En simpel hjælpe-funktion, der samler år, måned og dag til en pæn tekststreng.
     */
    val birthday: String
        get() = "$birthDayOfMonth/$birthMonth/$birthYear"
}
