lazy val root = project
  .in(file("."))
  .settings(
    name         := "charge-card-worker",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := "3.1.2",
    libraryDependencies ++= Seq(
      "org.camunda.bpm" % "camunda-external-task-client" % "7.17.0",
      "org.slf4j"       % "slf4j-simple"                 % "1.7.36",
      "javax.xml.bind"  % "jaxb-api"                     % "2.3.1"
    )
  )
