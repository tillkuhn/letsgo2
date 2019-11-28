package net.timafe.letsgo2.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import net.timafe.letsgo2.domain.Region
import net.timafe.letsgo2.repository.RegionRepository
import net.timafe.letsgo2.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val ENTITY_NAME = "region"
/**
 * REST controller for managing [net.timafe.letsgo2.domain.Region].
 */
@RestController
@RequestMapping("/api")
class RegionResource(
    private val regionRepository: RegionRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /regions` : Create a new region.
     *
     * @param region the region to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new region, or with status `400 (Bad Request)` if the region has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/regions")
    fun createRegion(@Valid @RequestBody region: Region): ResponseEntity<Region> {
        log.debug("REST request to save Region : {}", region)
        if (region.id != null) {
            throw BadRequestAlertException(
                "A new region cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = regionRepository.save(region)
        return ResponseEntity.created(URI("/api/regions/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /regions` : Updates an existing region.
     *
     * @param region the region to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated region,
     * or with status `400 (Bad Request)` if the region is not valid,
     * or with status `500 (Internal Server Error)` if the region couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/regions")
    fun updateRegion(@Valid @RequestBody region: Region): ResponseEntity<Region> {
        log.debug("REST request to update Region : {}", region)
        if (region.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = regionRepository.save(region)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     region.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /regions` : get all the regions.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of regions in body.
     */
    @GetMapping("/regions")
    // fun getAllRegions(): MutableList<Region> {
    fun getAllRegions(): MutableIterable<Region> {
        log.debug("REST request to get all Regions")
        return regionRepository.findAll()
    }

    /**
     * `GET  /regions/:id` : get the "id" region.
     *
     * @param id the id of the region to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the region, or with status `404 (Not Found)`.
     */
    @GetMapping("/regions/{id}")
    fun getRegion(@PathVariable id: String): ResponseEntity<Region> {
        log.debug("REST request to get Region : {}", id)
        val region = regionRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(region)
    }
    /**
     *  `DELETE  /regions/:id` : delete the "id" region.
     *
     * @param id the id of the region to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/regions/{id}")
    fun deleteRegion(@PathVariable id: String): ResponseEntity<Void> {
        log.debug("REST request to delete Region : {}", id)

        regionRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
