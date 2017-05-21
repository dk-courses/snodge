Snodge
======

A small, extensible Kotlin library to randomly mutate JSON documents. Useful for fuzz testing.

Examples of things you can test by mutating known good JSON documents:

- unexpected JSON structures will not make your application code throw unchecked exceptions
- your application code ignores additional fields
- your application code does not throw unchecked exceptions when parsing values from JSON strings
- your application does not instantiate arbitrary classes named in JSON data (a potential security risk)
- and more!


In a Nutshell
-------------

Output 10 random mutations of the JSON document:

~~~~~~~~~~~~~~~~~~~~~~kotlin
val random = Random()
val originalJson = "{\"x\": \"hello\", \"y\": [1,2,3]}"

random.mutants(allJsonMutagens().forStrings(), 10, originalJson)
    .forEach(::println)
~~~~~~~~~~~~~~~~~~~~~~

Example output:

~~~~~~~~~~~~~~~~~~~~~~
{"x":"hello","y":[1,2,3,null]}
{"y":[1,2,3],"x":{}}
{"x":"hello","y":[2,3]}
{"x":"hello","y":[{},2,3]}
{"x":"hello"}
{"x":"hello","y":[1,2,{}]}
{"x":"hello","y":[1,null,3]}
{"y":[1,2,3],"x":"hello"}
{"y":[1,2,3],"x":"a string"}
{"x":"hello","y":[99,2,3]}
~~~~~~~~~~~~~~~~~~~~~~

Other versions
--------------

When released, this will be version 3.x.x.x.  

Previous versions:

- Version 2.x.x.x (java8 branch) is for Java 8, and uses streams and Java 8 function types
- Version 1.x.x.x (master branch) is for Java 7 and depends on Guava

[Download from Maven Central](http://mvnrepository.com/artifact/com.natpryce/snodge)



To build
--------

* JDK 8
* Kotlin 1.1
* GNU Make
