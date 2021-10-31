package com.example.nasaapplication.repository

// Класс с константами для Repository
class ConstantsRepository {
    companion object {
        @JvmField
        val POD_BASE_URL: String = "https://api.nasa.gov/"
        @JvmField
        val NASA_ARCHIVE_BASE_URL: String = "https://images-api.nasa.gov/"
    }
}