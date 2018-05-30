name := "lorien"

description := "Lorien transforms video data into useful features."

lazy val commonSettings = Seq(
	organization := "com.github.kokellab",
	version := "0.5.0-SNAPSHOT",
	scalaVersion := "2.12.6",
	javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation"),
	testOptions in Test += Tests.Argument("-oF"),
	homepage := Some(url("https://github.com/kokellab/lorien")),
	developers := List(Developer("dmyersturnbull", "Douglas Myers-Turnbull", "dmyersturnbull@kokellab.com", url("https://github.com/dmyersturnbull"))),
	startYear := Some(2016),
	scmInfo := Some(ScmInfo(url("https://github.com/kokellab/lorien"), "https://github.com/kokellab/lorien.git")),
	libraryDependencies ++= Seq(
		"org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0",
		"org.scalanlp" %% "breeze" % "1.0-RC2",
		"com.sksamuel.scrimage" %% "scrimage-core" % "2.1.8",
		"org.bytedeco" % "javacv-platform" % "1.4.1",
		"org.boofcv" % "core" % "0.26",
		"org.typelevel"  %% "squants"  % "1.3.0",
		"com.google.guava" % "guava" % "25.1-jre",
		"com.typesafe.slick" %% "slick" % "3.2.0",
		"org.slf4j" % "slf4j-api" % "1.8.0-beta2",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
		"org.scalatest" %% "scalatest" % "3.0.5" % "test",
		"org.scalactic" %% "scalactic" % "3.0.5" % "test",
		"org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
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
