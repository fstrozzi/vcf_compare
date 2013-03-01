name := "vcf_compare"

scalaVersion := "2.10.0"

version := "0.1"

fork in run := true

javaOptions in run += "-Xmx2G"

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.1.0",
	"com.top10" %% "scala-redis-client" % "1.10.0" withSources()
)



resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
				

