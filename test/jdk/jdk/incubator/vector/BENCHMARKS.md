% Benchmarking Vector API

Benchmarks are generted from scripts in this directory to `test/micro/org/openjdk/bench/jdk/incubator/vector/operation`

## Test selection
Run benchmarks from the top level git dir using:

``` shell
make test TEST="micro:<benchmark-name>" CONF=linux-x86_64-server-release

### One Test
make test TEST="micro:Int64Vector" CONF=linux-x86_64-server-release

### Run all tests -- WARNING requires ~4-5 hours
make test TEST="micro:org.openjdk.bench.jdk.incubator.vector" CONF=linux-x86_64-server-release
```

### JMH Configuration
See `https://github.com/openjdk/jdk/blob/master/doc/testing.md` or `doc/testing.md` dir in this git repo for more detailed steps on running the benchmarks for Vector API at `test/micro/org/openjdk/bench/jdk/incubator/vector/operation`.

From doc/testing.md:
To be able to run microbenchmarks, `configure` needs to know where to find the
JMH dependency. Use `--with-jmh=<path to JMH jars>` to point to a directory
containing the core JMH and transitive dependencies. The recommended
dependencies can be retrieved by running `sh make/devkit/createJMHBundle.sh`,
after which `--with-jmh=build/jmh/jars` should work.
