package net.timafe.letsgo2.domain

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter
import org.springframework.util.StringUtils
import java.time.Instant

// https://docs.aws.amazon.com/de_de/amazondynamodb/latest/developerguide/DynamoDBMapper.ArbitraryDataMapping.html
class InstantNoodle : DynamoDBTypeConverter<String, Instant?> {

    override fun convert(instant: Instant?): String? {
        return if (instant == null) {
            null
        } else {
            instant.toString()
        }
    }


    override fun unconvert(obj: String): Instant? {
        return if (StringUtils.isEmpty(obj)) {
            null
        } else {
            Instant.parse(obj)
        }
    }


}
