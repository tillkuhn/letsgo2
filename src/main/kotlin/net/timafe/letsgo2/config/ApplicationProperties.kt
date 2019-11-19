package net.timafe.letsgo2.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Properties specific to Letsgo 2.
 *
 * Properties are configured in the `application.yml` file.
 * See [io.github.jhipster.config.JHipsterProperties] for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
class ApplicationProperties
