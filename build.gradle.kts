// File: build.gradle.kts (Project: project-wmp)

plugins {
    // ... baris-baris plugin yang sudah ada
    alias(libs.plugins.android.application) apply false

    // INI YANG HARUS DITAMBAH: Plugin untuk Google Services
    // Versi 4.4.1 atau 4.4.0 biasanya aman
    id("com.google.gms.google-services") version "4.4.1" apply false
}