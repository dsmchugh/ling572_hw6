package ling572

import util.{Instance, VectorFileReader}
import java.io.File
import scala.collection._

object Driver extends App {

  var testData:File = null
  var boundaryData:File = null
  var outputFile:File = null
  var modelFile:File = null
  var beamSize:Double  = 0.0
  var topN:Int = 0
  var topK:Int = 0


  def exit(errorText:String) {
    System.out.println(errorText)
    System.exit(1)
  }

  if (args.length != 7)
   exit("Error: usage Driver test_data boundary_data model_file output_file beam_size top_N top_K")

  try {
    this.testData = new File(args(0))
  } catch {
    case e:Exception => exit("Error: invalid test_data file")
  }

  try {
    this.boundaryData = new File(args(1))
  } catch {
    case e:Exception => exit("Error: invalid boundary_data file")
  }

  try {
    this.modelFile = new File(args(2))
  } catch {
    case e:Exception => exit("Error: invalid model file")
  }

  try {
    this.outputFile = new File(args(3))
  } catch {
    case e:Exception => exit("Error: invalid output_file")
  }


  try {
    this.beamSize = args(4).toDouble
  } catch {
      case e:Exception => exit("Error: invalid beam_size")
  }

  try {
    this.topK = args(5).toInt
  } catch {
    case e:Exception => exit("Error: invalid top_N")
  }

  try {
    this.topN = args(6).toInt
  } catch {
    case e:Exception => exit("Error: invalid top_K")
  }

  println("args(4): [" + args(4) + "]  beam_width: " + beamSize)
  println("args(5): [" + args(5) + "]  topK: " + topK)
  println("args(6): [" + args(6) + "]  topN: " + topN)


  val model = new MaxEntModel()
  model.loadFromFile(modelFile)

  val allInstances: java.util.List[Instance] = VectorFileReader.indexInstances(testData)

  val beamSearch = new BeamSearch(topK, topN, beamSize, model)
  beamSearch.search(allInstances)

  var node = beamSearch.getBestNode

  val tags = new mutable.ArrayBuffer[String] // mapResult { xs => mutable.LinkedList( xs:_* ) }

  while (node.getParent != null) {
      tags += (node.getName + " " + node.getGoldTag + " " + node.getTag + " " + node.getNodeProb)
    node = node.getParent
  }

  for (tag <- tags.reverse) {
    System.out.println(tag)
  }

}

