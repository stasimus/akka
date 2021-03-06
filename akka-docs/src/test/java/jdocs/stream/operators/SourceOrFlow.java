/**
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package jdocs.stream.operators;

import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

//#zip-with-index
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import java.util.Arrays;

//#zip-with-index
//#log
import akka.stream.Attributes;
import akka.stream.javadsl.Source;
//#log

import java.time.Duration;
import java.util.Arrays;


class SourceOrFlow {

  void logExample() {
    Flow.of(String.class)
        //#log
        .log("myStream")
        .addAttributes(Attributes.createLogLevels(
            Attributes.logLevelOff(), // onElement
            Attributes.logLevelError(), // onFailure
            Attributes.logLevelInfo())) // onFinish
    //#log
    ;
  }

  void zipWithIndexExample() {
    Materializer materializer = null;
    //#zip-with-index
    Source.from(Arrays.asList("apple", "orange", "banana"))
            .zipWithIndex()
            .runWith(Sink.foreach(System.out::print), materializer);
    // this will print ('apple', 0), ('orange', 1), ('banana', 2)
    //#zip-with-index
  }
  
  void conflateExample() {
    //#conflate
    Source.cycle(() -> Arrays.asList(1, 10, 100).iterator())
        .throttle(10, Duration.ofSeconds(1)) // fast upstream
        .conflate((Integer acc, Integer el) -> acc + el)
        .throttle(1, Duration.ofSeconds(1)); // slow downstream
    //#conflate
  }

  static //#conflateWithSeed-type
  class Summed {

    private final Integer el;

    public Summed(Integer el) {
      this.el = el;
    }

    public Summed sum(Summed other) {
      return new Summed(this.el + other.el);
    }
  }
  //#conflateWithSeed-type

  void conflateWithSeedExample() {
    //#conflateWithSeed

    Source.cycle(() -> Arrays.asList(1, 10, 100).iterator())
        .throttle(10, Duration.ofSeconds(1)) // fast upstream
        .conflateWithSeed(Summed::new, (Summed acc, Integer el) -> acc.sum(new Summed(el)))
        .throttle(1, Duration.ofSeconds(1)); // slow downstream
    //#conflateWithSeed
  }
}
