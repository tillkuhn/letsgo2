package net.timafe.letsgo2.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException
import kotlin.reflect.KClass
import net.timafe.letsgo2.domain.Country
import net.timafe.letsgo2.dynamo.CountryRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.*

@Configuration
// see https://github.com/derjust/spring-data-dynamodb-examples/blob/master/README-multirepo.md
@EnableDynamoDBRepositories(basePackageClasses = [CountryRepository::class])
class DynamoDBConfiguration {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Profile("!dev")
    @Bean("amazonDynamoDB")
    fun dynamoDb(): AmazonDynamoDB {
        val client = AmazonDynamoDBClientBuilder.defaultClient()
        createTableForEntity(client, Country::class)
        return client
    }

    @Profile("dev")
    @Bean("amazonDynamoDB")
    // http://localhost:8000/
    fun dynamoDbLocal(@Value("\${aws.dynamodb.endpoint:http://localhost:8000}") amazonDynamoDBEndpoint: String): AmazonDynamoDB {
        val client = AmazonDynamoDBClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("key", "secret")))
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, Regions.EU_CENTRAL_1.toString()))
            .build()
        createTableForEntity(client, Country::class)
        return client
    }

    private fun createTableForEntity(amazonDynamoDB: AmazonDynamoDB, entity: KClass<*>) {

        val tableRequest = DynamoDBMapper(amazonDynamoDB)
            .generateCreateTableRequest(entity.java)
            .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))

        try {
            com.amazonaws.services.dynamodbv2.document.DynamoDB(amazonDynamoDB).createTable(tableRequest).waitForActive()
            log.info("Table created! [entity={}]", entity)
        } catch (e: ResourceInUseException) {
            log.info("Table already exists - skip creation! [entity={}]", entity)
        }
    }
}
