package net.timafe.letsgo2.web.rest

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import net.timafe.letsgo2.domain.Coordinates
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class MapController(    private val amazonDynamoDB: AmazonDynamoDB) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/public/coordinates")
    fun getCoordinates(): List<Coordinates> {
        log.debug("REST request to get all coordinates")
        val list = mutableListOf<Coordinates>()
        val table = com.amazonaws.services.dynamodbv2.document.DynamoDB(amazonDynamoDB).getTable("letsgo2-place")
        val items = table.scan(null,"id,coordinates",null,null)
        val iterator: Iterator<Item> = items.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next();
            list.add(Coordinates(item.get("id").toString(),item.get("coordinates")))
            log.debug(item.toJSONPretty())
        }
        return list
    }
}
