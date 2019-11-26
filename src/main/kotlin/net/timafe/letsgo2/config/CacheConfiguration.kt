package net.timafe.letsgo2.config

import io.github.jhipster.config.JHipsterProperties
import java.time.Duration
import net.timafe.letsgo2.repository.UserRepository
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.jsr107.Eh107Configuration
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfiguration(jHipsterProperties: JHipsterProperties) {

    private val jcacheConfiguration: javax.cache.configuration.Configuration<Any, Any>

    init {
        val ehcache = jHipsterProperties.cache.ehcache

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Any::class.java, Any::class.java,
                ResourcePoolsBuilder.heap(ehcache.maxEntries)
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.timeToLiveSeconds.toLong())))
                .build()
        )
    }

    @Bean
    fun cacheManagerCustomizer(): JCacheManagerCustomizer {
        return JCacheManagerCustomizer { cm ->
            createCache(cm, UserRepository.USERS_BY_LOGIN_CACHE)
            createCache(cm, UserRepository.USERS_BY_EMAIL_CACHE)
            createCache(cm, net.timafe.letsgo2.domain.User::class.java.name)
            createCache(cm, net.timafe.letsgo2.domain.Authority::class.java.name)
            createCache(cm, net.timafe.letsgo2.domain.User::class.java.name + ".authorities")
            createCache(cm, net.timafe.letsgo2.domain.Place::class.java.name)
            createCache(cm, net.timafe.letsgo2.domain.Region::class.java.name)
            // jhipster-needle-ehcache-add-entry
        }
    }

    private fun createCache(cm: javax.cache.CacheManager, cacheName: String) {
        val cache: javax.cache.Cache<Any, Any>? = cm.getCache(cacheName)
        if (cache != null) {
            cm.destroyCache(cacheName)
        }
        cm.createCache(cacheName, jcacheConfiguration)
    }
}
