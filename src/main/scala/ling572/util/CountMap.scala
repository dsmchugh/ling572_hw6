package ling572.util

import collection._

class CountMap[K](buildFrom: Iterable[K] = None) extends mutable.HashMap[K, Int] {

  override def default(key: K) = 0

  // initializer
  buildFrom foreach addKey

  def addKey(v: K) {
    this(v) = this(v) + 1
  }

  def addKeys(from: Iterable[K]) {
    from foreach addKey
  }

  def smoothedCount(v: K, delta: Double):Double = {  this(v).toDouble + delta }

  def smoothedN(delta: Double):Double = { this.N.toDouble + (delta * this.keys.size) }

  def N = {
    values.sum
  }

}


class FreqDist[K] extends CountMap[K] {

  def prob(key: K): Double = {
    val c = this.getOrElse(key, 0).toDouble
    c / N
  }

  def logProb(key:K) = scala.math.log10(prob(key))

  def smoothedProb(key: K, delta: Double=0.0) = {
    smoothedCount(key, delta) / smoothedN(delta)
  }

  def logSmoothedProb(key:K, delta:Double) = scala.math.log10(smoothedProb(key,delta))
}

 class ConditionalFreqDist[K] extends mutable.HashMap[K,FreqDist[K]] {

   //type FreqDist = CountMap[K]

    //override def default(key: K) = new CountMap[K]

    def add(context: K, token: K) {
      this.getOrElseUpdate(context, new FreqDist).addKey(token)
    }

   // convenience method
   def add(tuple: (K, K)) {
     this.add(tuple._1,tuple._2)
   }

   def N(context: K, token: K) = {
     this.get(context) match {
       case Some(map) => map.getOrElse(token,0)
       case None => 0
     }
   }

   def N(context: K) = {
      this.get(context) match {
        case Some(m) => m.N
        case None => 0
      }
   }

   def N = {
     // sum over all contexts
     this.values.map({ x => x.values.sum }).sum
   }

   def prob(context: K, token: K) = {
     N(context,token) / N(context).toDouble
   }

   def logProb(context:K, token:K) = scala.math.log10(prob(context,token))

   def smoothedProb(context: K, token: K, delta: Double):Double = {
     (N(context,token) + delta) / (N(context)  + (2.0* delta))
   }

   def logSmoothedProb(context:K, token:K, delta:Double) = scala.math.log10(smoothedProb(context,token,delta))

}