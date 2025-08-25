package com.example.ppet.model

import androidx.annotation.DrawableRes
import com.example.ppet.R

data class PetCharacter(
    val id: String,
    val name: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    val rarity: PetRarity = PetRarity.COMMON
)

enum class PetRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

object PetCharacters {
    
    val availableCharacters = listOf(
        PetCharacter(
            id = "cat_orange",
            name = "주황이",
            description = "활발하고 장난기 많은 주황 고양이",
            imageRes = R.drawable.ic_pet_cat
        ),
        PetCharacter(
            id = "dog_golden",
            name = "골댕이",
            description = "충성스럽고 친근한 골든 리트리버",
            imageRes = R.drawable.ic_pet_dog
        ),
        PetCharacter(
            id = "turtle",
            name = "거북이",
            description = "느긋하고 똑똑한 거북이",
            imageRes = R.drawable.ic_pet_turtle
        ),
        PetCharacter(
            id = "hamster_brown",
            name = "브라운",
            description = "작고 애교 많은 갈색 햄스터",
            imageRes = R.drawable.ic_pet_hamster
        )
    )
}
