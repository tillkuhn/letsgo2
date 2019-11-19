package net.timafe.letsgo2.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import net.timafe.letsgo2.domain.Place
import net.timafe.letsgo2.repository.PlaceRepository
import net.timafe.letsgo2.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "place"

/**
 * REST controller for managing [net.timafe.letsgo2.domain.Place].
 */
@RestController
@RequestMapping("/api")
class PlaceResource(
    private val placeRepository: PlaceRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /places` : Create a new place.
     *
     * @param place the place to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new place, or with status `400 (Bad Request)` if the place has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/places")
    fun createPlace(@Valid @RequestBody place: Place): ResponseEntity<Place> {
        log.debug("REST request to save Place : {}", place)
        if (place.id != null) {
            throw BadRequestAlertException(
                "A new place cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = placeRepository.save(place)
        return ResponseEntity.created(URI("/api/places/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /places` : Updates an existing place.
     *
     * @param place the place to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated place,
     * or with status `400 (Bad Request)` if the place is not valid,
     * or with status `500 (Internal Server Error)` if the place couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/places")
    fun updatePlace(@Valid @RequestBody place: Place): ResponseEntity<Place> {
        log.debug("REST request to update Place : {}", place)
        if (place.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = placeRepository.save(place)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    place.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /places` : get all the places.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of places in body.
     */
    @GetMapping("/places")
    fun getAllPlaces(): MutableList<Place> {
        log.debug("REST request to get all Places")
        return placeRepository.findAll()
    }

    /**
     * `GET  /places/:id` : get the "id" place.
     *
     * @param id the id of the place to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the place, or with status `404 (Not Found)`.
     */
    @GetMapping("/places/{id}")
    fun getPlace(@PathVariable id: Long): ResponseEntity<Place> {
        log.debug("REST request to get Place : {}", id)
        val place = placeRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(place)
    }

    /**
     *  `DELETE  /places/:id` : delete the "id" place.
     *
     * @param id the id of the place to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/places/{id}")
    fun deletePlace(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Place : {}", id)

        placeRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
