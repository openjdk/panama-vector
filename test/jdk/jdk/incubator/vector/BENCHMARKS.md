Benchmarks generated to test/micro/org/openjdk/bench/jdk/incubator/vector/operation

Run benchmarks from top dir level using:

make test TEST="micro:<benchmark-name>" MICRO="FORK=2;WARMUP_ITER=5;" CONF=linux-x86_64-server-release

example:
make test TEST="micro:Int64Vector" MICRO="FORK=2;WARMUP_ITER=5;" CONF=linux-x86_64-server-release