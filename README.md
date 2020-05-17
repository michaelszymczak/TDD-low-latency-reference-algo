## To run:

    make

## To perQueue:

    make perfQueue

## To generate flame graphs

    sudo sysctl -w kernel.perf_event_paranoid=1
    sudo sysctl -w kernel.kptr_restrict=0

    https://github.com/jvm-profiling-tools/async-profiler/releases
    ./profiler.sh -d 30 -f cpu-flame-graph.svg --title "CPU profile" --width 2000 $(pgrep -f TddRefAlgoMain)
    ./profiler.sh -e alloc -d 30 -f alloc-flame-graph3.svg --title "Allocation profile" --width 2000  $(pgrep -f TddRefAlgoMain)


## To measure allocations

Look at the example flame graphs in docs/

I should take up to a minute to run in various modes. It will open a firefox (why not) with the results

    ./run_allocations

More resources re allocation:

https://blogs.oracle.com/jonthecollector/the-real-thing

https://shipilev.net/jvm/anatomy-quarks/4-tlab-allocation/

https://github.com/iovisor/bpftrace

To do things such as:

`sudo bpftrace -e "u:$LIBJVM:_ZN15G1CollectedHeap22humongous_obj_allocateEm { @[ustack] = count() }"`


### Use BBF tools to measure allocations.


WARNING: Do noy use this in production, the ExtendedDTraceProbes flag significantly slows down the app.

1. Install the tools

`sudo apt install bpftrac bpfcc-tools`

2. Check java version:

`echo $JAVA_HOME`

Example output: /usr/lib/jvm/java-8-openjdk-amd64

3. Check if the version of JVM has all required probes (if you distribution is compiled with --enable-dtrace etc.)

`find "${JAVA_HOME}" -name libjvm.so -exec readelf -n {} +| grep -A2 NT_STAPSDT | wc -l`
Example ouptut: 2247

If is 0, it means that this distro is of no use, find more useful one or compile one yourself.


You can install a version that supports it

```
wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | sudo apt-key add -
sudo add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/
# run sudo apt-get install -y software-properties-common if the command above not found
sudo apt-get install adoptopenjdk-11-hotspot

# after installation standard things, such as 'sudo update-alternatives --config java' or 'export JAVA_HOME ...'
```


4. Build and run a sample app

```
./gradlew clean distZip && unzip -q build/distributions/TDD-low-latency-reference-algo.zip -d build/distributions                                                                     
JAVA_OPTS="-XX:+PrintTLAB -XX:+PreserveFramePointer -XX:+ExtendedDTraceProbes" ./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfPricerAlloc
```

5. While running

`sudo uobjnew -l java `pgrep -f TddRefAlgoMain` 5`

Sample output:

Ignore Bytes - it does not allocation size reporting does not work properly, at least in my version built 2018-10-09).

```
Tracing allocations in process 21381 (language: java)... Ctrl-C to quit.

NAME/TYPE                      # ALLOCS      # BYTES

NAME/TYPE                      # ALLOCS      # BYTES
b'[C'                                 3            0
b'java/nio/HeapCharBuffer'            2            0
b'java/lang/StringBuilder'            1            0
b'java/lang/String'                   1            0

NAME/TYPE                      # ALLOCS      # BYTES

NAME/TYPE                      # ALLOCS      # BYTES

NAME/TYPE                      # ALLOCS      # BYTES
b'[C'                                 3            0
b'java/nio/HeapCharBuffer'            2            0
b'java/lang/StringBuilder'            1            0
b'java/lang/String'                   1            0

```

As you can see, it can detect even infrequent allocations


### Available probes

When the java app is running:

`tplist-bpfcc -p `pgrep -f TddRefAlgoMain`


## Sample results for perfPricer
Current results

```
$ make perfPricer

BUILD SUCCESSFUL in 1s
5 actionable tasks: 5 executed
run with ./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfPricer
--------------------------
Reference implementation latency measurement
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Warm up complete (3000000 iterations took 1.93s)
-------------------------------- BENCHMARK RESULTS (RUN 1) --------------------------------------------------------
Run time: 10.0s
Correcting for co-ordinated:true
Target throughput:100000/s = 1 message every 10us
End to End: (1,000,000)                         50/90 99/99.9 99.99/99.999 - worst was 0.33 / 0.91  23 / 96  113 / 143 - 176
OS Jitter (11,434)                              50/90 99/99.9 99.99 - worst was 9.5 / 11  16 / 58  160 - 201
-------------------------------------------------------------------------------------------------------------------
-------------------------------- BENCHMARK RESULTS (RUN 2) --------------------------------------------------------
Run time: 10.0s
Correcting for co-ordinated:true
Target throughput:100000/s = 1 message every 10us
End to End: (1,000,000)                         50/90 99/99.9 99.99/99.999 - worst was 0.31 / 0.91  24 / 96  121 / 160 - 193
OS Jitter (10,486)                              50/90 99/99.9 99.99 - worst was 9.5 / 11  16 / 29  76 - 121
-------------------------------------------------------------------------------------------------------------------
-------------------------------- BENCHMARK RESULTS (RUN 3) --------------------------------------------------------
Run time: 10.0s
Correcting for co-ordinated:true
Target throughput:100000/s = 1 message every 10us
End to End: (1,000,000)                         50/90 99/99.9 99.99/99.999 - worst was 0.31 / 0.88  22 / 96  113 / 129 - 143
OS Jitter (9,771)                               50/90 99/99.9 99.99 - worst was 9.5 / 10  11 / 22  68 - 68
-------------------------------------------------------------------------------------------------------------------
-------------------------------- BENCHMARK RESULTS (RUN 4) --------------------------------------------------------
Run time: 10.0s
Correcting for co-ordinated:true
Target throughput:100000/s = 1 message every 10us
End to End: (1,000,000)                         50/90 99/99.9 99.99/99.999 - worst was 0.31 / 0.88  22 / 96  109 / 129 - 135
OS Jitter (9,910)                               50/90 99/99.9 99.99 - worst was 9.5 / 11  14 / 46  100 - 100
-------------------------------------------------------------------------------------------------------------------
-------------------------------- SUMMARY (end to end)------------------------------------------------------------
Percentile   run1         run2         run3         run4      % Variation
50:             0.33         0.31         0.31         0.31         0.00
90:             0.91         0.91         0.88         0.88         2.37
99:            23.04        24.06        22.02        22.02         5.84
99.9:          96.26        96.26        96.26        96.26         0.00
99.99:        112.64       120.83       112.64       108.54         7.02
worst:        143.36       159.74       129.02       129.02        13.70
-------------------------------------------------------------------------------------------------------------------

```


## Use BPF trace

Example run for Java8

`JAVA_OPTS="-XX:+PrintTLAB -XX:+PrintGC -XX:+PreserveFramePointer -XX:+ExtendedDTraceProbes" ./build/distributions/TDD-low-latency-reference-algo/bin/TDD-low-latency-reference-algo perfPricerAlloc`

Print all java events as they occur

`sudo bpftrace -e 'uprobe:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/server/libjvm.so:*,uprobe:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/libjava.so:* { printf("%s\n", probe); }'`

