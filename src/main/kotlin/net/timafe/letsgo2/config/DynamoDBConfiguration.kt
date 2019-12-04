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
import io.github.jhipster.config.JHipsterConstants
import kotlin.reflect.KClass
import net.timafe.letsgo2.domain.Country
import net.timafe.letsgo2.domain.Place
import net.timafe.letsgo2.domain.Region
import net.timafe.letsgo2.repository.CountryRepository
import net.timafe.letsgo2.repository.PlaceRepository
import net.timafe.letsgo2.repository.RegionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.*
import org.springframework.core.env.Profiles
import org.springframework.core.env.Environment

@Configuration
// see https://github.com/derjust/spring-data-dynamodb-examples/blob/master/README-multirepo.md
// Table name overwrite? https://github.com/derjust/spring-data-dynamodb/wiki/Alter-table-name-during-runtime
@EnableDynamoDBRepositories(basePackageClasses = [CountryRepository::class],
    includeFilters = [ComponentScan.Filter(value = [EnableScan::class], type = FilterType.ANNOTATION)])
class DynamoDBConfiguration(private val env: Environment) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Profile("!localstack")
    @Bean("amazonDynamoDB")
    fun dynamoDb(): AmazonDynamoDB {
        val client = AmazonDynamoDBClientBuilder.defaultClient()
        createTableForEntity(client, Country::class)
        return client
    }

    @Profile("localstack")
    @Bean("amazonDynamoDB")
    // http://localhost:8000/
    fun dynamoDbLocal(@Value("\${aws.dynamodb.endpoint:http://localhost:8000}") amazonDynamoDBEndpoint: String): AmazonDynamoDB {
        val client = AmazonDynamoDBClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("key", "secret")))
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, Regions.EU_CENTRAL_1.toString()))
            .build()
        // todo remove should be done by terraform
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
            log.info("Profile {} detected, creating tables in Dynamodb",(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))
            createTableForEntity(client, Country::class)
            createTableForEntity(client, Region::class)
            createTableForEntity(client, Place::class)
        }
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
