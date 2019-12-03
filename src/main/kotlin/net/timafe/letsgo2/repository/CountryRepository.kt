package net.timafe.letsgo2.repository

import net.timafe.letsgo2.domain.Country
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository

@EnableScan
interface CountryRepository : CrudRepository<Country, String> {
    fun findByName(name: String): List<Country>
    override fun findAll(): List<Country>
}
