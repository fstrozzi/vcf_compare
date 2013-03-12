
import java.util.zip._
import java.io._
import scala.io._
import scala.collection.JavaConversions._

object VcfCompare extends App {

  val conf = new Conf(args)
  println(conf.summary)
  val files = conf.input()
  val table = conf.createTable()
  val common = conf.checkCommon()

  val dict = Map[String, Short]("00" -> 0, "AA" -> 1, "AT" -> 2, "AC" -> 3, "AG" -> 4,
    "TA" -> 5, "TC" -> 6, "TG" -> 7, "TT" -> 8,
    "CA" -> 9, "CT" -> 10, "CG" -> 11, "CC" -> 12,
    "GA" -> 13, "GT" -> 14, "GC" -> 15, "GG" -> 16
  )
  val dictRev = dict.map(_.swap)

  val maxSamples = files.size
  val vcf = new java.util.HashMap[String, Array[Short]]()
  val names = files.map(el => el.split("/").last)

  files.zipWithIndex.foreach {
    a =>
      val vcfFile = openFile(a._1)
      println("Reading file " + a._1 + "...")
      addVcf(vcf, vcfFile, a._2, maxSamples, dict)
      println(vcf.size)
  }

  if (table) {
    println("Writing global SNPs table...")
    writeMatrix(vcf, dictRev, names)
  }
  if (common) {
    println("Checking common SNPs across samples...")
    checkCommon(vcf, names)
  }

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
      alt.replaceAll(",", "")
    }
  }

  def writeMatrix(vcf: java.util.HashMap[String, Array[Short]], dictRev: Map[Short, String], sampleNames: List[String]) {
    val out = new java.io.FileWriter("all_snps.txt")
    out.write("SNPID\t" + sampleNames.mkString("\t") + "\n")
    vcf.keySet().foreach {
      k =>
        val genotypes = vcf(k).map(el => dictRev(el))
        out.write(k + "\t" + genotypes.mkString("\t") + "\n")
    }
  }

  def checkCommon(vcf: java.util.HashMap[String, Array[Short]], sampleNames: List[String]) {

    var samples = initSamples(sampleNames)
    samples = checkCommonSNPs(sampleNames, samples, vcf)
    samples.keys.foreach {
      k => println(k, samples(k))
    }
  }

  def initSamples(names: List[String]): scala.collection.mutable.Map[String, Int] = {
    val sampleMap = scala.collection.mutable.Map[String, Int]()
    sampleMap += "all" -> 0
    names.zipWithIndex.foreach {
      n =>
        sampleMap += n._1 -> 0
      /*for (pos <- new Range(n._2 + 1, names.size, 1)) {
        sampleMap += n._1 + "-" + names(pos) -> 0
      } */
    }
    sampleMap
  }

  def checkCommonSNPs(names: List[String], s: scala.collection.mutable.Map[String, Int], vcf: java.util.HashMap[String, Array[Short]]): scala.collection.mutable.Map[String, Int] = {
    vcf.keySet().foreach {
      k =>
        val snps = vcf(k)
        val size = snps.size
        val zero: Short = 0
        /*snps.zipWithIndex.foreach {
          i =>
            if (i._1 != zero) {
              for (pos <- new Range(i._2 + 1, size, 1)) {
                if (snps(pos) != zero) {
                  s(names(i._2) + "-" + names(pos)) += 1
                }
              }
            }
        }*/
        if (!snps.contains(zero)) {
          s("all") += 1
        }
        if (snps.count(_ != 0) == 1) {
          s(names(snps.indexWhere(_ != 0))) += 1
        }
    }
    s
  }

}



