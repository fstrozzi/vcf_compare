

import org.rogach.scallop._

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  version("VCF Compare 1.0 Copyright(c) Francesco Strozzi")
  val input = opt[List[String]](name = "input", short = 'i', descr = "List of VCF files (support gz files)")
  val saveTable = opt[Boolean](name = "save", short = 's', descr = "Create a table with all SNPs (Illu format)")
  val checkCommon = opt[Boolean](name = "common", short = 'c', descr = "Check common SNPs across samples (write report)")
  val readTable = opt[String](name="read",descr="Read SNP data from table file")
}
