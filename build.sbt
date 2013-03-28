name := "vcf_compare"

scalaVersion := "2.10.0"

version := "1.0"

fork in run := true

javaOptions in run += "-Xmx2520M"

libraryDependencies ++= Seq(
  //"com.h2database" % "h2" % "1.3.171",
  //"com.typesafe.slick" %% "slick" % "1.0.0",
  //"org.slf4j" % "slf4j-nop" % "1.6.4",
 // "org.mapdb" % "mapdb" % "0.9-SNAPSHOT",
  "org.rogach" %% "scallop" % "0.8.0"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
				

