package ling572

import util.{ConditionalFreqDist, Instance, VectorFileReader}
import java.io.{PrintWriter, File}
import scala.collection._
import scala.collection.JavaConverters._
import annotation.tailrec

object Driver extends App {

  var testData:File = null
  var boundaryData:File = null
  var outputFile:File = null
  var modelFile:File = null
  var beamSize:Double  = 0.0
  var topN:Int = 0
  var topK:Int = 0

  ////////////// argument parsing ///////////////////

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

  ////////////// setup ///////////////////

  val model:MaxEntModel = new MaxEntModel()
  model.loadFromFile(modelFile)

  val allInstances = VectorFileReader.indexInstances(testData).asScala

  val boundaries = scala.io.Source.fromFile(boundaryData).getLines().map(x => x.toInt).toSeq
  val confusionMatrix = new ConditionalFreqDist[String]()
  val sysOut = new PrintWriter(outputFile)
  sysOut.println("\n%%%%% test data:")
  var count = 0
  var correct = 0

  ///////////// search /////////////////

  // helper function
  @tailrec
  def sentenceSearch(instances:Seq[Instance], boundaries:Seq[Int])  {
    if (boundaries.isEmpty || instances.isEmpty) return

    val s_length = boundaries.head
    val beamSearch = new BeamSearch(topK, topN, beamSize, model)
    beamSearch.search( (instances take s_length).asJava )

    var node = beamSearch.getBestNode
    val tags = new mutable.ArrayBuffer[String]
    val nodes = new mutable.ArrayBuffer[BeamSearchNode]
    while (node.getParent != null) {
      tags += node.getTag
      nodes += node
      node = node.getParent
    }

    // sys_out
    nodes.reverse.foreach {  node =>
      sysOut.println(node.getName + " " + node.getGoldTag + " " + node.getTag + " " + node.getNodeProb)
    }

    // confusion matrix
    val instanceLabels = instances.map( instance => instance.getLabel )
    tags.reverse zip instanceLabels foreach { case (tag , gold) =>
      count += 1
      if (tag.equals(gold)) correct += 1
      confusionMatrix.add(gold, tag)
    }

    // recurse
    sentenceSearch(instances drop s_length, boundaries.tail)
  }

  // do tagging
  sentenceSearch(allInstances,boundaries)

  /////////// output ////////////

  println("class_num=" + model.classLabels.length + " feat_num=" + model.features.size + "\n")

  println("Confusion matrix for the test data:\nrow is the truth, column is the system output\n")


  // print confusion matrix
  val classLabels = confusionMatrix.keySet.toSeq.sorted

  print("\t")
  for (label <- classLabels) print(label + " ")
  println()
  for (gold <- classLabels) {
    print(gold + " ")
    for (label <- classLabels) {
      print(confusionMatrix.N(gold, label) + " ")
    }
    println()
  }

  println(" Test accuracy=" + (correct.toDouble) / count.toDouble)

}

