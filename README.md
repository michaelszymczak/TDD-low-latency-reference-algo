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