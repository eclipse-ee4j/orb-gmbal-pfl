This is the [gmbal-pfl project](https://github.com/eclipse-ee4j/orb-gmbal-pfl).

# Java EE Primitive Function Library
As of Java 9, pfl-basic produces a multi-release jar. The main code
is compiled in JDK 1.7 or 1.8, and additional code is compiled using Java 9. For this to work,
the code uses the toolchains plugin to select different compilers. You will need
a toolchains.xml in your .m2 directory.

See <https://maven.apache.org/guides/mini/guide-using-toolchains.html>

To build:
mvn clean install

To test:
mvn test

TODO: TfTest us failing, and is excluded in pom file. Fix it and add it back. 

Tests in error: 
  twoAnnotations(tf.TfTest)
  testSync(tf.TfTest): Could not initialize class tf.TfTest
  singleMethodInfoCall(tf.TfTest): Could not initialize class tf.TfTest
  singleMethodThrowsException(tf.TfTest): Could not initialize class tf.TfTest
  singleMethodNoReturn(tf.TfTest): Could not initialize class tf.TfTest
  singleMethodReturn(tf.TfTest): Could not initialize class tf.TfTest
  testSimple(tf.TfTest): Could not initialize class tf.TfTest
  twoCalls(tf.TfTest): Could not initialize class tf.TfTest
  twoCallsException(tf.TfTest): Could not initialize class tf.TfTest


## Releasing

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn -Psite verify site site:stage scm-publish:publish-scm
```

====================================================
