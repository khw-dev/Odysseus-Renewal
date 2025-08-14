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
    // 기본 캐릭터들 (실제 이미지로 업데이트)
    val availableCharacters = listOf(
        PetCharacter(
            id = "cat_orange",
            name = "주황이",
            description = "활발하고 장난기 많은 주황 고양이",
            imageRes = R.drawable.ic_pet_cat
        ),
        PetCharacter(
            id = "cat_black",
            name = "까망이",
            description = "신비롭고 우아한 검은 고양이",
            imageRes = R.drawable.ic_pet_cat
        ),
        PetCharacter(
            id = "dog_golden",
            name = "골댕이",
            description = "충성스럽고 친근한 골든 리트리버",
            imageRes = R.drawable.ic_pet_dog
        ),
        PetCharacter(
            id = "rabbit_white",
            name = "하양이",
            description = "귀엽고 온순한 흰 토끼",
            imageRes = R.drawable.ic_pet_cat // 토끼 이미지가 없으므로 고양이 이미지 사용
        ),
        PetCharacter(
            id = "hamster_brown",
            name = "브라운",
            description = "작고 애교 많은 갈색 햄스터",
            imageRes = R.drawable.ic_pet_cat // 햄스터 이미지가 없으므로 고양이 이미지 사용
        )
    )
}
