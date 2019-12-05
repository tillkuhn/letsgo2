package net.timafe.letsgo2.repository
import net.timafe.letsgo2.domain.Place
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.socialsignin.spring.data.dynamodb.repository.Query
import org.springframework.data.repository.CrudRepository


/**
 * Spring Data  repository for the [Place] entity.
 */
//@Repository
@EnableScan
interface CoordinatesRepository : CrudRepository<Place, String> {

    // https://github.com/derjust/spring-data-dynamodb/wiki/Projections
   //  @Query(fields = "leaveDate")
    //  open fun findByPostCode(postCode: String?): MutableList<User?>?
    // A string that identifies the attributes you want. To retrieve a single attribute, specify its name. For multiple attributes, the names must be comma-separated.
    // https://stackoverflow.com/questions/32957690/projection-expression-with-dynamodbmapper
    @Query(fields = "id,coordinates")
    override fun findAll(): List<Place>
}
