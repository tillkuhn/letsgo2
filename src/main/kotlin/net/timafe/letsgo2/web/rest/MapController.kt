package net.timafe.letsgo2.web.rest

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import net.timafe.letsgo2.domain.Coordinates
import net.timafe.letsgo2.domain.Place
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/api")
class MapController(    private val amazonDynamoDB: AmazonDynamoDB) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/coordinates")
    fun getCoordinates(): List<Coordinates> {
        log.debug("REST request to get all coordinates")
        val list = mutableListOf<Coordinates>()

        val mapper =  DynamoDBMapper(amazonDynamoDB)
        val nameMap = HashMap<String, String>()
        nameMap["#name"] = "name" // reserved keyword
        val scanExpression =  DynamoDBScanExpression().withExpressionAttributeNames(nameMap).withProjectionExpression("id,coordinates,country,#name")
        val iList = mapper.scan(Place::class.java,scanExpression)
        val iter = iList.iterator()
        while (iter.hasNext()) {
            val item = iter.next();
            list.add(Coordinates(item.id,item.name,item.coordinates))
            //log.debug("hase"+item)
        }
        return list
    }
}
