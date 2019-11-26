package net.timafe.letsgo2.domain

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotNull
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Place.
 */
@Entity
@Table(name = "place")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Place(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "summary")
    var summary: String? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Place) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Place{" +
        "id=$id" +
        ", name='$name'" +
        ", summary='$summary'" +
        ", imageUrl='$imageUrl'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
