name := "lorien"

lazy val commonSettings = Seq(
	organization := "com.github.dmyersturnbull",
	version := "0.0.1",
	scalaVersion := "2.11.8",
	javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:all"),
	scalacOptions ++= Seq("-unchecked", "-deprecation"),
	testOptions in Test += Tests.Argument("-oF"),
	libraryDependencies ++= Seq(
		"com.jsuereth" %% "scala-arm" % "1.4",
		"org.apache.spark" %% "spark-core" % "2.0.1",
		"org.apache.spark" %% "spark-sql" % "2.0.1",
		"org.apache.spark" %% "spark-mllib" % "2.0.1",
		//"com.github.haifengl" % "smile-scala_2.12" % "1.2.0",
		"org.scalanlp" %% "breeze" % "0.12",
		"com.sksamuel.scrimage" %% "scrimage-core" % "2.1.7",
		"com.squants"  %% "squants"  % "0.6.2",
		"com.google.guava" % "guava" % "19.0",
		"com.typesafe.slick" %% "slick" % "3.1.1",
		"org.scalatra" %% "scalatra" % "2.4.1",
		"org.slf4j" % "slf4j-api" % "1.7.21",
		"com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
		"ch.qos.logback" %  "logback-classic" % "1.1.7",
		"com.github.aishfenton" %% "vegas" % "0.2.3",
		"org.specs2" %% "specs2-core" % "3.8.6" % "test",
		"org.specs2" %% "specs2-scalacheck" % "3.8.6" % "test",
		"org.specs2" %% "specs2-mock" % "3.8.6" % "test",
		"org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
		"org.mockito" % "mockito-core" % "2.2.15" % "test"
	) map (_.exclude("log4j", "log4j")) map (_.exclude("org.slf4j", "slf4j-log4j12")),
	pomExtra :=
			<url>https://github.com/dmyersturnbull/lorien</url>
					<scm>
						<url>https://github.com/dmyersturnbull/lorien</url>
						<connection>https://github.com/dmyersturnbull/lorien.git</connection>
					</scm>
					<developers>
						<developer>
							<id>dmyersturnbull</id>
							<name>Douglas Myers-Turnbull</name>
							<url>https://www.dmyersturnbull.com</url>
							<timezone>-8</timezone>
						</developer>
					</developers>
					<issueManagement>
						<system>Github</system>
						<url>https://github.com/dmyersturnbull/lorien/issues</url>
					</issueManagement>
)

lazy val core = project.
		settings(commonSettings: _*)

lazy val root = (project in file(".")).
		settings(commonSettings: _*).
		aggregate(core)
