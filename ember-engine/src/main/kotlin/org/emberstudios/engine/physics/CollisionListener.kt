package org.emberstudios.engine.physics

import org.emberstudios.core.logger.getLogger
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.dynamics.contacts.Contact

class CollisionListener : ContactListener {

	companion object {
		private val LOGGER = getLogger<CollisionListener>()
	}

	override fun beginContact(contact: Contact) {
		val userDataA = contact.fixtureA.userData as PhysicsUserData?
		val userDataB = contact.fixtureB.userData as PhysicsUserData?
		LOGGER.info { "Collision begin between ${userDataA?.name ?: "null"} and ${userDataB?.name ?: "null"}" }
	}

	override fun endContact(contact: Contact) {
		val userDataA = contact.fixtureA.userData as PhysicsUserData?
		val userDataB = contact.fixtureB.userData as PhysicsUserData?
		LOGGER.info { "Collision end between ${userDataA?.name ?: "null"} and ${userDataB?.name ?: "null"}" }
	}

	override fun preSolve(contact: Contact, oldManifold: Manifold) {
	}

	override fun postSolve(contact: Contact, impulse: ContactImpulse) {
	}
}