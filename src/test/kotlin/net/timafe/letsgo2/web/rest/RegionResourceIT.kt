package net.timafe.letsgo2.web.rest

import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import net.timafe.letsgo2.Letsgo2App
import net.timafe.letsgo2.config.TestSecurityConfiguration
import net.timafe.letsgo2.domain.Region
import net.timafe.letsgo2.repository.RegionRepository
import net.timafe.letsgo2.web.rest.errors.ExceptionTranslator
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

/**
 * Integration tests for the [RegionResource] REST controller.
 *
 * @see RegionResource
 */
@SpringBootTest(classes = [Letsgo2App::class, TestSecurityConfiguration::class])
class RegionResourceIT {

    @Autowired
    private lateinit var regionRepository: RegionRepository

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

    private lateinit var restRegionMockMvc: MockMvc

    private lateinit var region: Region

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val regionResource = RegionResource(regionRepository)
        this.restRegionMockMvc = MockMvcBuilders.standaloneSetup(regionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        region = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createRegion() {
        val databaseSizeBeforeCreate = regionRepository.findAll().size

        // Create the Region
        restRegionMockMvc.perform(
            post("/api/regions")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(region))
        ).andExpect(status().isCreated)

        // Validate the Region in the database
        val regionList = regionRepository.findAll()
        assertThat(regionList).hasSize(databaseSizeBeforeCreate + 1)
        val testRegion = regionList[regionList.size - 1]
        assertThat(testRegion.code).isEqualTo(DEFAULT_CODE)
        assertThat(testRegion.name).isEqualTo(DEFAULT_NAME)
        assertThat(testRegion.parentCode).isEqualTo(DEFAULT_PARENT_CODE)
    }

    @Test
    @Transactional
    fun createRegionWithExistingId() {
        val databaseSizeBeforeCreate = regionRepository.findAll().size

        // Create the Region with an existing ID
        region.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restRegionMockMvc.perform(
            post("/api/regions")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(region))
        ).andExpect(status().isBadRequest)

        // Validate the Region in the database
        val regionList = regionRepository.findAll()
        assertThat(regionList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkCodeIsRequired() {
        val databaseSizeBeforeTest = regionRepository.findAll().size
        // set the field null
        region.code = null

        // Create the Region, which fails.

        restRegionMockMvc.perform(
            post("/api/regions")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(region))
        ).andExpect(status().isBadRequest)

        val regionList = regionRepository.findAll()
        assertThat(regionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = regionRepository.findAll().size
        // set the field null
        region.name = null

        // Create the Region, which fails.

        restRegionMockMvc.perform(
            post("/api/regions")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(region))
        ).andExpect(status().isBadRequest)

        val regionList = regionRepository.findAll()
        assertThat(regionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllRegions() {
        // Initialize the database
        regionRepository.saveAndFlush(region)

        // Get all the regionList
        restRegionMockMvc.perform(get("/api/regions?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(region.id?.toInt())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].parentCode").value(hasItem(DEFAULT_PARENT_CODE)))
    }

    @Test
    @Transactional
    fun getRegion() {
        // Initialize the database
        regionRepository.saveAndFlush(region)

        val id = region.id
        assertNotNull(id)

        // Get the region
        restRegionMockMvc.perform(get("/api/regions/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.parentCode").value(DEFAULT_PARENT_CODE))
    }

    @Test
    @Transactional
    fun getNonExistingRegion() {
        // Get the region
        restRegionMockMvc.perform(get("/api/regions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateRegion() {
        // Initialize the database
        regionRepository.saveAndFlush(region)

        val databaseSizeBeforeUpdate = regionRepository.findAll().size

        // Update the region
        val id = region.id
        assertNotNull(id)
        val updatedRegion = regionRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedRegion are not directly saved in db
        em.detach(updatedRegion)
        updatedRegion.code = UPDATED_CODE
        updatedRegion.name = UPDATED_NAME
        updatedRegion.parentCode = UPDATED_PARENT_CODE

        restRegionMockMvc.perform(
            put("/api/regions")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updatedRegion))
        ).andExpect(status().isOk)

        // Validate the Region in the database
        val regionList = regionRepository.findAll()
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate)
        val testRegion = regionList[regionList.size - 1]
        assertThat(testRegion.code).isEqualTo(UPDATED_CODE)
        assertThat(testRegion.name).isEqualTo(UPDATED_NAME)
        assertThat(testRegion.parentCode).isEqualTo(UPDATED_PARENT_CODE)
    }

    @Test
    @Transactional
    fun updateNonExistingRegion() {
        val databaseSizeBeforeUpdate = regionRepository.findAll().size

        // Create the Region

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegionMockMvc.perform(
            put("/api/regions")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(region))
        ).andExpect(status().isBadRequest)

        // Validate the Region in the database
        val regionList = regionRepository.findAll()
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteRegion() {
        // Initialize the database
        regionRepository.saveAndFlush(region)

        val databaseSizeBeforeDelete = regionRepository.findAll().size

        val id = region.id
        assertNotNull(id)

        // Delete the region
        restRegionMockMvc.perform(
            delete("/api/regions/{id}", id)
                .accept(APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val regionList = regionRepository.findAll()
        assertThat(regionList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        equalsVerifier(Region::class)
        val region1 = Region()
        region1.id = 1L
        val region2 = Region()
        region2.id = region1.id
        assertThat(region1).isEqualTo(region2)
        region2.id = 2L
        assertThat(region1).isNotEqualTo(region2)
        region1.id = null
        assertThat(region1).isNotEqualTo(region2)
    }

    companion object {

        private const val DEFAULT_CODE: String = "AAAAAAAAAA"
        private const val UPDATED_CODE = "BBBBBBBBBB"

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_PARENT_CODE: String = "AAAAAAAAAA"
        private const val UPDATED_PARENT_CODE = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Region {
            val region = Region(
                code = DEFAULT_CODE,
                name = DEFAULT_NAME,
                parentCode = DEFAULT_PARENT_CODE
            )

            return region
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Region {
            val region = Region(
                code = UPDATED_CODE,
                name = UPDATED_NAME,
                parentCode = UPDATED_PARENT_CODE
            )

            return region
        }
    }
}
