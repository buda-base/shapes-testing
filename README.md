# shapes-testing
We put various test apps and harnesses and so on for working w/ topquadrant-shacl and jena-shacl

All test apps should be standlone except of course for necessary topquadrant or jena libs. 

We need a directory of shape files and resource files such as P707 and so on

### Testing ontmanager in jar

```
mvn clean package
cp target/shapes-testing-0.0.1-SNAPSHOT.jar /tmp/test.jar
java -jar /tmp/test.jar
```