# Scala Best Practices

## Motivation

Scala is a very flexible general purpose programming langue, with support for Objectum Oriented and Functional programming. It has a good type system to help with program composition but it also let programmers write code without using the capabilities of types.

This flexibility comes with trade-offs. Code not checked by the compiler usually checked at runtime leading to slower programs full with error handling logic, or in worst situations runtime exceptions.

Programs composed of pure not side effecting functions are easier to reason about and test but sometimes can lead to degraded performance.

In this tutorial i'll try to collect best practices and help the reader find the right balance navigating trough the capabilities of this expressive language.

## Axiome #1: No software is ever done

Start-up companies tend to fall into the "it works, lets move on to the next task" mentality during their development process. The problem with this is that software is never considered done, we developers spend most of our time on fixing bugs or writing new features for some existing software. We should keep our codebase clean, and readable.

Please repeat: "No software is ever done!"

## Axiome #2: Performance is secondary over readability

One can argue: making code readable often means composing it from small methods resulting in higher cpu stack usage, and wrapping values to objects resulting higher memory usage.

Lets be honest, it is true to some degree (in spite of the fact that the compiler and the JVM do a lot of optimizations for us). We usually end up with some degraded performance. The degradation usually can be expressed by a constant factor, and constant factors easy to price...

Ohohh, wait! Before I jump to the conclusion lets make some thought experiment and decide it yourself if readability or performance is more important from the business perspective.

Scenario 1: We have a highly optimized and fast web service, currently running on one server handling millions of requests per second. This service is also difficult to read because the design goal was to optimize it for performance as much as possible. On day you have to fix some bug in it. You spend days understanding the code, asking colleges for help, and finally the project ends up costing 2-3 times much man hours than planned.

Scenario 2: We have a not so optimized web service, currently running on 3 servers handling millions of requests per second. This service is easy to read because it was designed with maintainability in mind. On day you have to fix some bug in it. You spend 2 days on it and end up adding 1 more server to handle the same amount off traffic.

From business perspective which scenario do you think is better?

At the end of the day your company is selling some product. This product has to include all the man hours and operational costs plus some profit. What do you think what is easier to price? Development time when developers always say "it will be ready by the next sprint" or operational costs when developers say "it can be done, but it will take one more server to run"?

Please repeat: "Performance is secondary over readability!"

## Axiome #3: No-one can write good quality code without actively learning how to do it

Lets play a taught experiment again. You are a new developer introduced to a big software project, which also exhibits good design practices. You given a task to fix some bug. You fire up your IDE and even if you don't understand all the abstractions, you manage to find the pain point following the data flow in the system. At this point you are happy, you fix the bug and send it for code review. Your peers are finding possible bugs or code style issues and rejecting your code. You feel frustrated, but you learn just enough from it to get your code accepted and you move on to your next task.

You are in the rat race and the same thing happens over and over again. You possibly end up in one of the following situations.

* With time you learn just enough from the review comments and now everyone accepts your code because finally you cached up.
* Your peers are got bored of how much time they spend on reviewing your code and after a while they let your buggy code sneak in lowering the code quality of the whole project.
* You actively learn and share new concepts so everyone is happy to review your code because they know that it will be an easy task and they might also learn something.

And there are many other possibilities, I think you've got the point. It is up to you where will you end up, but one thing is for sure. No-one can write good quality code without actively learning how to do it.

Please share your knowledge and help improve this document!

## Write out return types

### Goal

The goal is to be able to quickly read trough an unknown codebase and get an idea about how data flows trough the methods, without digging into method implementations.

### Write out return types explicitly

When the return type is not the same what type you construct right after the equal sign.

```tut
import scala.util.Try

def divideByZero(x: Int):Option[Int] = Try{
  // Some silly computation
  x / 0
}.toOption
```

When the method is long and difficult to understand (btw you should avoid writing methods longer than 5 lines)

## Always return something specific, avoid Unit, Any, and AnyRef types

### Goal

We would like to be able to follow the data from the first entry to the system to the exit of it. If we do that
 * the compiler can help to explore bugs
 * wrong code won't compile so less testing is needed
 * it will be easier follow the data

### Why not to return Unit

Methods that return something can be composed together, allowing us to create complex programs out of simple building blocks.

### Why not to return Any or AnyRef

Similarly to Unit methods that return Any don't compose. The only way to use the output of Any in a new method is to pattern match on the possible types. The problem here is unlike pattern matching on Union types the compiler can't figure out if we missed a possible case.

```tut
sealed trait TrafficLight
case object Red extends TrafficLight
case object Yellow extends TrafficLight
case object Green extends TrafficLight

// Exhaustive pattern mach
def canIGoExhaustive(x: TrafficLight): String = x match {
  case Red => "Not yet"
  case Yellow => "Be ready"
  case Green => "Yes"
}

// Missing one case, compiler warns (or fails dependig on compiler parameters)
def canIGoPartial(x: TrafficLight): String = x match {
  case Red => "Not yet"
  case Yellow => "Be ready"
}

// No warning when pattern matching on Any
def canIGoAny(x: Any): String = x match {
  case Red => "Not yet"
  case Yellow => "Be ready"
}
```

### When should you return unit?

When you want to perform some side effect, tipicaly at the end of the data flow. For example writing to the database, or to the network.
