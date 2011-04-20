package com.juiceanalytics.nectar.model

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfterEach
import appengine.UserEntity
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig


@RunWith(classOf[JUnitRunner])
class SliceTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  val sliceFixture = new Slice
  val helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  override protected def withFixture(test: NoArgTest) {
    // Reset the test fixture before executing any of the tests.
    sliceFixture.sliceType = ""
    sliceFixture.title = ""
    sliceFixture.subtitle = ""
    sliceFixture.index = 0


    test()
  }

  override def beforeEach() {
    helper.setUp()
  }

  override def afterEach() {
    helper.tearDown()
  }

  test("Default setup") {

    sliceFixture.sliceType should equal("")
    sliceFixture.title should equal("")
    sliceFixture.subtitle should equal("")
    sliceFixture.index should equal(0)
  }



  test("Testing the Create") {
    val sliceTmp = Slice.createTester();
    sliceTmp.sliceType should equal("KeyMetrics")
    sliceTmp.title should equal("Key Metric Slice")
    sliceTmp.subtitle should equal("FOO SUB TITLE")
    sliceTmp.index should equal(0)
    Slice.save(sliceTmp)
    val sliceOption = Slice.getBySliceType("KeyMetrics")
    sliceOption.size should equal(1)
    val sliceResult = sliceOption.get
    sliceResult.sliceType should equal("KeyMetrics")
    sliceResult.title should equal("Key Metric Slice")
    sliceResult.subtitle should equal("FOO SUB TITLE")
    sliceResult.index should equal(0)

  }
  test("Testing the Delete") {
    val sliceTmp = Slice.createTester();
    sliceTmp.sliceType should equal("KeyMetrics")
    sliceTmp.title should equal("Key Metric Slice")
    sliceTmp.subtitle should equal("FOO SUB TITLE")
    sliceTmp.index should equal(0)
    Slice.save(sliceTmp)

    var sliceResult = Slice.getBySliceType("KeyMetrics").size
    sliceResult should equal(1)
    Slice.delete(sliceTmp)
    sliceResult = Slice.getBySliceType("KeyMetrics").size
    sliceResult should equal(0)

  }


  test("Testing the Update") {
    val sliceTmp = Slice.createTester();
    sliceTmp.sliceType should equal("KeyMetrics")
    sliceTmp.title should equal("Key Metric Slice")
    sliceTmp.subtitle should equal("FOO SUB TITLE")
    sliceTmp.index should equal(0)

    var sliceResult = Slice.getBySliceType("KeyMetrics").size
    sliceResult should equal(1)
    sliceTmp.title = "FOOBAR"
    Slice.save(sliceTmp)
    val sliceOption = Slice.getBySliceType("KeyMetrics")
    sliceOption.size should equal(1)
    sliceOption.get.title should equal("FOOBAR")

  }

  test("Test Default setup") {
    val sliceTmp = Slice.createTester();
    sliceTmp.sliceType should equal("KeyMetrics")
    sliceTmp.title should equal("Key Metric Slice")
    sliceTmp.subtitle should equal("FOO SUB TITLE")
    sliceTmp.index should equal(0)
  }

  test("Test the Get All")    {
       val sliceTmp = Slice.createTester();
    sliceTmp.sliceType should equal("KeyMetrics")
    sliceTmp.title should equal("Key Metric Slice")
    sliceTmp.subtitle should equal("FOO SUB TITLE")
    sliceTmp.index should equal(0)

    val sliceOption = new Slice
    sliceOption.title = "FOOBAR"
    Slice.save(sliceOption)
    val resultSet = Slice.getAll();
    resultSet.size should equal (2)

  }

}
