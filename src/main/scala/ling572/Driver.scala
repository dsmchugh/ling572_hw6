package ling572

import ling572.util.SVMLightReader
import java.io.File

import scala.collection.JavaConverters._

object Driver extends App {

  var trainingData:File = null
  var outputFile:File = null
  var modelFile:File = null

  def exit(errorText:String) {
    System.out.println(errorText)
    System.exit(1)
  }

  if (args.length < 2 || args.length > 3)
   exit("Error: usage Q4Driver training_data output_file [model_file]")

  try {
    this.trainingData = new File(args(0))
  } catch {
    case e:Exception => exit("Error: invalid training_data file")
  }

  try {
    this.outputFile = new File(args(1))
  } catch {
    case e:Exception => exit("Error: invalid output_file")
  }

  if (args.length == 3) {
    try {
      this.modelFile = new File(args(2))
    } catch {
      case e:Exception => exit("Error: invalid model_file")
    }
  } else {
    this.modelFile = null
  }

  val instances = SVMLightReader.indexInstances(trainingData).asScala.toList

  val expectation = new ModelExpectation()
  expectation.setInstances(instances)

  if (modelFile != null) {
    val maxEnt = new MaxEntModel()
    maxEnt.loadFromFile(modelFile)
    expectation.setMaxEntModel(maxEnt)
  }

  expectation.build()
  expectation.generateOutput(outputFile)
}

