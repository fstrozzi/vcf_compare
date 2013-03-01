
//import com.top10.redis._
import java.util.zip._
import java.io._
import scala.io._

object VcfCompare extends App {
 
  val dict = Map("AA"->1,"AT"->2,"AC"->3,"AG"->4,
                  "TA"->5,"TC"->6,"TG"->7,"TT"->8,
                  "CA"->9,"CT"->10,"CG"->11,"CC"->12,
                  "GA"->13,"GT"->14,"GC"->15,"GG"->16
    )
  //val redis = new SingleRedis("localhost",6379)
  val maxSamples = args.size
  val vcf = new java.util.HashMap[String,Array[Int]]()

  args.zipWithIndex.foreach {a =>
    val vcfFile = openFile(a._1)
    addVcf(vcf,vcfFile,a._2,maxSamples)
    println(vcf.size)
  } 

  def openFile(vcf: String) : BufferedSource = {
    if (vcf.endsWith(".gz")) {
      new BufferedSource(new GZIPInputStream(new BufferedInputStream(new FileInputStream(vcf),1000000)),1000000)
    }
    else {
      Source.fromFile(vcf) 
    }

  }


  def addVcf(vcf: java.util.HashMap[String,Array[Int]],openVcf: BufferedSource, samplePos: Int, maxSamples: Int) = { 
      openVcf.getLines.foreach {line =>  
        if(!line.startsWith("#")) {
          val elements = line.split("\t")
          val key = elements(0)+"-"+elements(1)
          val value = vcf.get(key)
          var alleles = elements(4).split(',').reduceLeft((a,b)=>a+b)
          if (alleles.size == 1) {
            alleles = elements(3)+alleles  
          }
          value match {
            case x: Array[Int] => {
              x(samplePos) = dict(alleles)
              vcf.put(key,x)
            }
            case null => {
              vcf.put(key,(Array.fill[Int](samplePos)(0)++Array(dict(alleles))++Array.fill[Int](maxSamples-(samplePos+1))(0)))
            }
          }
        }
      }
    }
  }

