name := "lorien"

description := "Master of dreams and visions; transforms videos into feature vectors"

lazy val commonSettings = Seq(
	organization := "com.github.kokellab",
	version := "0.0.1",
	scalaVersion := "2.11.8",
	javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation"),
	testOptions in Test += Tests.Argument("-oF"),
	homepage := Some(url("https://github.com/kokellab/lorien")),
	developers := List(Developer("dmyersturnbull", "Douglas Myers-Turnbull", "dmyersturnbull@kokellab.com", url("https://github.com/dmyersturnbull"))),
	startYear := Some(2016),
	scmInfo := Some(ScmInfo(url("https://github.com/kokellab/lorien"), "https://github.com/kokellab/lorien.git")),
	libraryDependencies ++= Seq(
		"com.jsuereth" %% "scala-arm" % "1.4",
		"org.apache.spark" %% "spark-core" % "2.0.1",
		"org.apache.spark" %% "spark-sql" % "2.0.1",
		"org.apache.spark" %% "spark-mllib" % "2.0.1",
		//"com.github.haifengl" % "smile-scala_2.12" % "1.2.0",
		"com.github.kokellab" % "valar-core" % "0.3.0-SNAPSHOT",
		"org.scalanlp" %% "breeze" % "0.12",
		"com.sksamuel.scrimage" %% "scrimage-core" % "2.1.7",
		"com.squants"  %% "squants"  % "0.6.2",
		"com.google.guava" % "guava" % "20.0",
		"com.typesafe.slick" %% "slick" % "3.1.1",
		"org.scalatra" %% "scalatra" % "2.4.1",
		"org.slf4j" % "slf4j-api" % "1.7.21",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
		"ch.qos.logback" %  "logback-classic" % "1.1.7",
		"com.github.aishfenton" %% "vegas" % "0.2.3",
		"org.scalatest" %% "scalatest" % "3.0.0" % "test",
		"org.scalactic" %% "scalactic" % "3.0.0" % "test",
		"org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
		"org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"
	) map (_.exclude("log4j", "log4j")) map (_.exclude("org.slf4j", "slf4j-log4j12")),
	pomExtra :=
		<issueManagement>
			<system>Github</system>
			<url>https://github.com/kokellab/lorien/issues</url>
		</issueManagement>
)

lazy val core = project.
		settings(commonSettings: _*)

lazy val root = (project in file(".")).
		settings(commonSettings: _*).
		aggregate(core)
