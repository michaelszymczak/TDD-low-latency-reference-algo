To run:

    make

To perQueue:

    make perfQueue

To generate flame graphs

    sudo sysctl -w kernel.perf_event_paranoid=1
    sudo sysctl -w kernel.kptr_restrict=0

    https://github.com/jvm-profiling-tools/async-profiler/releases
    ./profiler.sh -d 30 -f cpu-flame-graph.svg --title "CPU profile" --width 2000 $(pgrep -f TddRefAlgoMain)
    ./profiler.sh -e alloc -d 30 -f alloc-flame-graph3.svg --title "Allocation profile" --width 2000  $(pgrep -f TddRefAlgoMain)


To measure allocations in simple throttled prices


    make perfPricer
    ./profiler.sh -e alloc -d 30 -f simple-pricer-alloc-flame-graph3.svg --title "Allocation profile" --width 2000  $(pgrep -f TddRefAlgoMain)

Current results

```
$ make perfPricer

BUILD SUCCESSFUL in 1s
5 actionable tasks: 5 executed
run with ./build/distributions/tdd-reference-algo/bin/tdd-reference-algo perfPricer
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
