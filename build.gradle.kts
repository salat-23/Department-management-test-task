import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Property
import org.jooq.meta.jaxb.Logging

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.flywaydb.flyway") version "9.8.1"
    id("nu.studer.jooq") version "7.0"
}

group = "org.logiclettuce"
version = "0.1.0"
java.sourceCompatibility = JavaVersion.VERSION_16


configurations {
    all {
        exclude(group = "org.apache.tomcat")
        exclude(module = "spring-boot-starter-tomcat")
    }
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val jooqVersion: String by project
val jjwtVersion: String by project
val springDocVersion: String by project
val pgConnectionString: String by project
val pgUser: String by project
val pgPassword: String by project

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.flywaydb:flyway-core")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    // region jwt
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
    // endregion jwt
    // region Jooq


    jooqGenerator("org.postgresql:postgresql:42.5.4")
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")

    // endregion

    // spring doc
    implementation("org.springdoc:springdoc-openapi-ui:$springDocVersion")
    implementation("org.springdoc:springdoc-openapi-data-rest:$springDocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springDocVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.jar {
    enabled = true
    // Remove `plain` postfix from jar file name
    archiveClassifier.set("")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// region Flyway setup

flyway {
    url = pgConnectionString
    user = pgUser
    password = pgPassword
    schemas = arrayOf("public")
    locations = arrayOf("filesystem:${project.projectDir}/src/main/resources/db/migration")
}

// endregion

// region Jooq setup

tasks.withType<JooqGenerate> {
    dependsOn("flywayMigrate")
}

jooq {
    version.set(jooqVersion)  // default (can be omitted)
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)

    configurations {
        create("main") {  // name of the jOOQ configuration
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)

            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = pgConnectionString
                    user = pgUser
                    password = pgPassword
                    properties.add(Property().apply {
                        key = "ssl"
                        value = "false"
                    })
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        forcedTypes.addAll(listOf(
                            ForcedType().apply {
                                name = "varchar"
                                includeExpression = ".*"
                                includeTypes = "JSONB?"
                            },
                            ForcedType().apply {
                                name = "varchar"
                                includeExpression = ".*"
                                includeTypes = "INET"
                            }
                        ))
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "${group}.database"
                        directory = "build/generated-src/jooq/main"  // default (can be omitted)
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

// endregion
