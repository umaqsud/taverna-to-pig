# Taverna to Pig Compiler

Compiling Taverna Workflows to Apache Pig Programs

## Introduction

Taverna is an open source and domain-independent Workflow Management System – a suite of tools used to design and execute scientific workflows and aid in silico experimentation. In order to be able to easily scale workflows created with Taverna, this compiler automatically generates a pig script file that can be executed on a Hadoop Cluster.

For more information see https://pig.apache.org/ or http://www.taverna.org.uk/

## Usage

This will compile the taverna workflow and generate the pig script and configuration files in the output path.

```
java -cp target/taverna-to-pig-1.0-SNAPSHOT.jar edu.tuberlin.dima.taverna_to_pig.main.TavernaToPigMain \
-i path_to_taverna_workflow -o path_to_output
```

To run the pig script in local mode, go inside the `path_to_output` use the predefined shell script.

```
sudo chmod +x run.sh
./run.sh
```

## Extending the Compiler

Currently, the compiler supports workflows with any number of `Local Tool Incocations` or `XPath Services` processors, which can be combined in a linear fashion. In the future it shall be possible to extend the compiler easily in order to incorporate further Taverna features.


