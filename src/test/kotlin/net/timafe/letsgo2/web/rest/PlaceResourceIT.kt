package net.timafe.letsgo2.web.rest

import net.timafe.letsgo2.Letsgo2App
import net.timafe.letsgo2.config.TestSecurityConfiguration
import net.timafe.letsgo2.domain.Place
import net.timafe.letsgo2.repository.PlaceRepository
import net.timafe.letsgo2.web.rest.errors.ExceptionTranslator

import kotlin.test.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import javax.persistence.EntityManager
import java.time.Instant
import java.time.temporal.ChronoUnit

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


/**
 * Integration tests for the [PlaceResource] REST controller.
 *
 * @see PlaceResource
 */
@SpringBootTest(classes = [Letsgo2App::class, TestSecurityConfiguration::class])
class PlaceResourceIT {

    @Autowired
    private lateinit var placeRepository: PlaceRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restPlaceMockMvc: MockMvc

    private lateinit var place: Place

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val placeResource = PlaceResource(placeRepository)
        this.restPlaceMockMvc = MockMvcBuilders.standaloneSetup(placeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        place = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createPlace() {
        val databaseSizeBeforeCreate = placeRepository.findAll().size

        // Create the Place
        restPlaceMockMvc.perform(
            post("/api/places")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(place))
        ).andExpect(status().isCreated)

        // Validate the Place in the database
        val placeList = placeRepository.findAll()
        assertThat(placeList).hasSize(databaseSizeBeforeCreate + 1)
        val testPlace = placeList[placeList.size - 1]
        assertThat(testPlace.name).isEqualTo(DEFAULT_NAME)
        assertThat(testPlace.summary).isEqualTo(DEFAULT_SUMMARY)
        assertThat(testPlace.imageUrl).isEqualTo(DEFAULT_IMAGE_URL)
        assertThat(testPlace.rating).isEqualTo(DEFAULT_RATING)
        assertThat(testPlace.lotype).isEqualTo(DEFAULT_LOTYPE)
        assertThat(testPlace.country).isEqualTo(DEFAULT_COUNTRY)
        assertThat(testPlace.updatedBy).isEqualTo(DEFAULT_UPDATED_BY)
        assertThat(testPlace.coordinates).isEqualTo(DEFAULT_COORDINATES)
        assertThat(testPlace.notes).isEqualTo(DEFAULT_NOTES)
        assertThat(testPlace.updatedAt).isEqualTo(DEFAULT_UPDATED_AT)
        assertThat(testPlace.primaryUrl).isEqualTo(DEFAULT_PRIMARY_URL)
    }

    @Test
    @Transactional
    fun createPlaceWithExistingId() {
        val databaseSizeBeforeCreate = placeRepository.findAll().size

        // Create the Place with an existing ID
        place.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaceMockMvc.perform(
            post("/api/places")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(place))
        ).andExpect(status().isBadRequest)

        // Validate the Place in the database
        val placeList = placeRepository.findAll()
        assertThat(placeList).hasSize(databaseSizeBeforeCreate)
    }


    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = placeRepository.findAll().size
        // set the field null
        place.name = null

        // Create the Place, which fails.

        restPlaceMockMvc.perform(
            post("/api/places")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(place))
        ).andExpect(status().isBadRequest)

        val placeList = placeRepository.findAll()
        assertThat(placeList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllPlaces() {
        // Initialize the database
        placeRepository.saveAndFlush(place)

        // Get all the placeList
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].lotype").value(hasItem(DEFAULT_LOTYPE)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].coordinates").value(hasItem(DEFAULT_COORDINATES)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].primaryUrl").value(hasItem(DEFAULT_PRIMARY_URL)))
    }
    
    @Test
    @Transactional
    fun getPlace() {
        // Initialize the database
        placeRepository.saveAndFlush(place)

        val id = place.id
        assertNotNull(id)

        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.lotype").value(DEFAULT_LOTYPE))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.coordinates").value(DEFAULT_COORDINATES))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.primaryUrl").value(DEFAULT_PRIMARY_URL))
    }

    @Test
    @Transactional
    fun getNonExistingPlace() {
        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updatePlace() {
        // Initialize the database
        placeRepository.saveAndFlush(place)

        val databaseSizeBeforeUpdate = placeRepository.findAll().size

        // Update the place
        val id = place.id
        assertNotNull(id)
        val updatedPlace = placeRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedPlace are not directly saved in db
        em.detach(updatedPlace)
        updatedPlace.name = UPDATED_NAME
        updatedPlace.summary = UPDATED_SUMMARY
        updatedPlace.imageUrl = UPDATED_IMAGE_URL
        updatedPlace.rating = UPDATED_RATING
        updatedPlace.lotype = UPDATED_LOTYPE
        updatedPlace.country = UPDATED_COUNTRY
        updatedPlace.updatedBy = UPDATED_UPDATED_BY
        updatedPlace.coordinates = UPDATED_COORDINATES
        updatedPlace.notes = UPDATED_NOTES
        updatedPlace.updatedAt = UPDATED_UPDATED_AT
        updatedPlace.primaryUrl = UPDATED_PRIMARY_URL

        restPlaceMockMvc.perform(
            put("/api/places")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedPlace))
        ).andExpect(status().isOk)

        // Validate the Place in the database
        val placeList = placeRepository.findAll()
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate)
        val testPlace = placeList[placeList.size - 1]
        assertThat(testPlace.name).isEqualTo(UPDATED_NAME)
        assertThat(testPlace.summary).isEqualTo(UPDATED_SUMMARY)
        assertThat(testPlace.imageUrl).isEqualTo(UPDATED_IMAGE_URL)
        assertThat(testPlace.rating).isEqualTo(UPDATED_RATING)
        assertThat(testPlace.lotype).isEqualTo(UPDATED_LOTYPE)
        assertThat(testPlace.country).isEqualTo(UPDATED_COUNTRY)
        assertThat(testPlace.updatedBy).isEqualTo(UPDATED_UPDATED_BY)
        assertThat(testPlace.coordinates).isEqualTo(UPDATED_COORDINATES)
        assertThat(testPlace.notes).isEqualTo(UPDATED_NOTES)
        assertThat(testPlace.updatedAt).isEqualTo(UPDATED_UPDATED_AT)
        assertThat(testPlace.primaryUrl).isEqualTo(UPDATED_PRIMARY_URL)
    }

    @Test
    @Transactional
    fun updateNonExistingPlace() {
        val databaseSizeBeforeUpdate = placeRepository.findAll().size

        // Create the Place

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceMockMvc.perform(
            put("/api/places")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(place))
        ).andExpect(status().isBadRequest)

        // Validate the Place in the database
        val placeList = placeRepository.findAll()
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deletePlace() {
        // Initialize the database
        placeRepository.saveAndFlush(place)

        val databaseSizeBeforeDelete = placeRepository.findAll().size

        val id = place.id
        assertNotNull(id)

        // Delete the place
        restPlaceMockMvc.perform(
            delete("/api/places/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val placeList = placeRepository.findAll()
        assertThat(placeList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Place::class)
        val place1 = Place()
        place1.id = 1L
        val place2 = Place()
        place2.id = place1.id
        assertThat(place1).isEqualTo(place2)
        place2.id = 2L
        assertThat(place1).isNotEqualTo(place2)
        place1.id = null
        assertThat(place1).isNotEqualTo(place2)
    }

    companion object {

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_SUMMARY = "BBBBBBBBBB"

        private const val DEFAULT_IMAGE_URL: String = "AAAAAAAAAA"
        private const val UPDATED_IMAGE_URL = "BBBBBBBBBB"

        private const val DEFAULT_RATING: Int = 1
        private const val UPDATED_RATING: Int = 2

        private const val DEFAULT_LOTYPE: String = "AAAAAAAAAA"
        private const val UPDATED_LOTYPE = "BBBBBBBBBB"

        private const val DEFAULT_COUNTRY: String = "AAAAAAAAAA"
        private const val UPDATED_COUNTRY = "BBBBBBBBBB"

        private const val DEFAULT_UPDATED_BY: String = "AAAAAAAAAA"
        private const val UPDATED_UPDATED_BY = "BBBBBBBBBB"

        private const val DEFAULT_COORDINATES: String = "AAAAAAAAAA"
        private const val UPDATED_COORDINATES = "BBBBBBBBBB"

        private const val DEFAULT_NOTES: String = "AAAAAAAAAA"
        private const val UPDATED_NOTES = "BBBBBBBBBB"

        private val DEFAULT_UPDATED_AT: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_UPDATED_AT: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_PRIMARY_URL: String = "AAAAAAAAAA"
        private const val UPDATED_PRIMARY_URL = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Place {
            val place = Place(
                name = DEFAULT_NAME,
                summary = DEFAULT_SUMMARY,
                imageUrl = DEFAULT_IMAGE_URL,
                rating = DEFAULT_RATING,
                lotype = DEFAULT_LOTYPE,
                country = DEFAULT_COUNTRY,
                updatedBy = DEFAULT_UPDATED_BY,
                coordinates = DEFAULT_COORDINATES,
                notes = DEFAULT_NOTES,
                updatedAt = DEFAULT_UPDATED_AT,
                primaryUrl = DEFAULT_PRIMARY_URL
            )

            return place
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Place {
            val place = Place(
                name = UPDATED_NAME,
                summary = UPDATED_SUMMARY,
                imageUrl = UPDATED_IMAGE_URL,
                rating = UPDATED_RATING,
                lotype = UPDATED_LOTYPE,
                country = UPDATED_COUNTRY,
                updatedBy = UPDATED_UPDATED_BY,
                coordinates = UPDATED_COORDINATES,
                notes = UPDATED_NOTES,
                updatedAt = UPDATED_UPDATED_AT,
                primaryUrl = UPDATED_PRIMARY_URL
            )

            return place
        }
    }
}
