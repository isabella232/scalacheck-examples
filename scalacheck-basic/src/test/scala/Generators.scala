package com.company.scalacheck

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties, Gen}

object SimpleGenerator extends Properties("Simple sample generator") {
  // simple generator based on the example given in the ScalaCheck documentation
  // TODO: create a more suitable one?
  val myGen = for {
      n <- Gen.choose(10,20)
      m <- Gen.choose(2*n, 500)
  } yield (n,m)

  // simple function
  def myFunction(n1:Int, n2:Int) = n1 + n2

  property("Test property with generator") = forAll(myGen) {(n:(Int,Int)) =>
    n match {
      case (n1, n2) => (n1 + n2) == myFunction(n1,n2)
    }
  }
}

/**
 * Specification with a Generator that is used to create case classes and verify the
 * data in them.
 *
 * In this example the generator returns both the case class with specific values as well as
 * the specific values that were used to genreate the case class, so that its calculations
 * can be verified
 */
object RectangleSpecification extends Properties("Rectangle specification") {

  val rectangleGen:Gen[(Rectangle, Double,Double)] = for {
    height <- Gen.choose(0,9999)
    width <- Gen.choose(0,9999)
  } yield((Rectangle(width, height), width,height))

  property("Test area") = forAll(rectangleGen) { (input:(Rectangle,Double,Double)) => input match {
    case(r, width, height) => r.area == width * height
  }}
}

/**
 * Generator of case objects for the Rectangle class, as well as an arbitrary generator
 */
object RectangleGenerator {
  // generator for the Rectangle case class
  val rectangleGen:Gen[Rectangle] = for {
    height <- Gen.choose(0,9999)
    width <- Gen.choose(0,9999)
  } yield(Rectangle(width, height))

  // Arbitrary generator of rectangles
  implicit val arbRectangle: Arbitrary[Rectangle] = Arbitrary(rectangleGen)
}

/**
 * This property shows the advantage of using an arbitrary generator, as ScalaCheck will then
 * be able to automatically generate the test data using the implicit arbitrary generator in scope,
 * and we don't need to provide a generator object as a parameter to the forAll method
 *
 * In this case, the arbitrary generator is in the scope after the import statement to import
 * the arbitrary function
 */
object ArbitraryRectangleSpecification extends Properties("Rectangle specification with an Arbitrary generator") {
  import RectangleGenerator._

  // generate two random rectangles and check which one is bigger
  property("Test biggerThan") = forAll{ (r1:Rectangle, r2:Rectangle) =>
    (r1 biggerThan r2) == (r1.area > r2.area)
  }

  // please note that if not using an arbitrary generator, then we need to provide a generator parameter
  // for each one of the random data parameters. Therefore, the following code is equivalent to the
  // one above:
  property("Test biggerThan") = forAll(rectangleGen, rectangleGen){ (r1:Rectangle, r2:Rectangle) =>
    (r1 biggerThan r2) == (r1.area > r2.area)
  }
}