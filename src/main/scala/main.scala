
import com.top10.redis._
import java.util.zip._
import java.io._
import scala.io._

object VcfCompare extends App {
  
  val redis = new SingleRedis("localhost",6379)
  val maxSamples = args.size
  args.zipWithIndex.foreach {a =>
    val vcf = openFile(a._1)
    addVcf(vcf,a._2,maxSamples,redis)
  } 

  def openFile(vcf: String) : BufferedSource = {
    if (vcf.endsWith(".gz")) {
      new BufferedSource(new GZIPInputStream(new BufferedInputStream(new FileInputStream(vcf),1000000)),1000000)
    }
    else {
      Source.fromFile(vcf) 
    }

  }


  def addVcf(openVcf: BufferedSource, samplePos: Int, maxSamples: Int, redis: SingleRedis) = { 
    var vcf = Set[String]()
    redis.exec(pipeline => {
      openVcf.getLines.foreach {line =>  
        if(!line.startsWith("#")) {
          val elements = line.split("\t")
          val key = elements(0)+"-"+elements(1)+"-"+elements(3)
          val value = redis.get(key)
          value match {
            case Some(x) => {
              val alternatives = x.updated(samplePos,elements(4))
              pipeline.set(key,alternatives.toString)
            } 
            case None => {
              val alternatives = new String(Array.fill[Char](samplePos)('0') ++ elements(4) ++
                               Array.fill[Char](maxSamples-(samplePos+1))('0'))
              pipeline.set(key,alternatives)
            } 
          }
         }
        }
      })  
      vcf 
    }
  }

