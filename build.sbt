name := "scala-tron"

scalaVersion := "2.12.3"

mainClass in Compile := Some("org.tron.example.Tron")

libraryDependencies ++= Seq(
  // Test
  "junit" % "junit" % "4.1" % Test,
  "org.mockito" % "mockito-all" % "1.9.5" % Test,

  // Main
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.25",
  "log4j" % "log4j" % "1.2.17",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.projectlombok" % "lombok" % "1.16.18",
  "commons-codec" % "commons-codec" % "1.11",
  "com.madgag.spongycastle" % "core" % "1.53.0.0",
  "com.madgag.spongycastle" % "prov" % "1.53.0.0",
  "org.springframework" % "spring-context" % "4.2.0.RELEASE",
  "com.google.guava" % "guava" % "18.0",
  "com.google.protobuf" % "protobuf-java" % "3.4.0",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "org.apache.commons" % "commons-collections4" % "4.0",
  "com.typesafe" % "config" % "1.3.2",
  "com.google.code.findbugs" % "jsr305" % "3.0.0",
  "com.cedarsoftware" % "java-util" % "1.8.0",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.apache.commons" % "commons-collections4" % "4.0",
  "com.beust" % "jcommander" % "1.72",
  "junit" % "junit" % "4.8.1",
  "io.atomix.copycat" % "copycat-server" % "1.1.4",
  "io.atomix.copycat" % "copycat-client" % "1.1.4",
  "io.atomix.catalyst" % "catalyst-netty" % "1.1.1",
  "net.jcip" % "jcip-annotations" % "1.0",
  "org.fusesource.jansi" % "jansi" % "1.16",

  "org.apache.kafka" % "kafka_2.12" % "0.11.0.2",

  "com.typesafe.akka" %% "akka-actor" % "2.5.8",
  "com.typesafe.akka" %% "akka-stream" % "2.5.8"


)