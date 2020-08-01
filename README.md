
# Bric

Bric is a cross-platform batch image editor. Its key features are convert, resize, rotate and add watermark to images and PDF files. Bric has a great strength in handling multiple file types for input and output.

*Note*: PDF files are handled as a sequence of images

## Project status

The development started back in 2011 and the project was maintened for several years, gaining popularity on [sourceforge](https://sourceforge.net/projects/bric/). The original name was BIRSN and was quickly renamed to its current easier pronounceable name. As of 2020 the project is back in active (slow paced) development.

## Running

How to get the latest version and run the program on your machine.

### Prerequisites

This project is written in Java and its executable is in jar format. In order to run the jar file you must have installed on your machine the [Java SE](https://www.oracle.com/java/technologies/javase-downloads.html) 8 or later.

### Run

The latest version as well as all the previous once are available at [sourceforge](https://sourceforge.net/projects/bric/). To run it either double click or run `java -jar bric-x.x.x.jar` on a terminal/command prompt.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See **Running** for notes on how to run the project on a live system.

### Prerequisites

What things you need to install and how to install them
```
JDK 8
Maven
```
It is recommended to us the [sdkman](https://sdkman.io/) to install all of the above. 

### Test your setup

A step by step series of examples that tell you how to get a development env running

* Open the maven project with your favorite IDE
* And click Run

or

* Run on a terminal/command prompt `mvn clean exec:java`

## Running the tests

Tests are separated into two groups, the *unit* and the *integration*. Integration tests

### Unit tests

Unit tests isolate single components from the system and test them one by one without having to access any external resources. They should be run frequently, to make sure that all the components run as expected during code interventions.

```
mvn clean test
```

### Integration tests

Integration tests are testing the communication between components, to do that they utilize test resources such as test image and PDF files packaged into the project. Thus integration tests are much slower. 

```
mvn clean integration-test
```

## Coding style

The whole project is using the default coding style by IntelliJ IDE.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Swing](https://docs.oracle.com/javase/8/docs/technotes/guides/swing/) - Used for the GUI

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use a relatively random way for versioning, hopefully this will get standardized on the upcoming releases. For the versions available, see the [tags on this repository](https://github.com/vnaskos/bric/tags). 

## Authors

* **Vasilis Naskos** - *Initial work* - [vnaskos](https://vnaskos.com)

See also the list of [contributors](https://github.com/vnaskos/bric/contributors) who participated in this project.

## License

This project is licensed under the GPLv3 License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
