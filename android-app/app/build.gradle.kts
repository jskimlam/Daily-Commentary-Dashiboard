plugins {
    id("com.android.application")
}

val generatedIconResDir = layout.buildDirectory.dir("generated/lamIconRes")

val decodeLamIcon by tasks.registering {
    val source = file("src/main/res/raw/lam_daily_icon_base64.txt")
    inputs.file(source)
    outputs.dir(generatedIconResDir)

    doLast {
        val encoded = source.readText().trim()
        val bytes = java.util.Base64.getDecoder().decode(encoded)
        val root = generatedIconResDir.get().asFile
        listOf(
            "mipmap-mdpi",
            "mipmap-hdpi",
            "mipmap-xhdpi",
            "mipmap-xxhdpi",
            "mipmap-xxxhdpi"
        ).forEach { density ->
            val dir = java.io.File(root, density)
            dir.mkdirs()
            java.io.File(dir, "ic_launcher.jpg").writeBytes(bytes)
        }
    }
}

android {
    namespace = "com.lam.daily"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lam.daily"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    sourceSets.getByName("main").res.srcDir(generatedIconResDir)

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.named("preBuild").configure {
    dependsOn(decodeLamIcon)
}
