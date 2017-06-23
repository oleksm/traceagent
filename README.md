# traceagent
java agent to trace, troubleshoot, debug and do simple modifications in java code


## Config
Modify your [config.yaml](config.yaml "config.yaml"):
```yaml
debug: false # will print lots of debug information about instrumentation
classes:
  - 
    crex: .+ # regex to find target class
    methods:
    -  
      mrex: .+ # regex to find target method within a class
      addLocalVariable: # adds local variable into a method 
        - long startTime # variable example
      insertBefore: # add line of code at the top of method body. Can access method arguments
        - startTime = System.nanoTime(); # example
      insertAfter: # add line of code at the bottom of method body. Can access method arguments
        - System.out.println(<<clazz>>.class.getName() + ".<<method>> Execution Duration (nano sec) "+ (System.nanoTime() - startTime) ); # example of eol code
#tbd      insertAt: 
#        - 1: false, System.out.println("insertAt 1)"
```
###Macro Params
macros may be useful if class and or method information need to be injected into code:

* clazz - class full path
* method - method name

## Build
I use two tasks:
1. gradle eclipse run - to check real quick and update eclipse dependencies
2. gradle fatJar - to package everything into a single jar. Config should place outside.

## Run
```shell
java -javaagent:traceagent-all-1.0.jar=config.yaml -jar <<rest of jvm args>>
```

