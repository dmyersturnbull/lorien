name := "lorien"

description := "Lorien transforms video data into useful features."

lazy val commonSettings = Seq(
	organization := "com.github.kokellab",
	version := "0.4.0-SNAPSHOT",
	scalaVersion := "2.12.2",
	crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
	javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation"),
	testOptions in Test += Tests.Argument("-oF"),
	homepage := Some(url("https://github.com/kokellab/lorien")),
	developers := List(Developer("dmyersturnbull", "Douglas Myers-Turnbull", "dmyersturnbull@kokellab.com", url("https://github.com/dmyersturnbull"))),
	startYear := Some(2016),
	scmInfo := Some(ScmInfo(url("https://github.com/kokellab/lorien"), "https://github.com/kokellab/lorien.git")),
	libraryDependencies ++= Seq(
		"org.apache.spark" %% "spark-core" % "2.2.1",
		"org.apache.spark" %% "spark-sql" % "2.2.1",
		"org.apache.spark" %% "spark-mllib" % "2.2.1",
		//"com.github.haifengl" %% "smile-scala" % "1.2.0",
		"org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0",
		"org.scalanlp" %% "breeze" % "0.13.2",
//		"org.spire-math" %% "spire" % "0.13.0",
		"com.sksamuel.scrimage" %% "scrimage-core" % "2.1.8",
//		"com.sksamuel.scrimage" %% "scrimage-filters" % "2.1.8",
		//"org.bytedeco" % "javacv-platform" % "1.3.2",
//		"org.boofcv" % "core" % "0.26",
		"org.openpnp" % "opencv" % "3.2.0-1",
		"org.typelevel"  %% "squants"  % "1.3.0",
		"com.google.guava" % "guava" % "23.5-jre",
		"com.typesafe.slick" %% "slick" % "3.2.0",
		"org.slf4j" % "slf4j-api" % "1.8.0-beta0",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
		"org.scalatest" %% "scalatest" % "3.0.4" % "test",
		"org.scalactic" %% "scalactic" % "3.0.4" % "test",
		"org.scalacheck" %% "scalacheck" % "1.13.5" % "test",
//		"org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % "test"
	) map (_.exclude("log4j", "log4j")) map (_.exclude("org.slf4j", "slf4j-log4j12")),
	pomExtra :=
		<issueManagement>
			<system>Github</system>
			<url>https://github.com/kokellab/lorien/issues</url>
		</issueManagement>
)

lazy val core = project.
		settings(commonSettings: _*)

lazy val simple = project.
	dependsOn(core).
	settings(commonSettings: _*)

lazy val roi = project.
		dependsOn(core).
		settings(commonSettings: _*)

lazy val root = (project in file(".")).
		settings(commonSettings: _*).
		aggregate(core, simple, roi)
