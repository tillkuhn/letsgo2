package net.timafe.letsgo2.config

import io.github.jhipster.config.JHipsterConstants
import io.github.jhipster.config.h2.H2ConfigurationHelper
import java.sql.SQLException
import net.timafe.letsgo2.repository.CountryRepository
import net.timafe.letsgo2.repository.JPABasePackage
import net.timafe.letsgo2.repository.RegionRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.*
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
// See https://github.com/derjust/spring-data-dynamodb/wiki/Multi-Repository-configuration
// https://discuss.kotlinlang.org/t/kotlin-with-spring-in-trouble-error-18-65-kotlin-expecting-an-element/1434
@EnableJpaRepositories(basePackageClasses = [JPABasePackage::class],
    excludeFilters = [ComponentScan.Filter(value = [CountryRepository::class], type = FilterType.ASSIGNABLE_TYPE),
    ComponentScan.Filter(value = [RegionRepository::class], type = FilterType.ASSIGNABLE_TYPE)])
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
class DatabaseConfiguration(private val env: Environment) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Open the TCP port for the H2 database, so it is available remotely.
     *
     * @return the H2 database TCP server.
     * @throws SQLException if the server failed to start.
     */
    @Throws(SQLException::class)
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
    fun h2TCPServer(): Any {
        val port = getValidPortForH2()
        log.debug("H2 database is available on port {}", port)
        return H2ConfigurationHelper.createServer(port)
    }

    private fun getValidPortForH2(): String {
        var port = Integer.parseInt(env.getProperty("server.port"))
        if (port < 10000) {
            port += 10000
        } else {
            if (port < 63536) {
                port += 2000
            } else {
                port -= 2000
            }
        }
        return port.toString()
    }
}
