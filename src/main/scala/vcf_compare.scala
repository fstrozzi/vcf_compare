
import java.util.zip._
import java.io._
import scala.io._
import scala.collection.JavaConversions._

object VcfCompare extends App {

  val dict = Map[String, Short]("00" -> 0, "AA" -> 1, "AT" -> 2, "AC" -> 3, "AG" -> 4,
    "TA" -> 5, "TC" -> 6, "TG" -> 7, "TT" -> 8,
    "CA" -> 9, "CT" -> 10, "CG" -> 11, "CC" -> 12,
    "GA" -> 13, "GT" -> 14, "GC" -> 15, "GG" -> 16
  )
  val dictRev = dict.map(_.swap)

  val maxSamples = args.size
  val vcf = new java.util.HashMap[String, Array[Short]]()

  args.zipWithIndex.foreach {
    a =>
      val vcfFile = openFile(a._1)
      addVcf(vcf, vcfFile, a._2, maxSamples, dict)
      println(vcf.size)
  }
  writeMatrix(vcf, dictRev, args)

  def openFile(vcf: String): BufferedSource = {
    if (vcf.endsWith(".gz")) {
      new BufferedSource(new GZIPInputStream(new BufferedInputStream(new FileInputStream(vcf), 1000000)), 1000000)
    }
    else {
      Source.fromFile(vcf)
    }

  }

  def addVcf(vcf: java.util.HashMap[String, Array[Short]], openVcf: BufferedSource, samplePos: Int, maxSamples: Int, dict: Map[String, Short]) {
    openVcf.getLines.foreach {
      line =>
        if (!line.startsWith("#")) {
          val elements = line.split("\t")
          val key = elements(0) + "-" + elements(1)
          val value = vcf.get(key)
          val het = elements.last.split(':')(0)
          val alleles = getGenotype(elements(3), elements(4), het)

          value match {
            case x: Array[Short] => {
              x(samplePos) = dict(alleles)
              vcf.put(key, x)
            }
            case null => {
              vcf.put(key, (Array.fill[Short](samplePos)(0) ++ Array(dict(alleles)) ++ Array.fill[Short](maxSamples - (samplePos + 1))(0)))
            }
          }
        }
    }
  }

  def getGenotype(ref: String, alt: String, het: String): String = {
    if (het(0) == '0') {
      ref + alt
    }
    else if (het(2) == '1') {
      alt + alt
    }
    else {
      alt.split(',').reduceLeft((a, b) => a + b)
    }
  }

  def writeMatrix(vcf: java.util.HashMap[String, Array[Short]], dictRev: Map[Short, String], fileNames: Array[String]) {
    val out = new java.io.FileWriter("all_snps.txt")
    val names = fileNames.map(el => el.split("/").last)
    out.write(names.mkString("\t") + "\n")
    vcf.keySet().foreach {
      k =>
        val genotypes = vcf(k).map(el => dictRev(el))
        out.write(k + "\t" + genotypes.mkString("\t") + "\n")
    }
  }

}

