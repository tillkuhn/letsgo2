package net.timafe.letsgo2.repository
import net.timafe.letsgo2.domain.Place
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository


/**
 * Spring Data  repository for the [Place] entity.
 */
//@Repository
@EnableScan
interface PlaceRepository : CrudRepository<Place, String> {

}
