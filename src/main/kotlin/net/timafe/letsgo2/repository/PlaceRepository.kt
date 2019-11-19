package net.timafe.letsgo2.repository
import net.timafe.letsgo2.domain.Place
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Place] entity.
 */
@Suppress("unused")
@Repository
interface PlaceRepository : JpaRepository<Place, Long>
