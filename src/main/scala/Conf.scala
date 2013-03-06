

import org.rogach.scallop._

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  version("VCF Compare 1.0 Copyright(c) Francesco Strozzi")
  val input = opt[List[String]](name = "input", short = 'i', descr = "List of VCF files (also gz files)", required = true)
  val createTable = opt[Boolean](name = "table", short = 't', descr = "Create a table with all SNPs (Illu format)")
  val checkCommon = opt[Boolean](name = "common", short = 'c', descr = "Check common SNPs across samples (write report)")
}
