package com.juiceanalytics.nectar.resourceview

import com.google.inject.Guice
import com.juiceanalytics.nectar.config.AppTestModule
import com.juiceanalytics.nectar.security.MockAuthenticatedContext
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfterEach
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.juiceanalytics.nectar.model.Slice


/**
 * Is the unit test cases for the {@link SliceResource} class.
 *
 * @author Glenn Renfro
 */
@RunWith(classOf[JUnitRunner])
class SliceResourceTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  val injector = Guice.createInjector(new AppTestModule)

  val helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());


  override def beforeEach() {
    helper.setUp()
  }

  override def afterEach() {
    helper.tearDown()
  }


  test("current Slice is the empty Slice object") {
    val sliceResource = injector.getInstance(classOf[SliceResource])
    val sliceBean = sliceResource.current
    sliceBean.getId should be(null)
    sliceBean.getTitle should equal("")
    sliceBean.getSubTitle should equal("")
    sliceBean.getIndex should equal(0)
    sliceBean.getType should equal ("")

  }



  test("current Get All Slices is the mock User") {
    val sliceResource:SliceResource = injector.getInstance(classOf[SliceResource])
    sliceResource.sliceList.getSlices.size  should equal (0)
    Slice.createTester
    sliceResource.sliceList.getSlices.size  should equal (1)
    Slice.createTester
    sliceResource.sliceList.getSlices.size  should equal (2)

  }
}
