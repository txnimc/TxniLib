import toni.blahaj.*
import toni.blahaj.api.*

val templateSettings = object : BlahajSettings {
	// -------------------- Dependencies ---------------------- //
	override val depsHandler: BlahajDependencyHandler get() = object : BlahajDependencyHandler {
		override fun addGlobal(mod : ModData, deps: DependencyHandler) {

		}

		override fun addFabric(mod : ModData, deps: DependencyHandler) {

		}

		override fun addForge(mod : ModData, deps: DependencyHandler) {
			deps.include(deps.modApi("dev.su5ed.sinytra:fabric-loader:2.7.4+0.15.3+1.20.1")!!)
			deps.include(deps.modApi("dev.su5ed.sinytra.fabric-api:fabric-api-base:+")!!)
			deps.include(deps.modApi("dev.su5ed.sinytra.fabric-api:fabric-rendering-v1:+")!!)
			deps.include(deps.modApi("dev.su5ed.sinytra.fabric-api:fabric-events-interaction-v0:+")!!)
			deps.include(deps.modApi("dev.su5ed.sinytra.fabric-api:fabric-networking-api-v1:+")!!)
			deps.include(deps.modApi("dev.su5ed.sinytra.fabric-api:fabric-command-api-v2:2.2.13+561530ec77")!!)
		}

		override fun addNeo(mod : ModData, deps: DependencyHandler) {
			deps.include(deps.modApi("org.sinytra:forgified-fabric-loader:2.5.29+0.16.0+1.21")!!)
			deps.include(deps.modApi("org.sinytra.forgified-fabric-api:fabric-api-base:0.4.42+d1308ded19")!!)
			deps.include(deps.modApi("org.sinytra.forgified-fabric-api:fabric-networking-api-v1:4.2.2+a6c6b14f19")!!)
			deps.include(deps.modApi("org.sinytra.forgified-fabric-api:fabric-rendering-v1:5.0.5+2df007aa19")!!)
			deps.include(deps.modApi("org.sinytra.forgified-fabric-api:fabric-events-interaction-v0:0.7.12+7b71cc1619")!!)
			deps.include(deps.modApi("org.sinytra.forgified-fabric-api:fabric-command-api-v2:2.2.28+36d727be19")!!)
		}
	}

	// ---------- Curseforge/Modrinth Configuration ----------- //
	// For configuring the dependecies that will show up on your mod page.
	override val publishHandler: BlahajPublishDependencyHandler get() = object : BlahajPublishDependencyHandler {
		override fun addShared(mod : ModData, deps: DependencyContainer) {
			if (mod.isFabric) {
				deps.requires("fabric-api")
			}
		}

		override fun addCurseForge(mod : ModData, deps: DependencyContainer) {

		}

		override fun addModrinth(mod : ModData, deps: DependencyContainer) {

		}
	}
}

plugins {
	`maven-publish`
	application
	id("toni.blahaj") version "1.0.13"
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("dev.kikugie.j52j") version "1.0"
	id("dev.architectury.loom")
	id("me.modmuss50.mod-publish-plugin")
	id("systems.manifold.manifold-gradle-plugin")
}

blahaj {
	sc = stonecutter
	settings = templateSettings
	init()
}

// Dependencies
repositories {
	maven("https://www.cursemaven.com")
	maven("https://api.modrinth.com/maven")
	maven("https://thedarkcolour.github.io/KotlinForForge/")
	maven("https://maven.kikugie.dev/releases")
	maven("https://maven.txni.dev/releases")
	maven("https://jitpack.io")
	maven("https://maven.neoforged.net/releases/")
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
	maven("https://maven.parchmentmc.org")
	maven("https://maven.su5ed.dev/releases")
	maven("https://maven.su5ed.dev/releases")
	maven("https://maven.fabricmc.net")
	maven("https://maven.shedaniel.me/")
}