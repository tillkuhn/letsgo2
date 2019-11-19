package net.timafe.letsgo2.web.rest.vm

import net.timafe.letsgo2.service.dto.UserDTO

/**
 * View Model extending the [UserDTO], which is meant to be used in the user management UI.
 */
class ManagedUserVM : UserDTO() {

    override fun toString() = "ManagedUserVM{${super.toString()}}"
}
