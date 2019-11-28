package net.timafe.letsgo2.repository
import net.timafe.letsgo2.domain.Region
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository

/**
 * Spring Data  repository for the [Region] entity.
 */
// @Suppress("unused")
// @Repository
@EnableScan
interface RegionRepository : CrudRepository<Region, String>
