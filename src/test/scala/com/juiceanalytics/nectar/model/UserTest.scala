package com.juiceanalytics.nectar.model

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import appengine.UserEntity


@RunWith(classOf[JUnitRunner])
class UserTest extends FunSuite with ShouldMatchers {
  val userFixture = new User

  override protected def withFixture(test: NoArgTest) {
    // Reset the test fixture before executing any of the tests.
    userFixture.email = ""
    userFixture.salt64 = ""
    userFixture.hash64 = ""
    userFixture.firstName = ""
    userFixture.lastName = ""
    userFixture.rolesNames.clear()

    test()
  }

  test("user is not an editor or client admin") {
    userFixture.rolesNames.add(Roles.dashboardViewer)
    userFixture.isEditor should be(false)
    userFixture.isClientAdmin should be(false)
  }

  test("user is an editor but not a client admin") {
    userFixture.rolesNames.add(Roles.dashboardEditor)
    userFixture.isEditor should be(true)
    userFixture.isClientAdmin should be(false)
  }

  test("user is a client admin but not an editor") {
    userFixture.rolesNames.add(Roles.clientAdmin)
    userFixture.isEditor should be(false)
    userFixture.isClientAdmin should be(true)
  }

  test("user is both an editor and a client admin") {
    userFixture.rolesNames.add(Roles.dashboardEditor)
    userFixture.rolesNames.add(Roles.clientAdmin)
    userFixture.isEditor should be(true)
    userFixture.isClientAdmin should be(true)
  }

  test("user password can be compared to hashed value") {
    val plaintext = "hello, world"
    val salt = UserEntity.salt
    userFixture.salt64 = salt
    userFixture.hash64 = UserEntity.hashedCredentials(plaintext, userFixture.salt64)

    userFixture.credentials should equal(UserEntity.hashedCredentials(plaintext, salt))
    userFixture.salt should equal(salt)
  }

  test("user email address is the unique name") {
    userFixture.email = "iam@theworld.com"
    userFixture.uniqueName should equal(userFixture.email)
  }
}
