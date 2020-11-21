package com.relapps.everythingyouneed.models.randomAnimalPictures

import java.io.Serializable

data class RequestKittensResponse (
    val breeds: List<Breeds>,
    val id: String,
    val url: String,
    val width: Int,
    val height: Int


): Serializable



data class Breeds(
    val id: String,
    val name: String,
    val temperament: String,
    val life_span: String,
    val alt_names: String,
    val wikipedia_url: String,
    val origin: String,
    val weight_imperial: String,
    val experimental: Int,
    val hairless: Int,
    val natural: Int,
    val rare: Int,
    val rex: Int,
    val suppress_tail: Int,
    val short_legs: Int,
    val hypoallergenic: Int,
    val adaptability: Int,
    val affection_level: Int,
    val country_code: String,
    val child_friendly: Int,
    val dog_friendly: Int,
    val energy_level: Int,
    val grooming: Int,
    val health_issues: Int,
    val intelligence: Int,
    val shedding_level: Int,
    val social_needs: Int,
    val stranger_friendly: Int,
    val vocalisation: Int,
): Serializable

//data class Categories (
//    val id: Long,
//    val name: String,
//): Serializable