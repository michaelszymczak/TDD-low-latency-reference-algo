To run:

    make

To perQueue:

    make perfQueue

To generate flame graphs

    https://github.com/jvm-profiling-tools/async-profiler/releases
    ./profiler.sh -d 30 -f cpu-flame-graph.svg --title "CPU profile" --width 2000 $(pgrep -f TddRefAlgoMain)
    ./profiler.sh -e alloc -d 30 -f alloc-flame-graph3.svg --title "Allocation profile" --width 2000  $(pgrep -f TddRefAlgoMain)
