package ling572

import collection._
import util.Instance
import java.io.File
import scala.collection.JavaConverters._
import collection.immutable

class MaxEntModel {

  var lambdasByClass = new mutable.HashMap[String,mutable.HashMap[String,Double]]()

  def addFeatureLambda(classLabel: String, featureLabel: String, lambda: Double) {
	  lambdasByClass.get(classLabel) match {
	    case Some(lambdas) => lambdas.put(featureLabel, lambda) 
	    case None => { 
	      val lambdas = new mutable.HashMap[String,Double]()
	      lambdas.put(featureLabel, lambda)
	      lambdasByClass.put(classLabel, lambdas)
	    }
	  }
  }

  // make immutable
  lazy val constLambdasByClass = {
     lambdasByClass.map(kv => (kv._1, kv._2.toMap)).toMap
  }

  lazy val features:Set[String] = {
     lambdasByClass.flatMap(kv => kv._2.keySet).toSet
  }

  lazy val classLabels = constLambdasByClass.keys.toSeq.sorted

  def classLambdasJava: java.util.HashMap[String, java.util.HashMap[String,java.lang.Double]] = {
    val cmap = new java.util.HashMap[String,java.util.HashMap[String,java.lang.Double]]()
    for ((label,lambdas) <- constLambdasByClass) {
      val fmap = new java.util.HashMap[String,java.lang.Double]()
      lambdas.foreach(kv => fmap.put(kv._1, kv._2) )
      cmap.put(label,fmap)
    }
    cmap
  }

  val classLinePattern = """FEATURES FOR CLASS ([\S]+)""".r 
  val featureLinePattern = """[\s]+([\S]+)[\s]+([\S]+)""".r
  var currentClass:String = ""

  def loadFromFile(file: File) {
    scala.io.Source.fromFile(file).getLines().foreach(parseLine)
  }

  def parseLine(line: String) {
	  line match {
	  	case classLinePattern(cl) => currentClass = cl
	  	case featureLinePattern(fn,lambda) => addFeatureLambda(currentClass, fn, lambda.toDouble)
	  	case _ => Nil
	  }	  
	}

  def scoreInstance(instance: Instance): (String, immutable.ListMap[String,Double] ) = {
    val scores = new mutable.HashMap[String,Double]()
    val sInstance = instance.getFeatures.asScala
    for ((label,lambdas) <- constLambdasByClass) {
      var score = lambdas.getOrElse("<default>",0.0)
      score += sInstance
          .map({ case (k: String,v:Integer) =>
              v * lambdas.getOrElse(k,0.0)
            })
          .sum
      scores.put(label, math.exp(score))
    }
    val normalizer = scores.values.sum
    val normedScores = immutable.ListMap() ++ scores.toList.sortBy(-_._2).map{ kv => (kv._1 , kv._2 / normalizer  )}
    val label = normedScores.head._1
    (label,normedScores)
  }

  def scoreInstanceJava(instance: Instance) = {
    val scores = scoreInstance(instance)
    val map = new java.util.HashMap[String,java.lang.Double]()
    scores._2.foreach(kv => map.put(kv._1, kv._2))
    map
  }

  def containsFeature(feature:String) = features.contains(feature)

}