name := "vcf_compare"

scalaVersion := "2.10.0"

version := "1.0"

fork in run := true

javaOptions in run += "-Xmx1562M"

libraryDependencies ++= Seq(
//	"com.typesafe.akka" %% "akka-actor" % "2.1.0",
    "org.rogach" %% "scallop" % "0.8.0"
)



resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
				

