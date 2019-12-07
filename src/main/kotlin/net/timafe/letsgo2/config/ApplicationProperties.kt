package net.timafe.letsgo2.config

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
        val s3 = ApplicationProperties.Aws.S3()
        class Dynamodb {
            var tablePrefix = ""
        }
        class S3 {
            var bucketName = ""
        }
    }
}
