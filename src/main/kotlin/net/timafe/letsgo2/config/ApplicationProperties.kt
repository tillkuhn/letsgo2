package net.timafe.letsgo2.config

import io.github.jhipster.config.JHipsterDefaults
import io.github.jhipster.config.JHipsterProperties
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Properties specific to Letsgo 2.
 *
 * Properties are configured in the `application.yml` file.
 * See [io.github.jhipster.config.JHipsterProperties] for a good example.
 *
 * and https://www.jhipster.tech/common-application-properties/#2
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
class ApplicationProperties {

    val aws = ApplicationProperties.Aws()

    class Aws {
        val dynamodb = ApplicationProperties.Aws.Dynamodb()
        class Dynamodb {
            var tablePrefix = "hase-"
        }
    }
}
