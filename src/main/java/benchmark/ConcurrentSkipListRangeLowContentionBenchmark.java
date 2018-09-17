
package benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import java.util.concurrent.ConcurrentSkipListSet;


@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class ConcurrentSkipListRangeLowContentionBenchmark {

    private ConcurrentSkipListSet<Integer> list;

    public ConcurrentSkipListRangeLowContentionBenchmark() {
        this.list = new ConcurrentSkipListSet<Integer>();
    }

    @State(Scope.Thread)
    public static class OperationState {

        public OperationGenerator opGen;

        public OperationState() {
            this.opGen = new OperationGenerator(0.1, 0.01, 0.1, 10000);
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(2)
    public void twoThreads(LFSkipListEqualRatioLowContentionBenchmark.OperationState state) {
        switch (state.opGen.nextOperation()) {
            case ADD:
                list.add(state.opGen.nextValue());
                break;
            case REMOVE:
                list.remove(state.opGen.nextValue());
                break;
            case CONTAINS:
                list.contains(state.opGen.nextValue());
                break;
            default:
                list.subSet(state.opGen.nextRange().getStart(),state.opGen.nextRange().getEnd());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(4)
    public void fourThreads(LFSkipListEqualRatioLowContentionBenchmark.OperationState state) {
        switch (state.opGen.nextOperation()) {
            case ADD:
                list.add(state.opGen.nextValue());
                break;
            case REMOVE:
                list.remove(state.opGen.nextValue());
                break;
            case CONTAINS:
                list.contains(state.opGen.nextValue());
                break;
            default:
                list.subSet(state.opGen.nextRange().getStart(),state.opGen.nextRange().getEnd());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(8)
    public void eightThreads(LFSkipListEqualRatioLowContentionBenchmark.OperationState state) {
        switch (state.opGen.nextOperation()) {
            case ADD:
                list.add(state.opGen.nextValue());
                break;
            case REMOVE:
                list.remove(state.opGen.nextValue());
                break;
            case CONTAINS:
                list.contains(state.opGen.nextValue());
                break;
            default:
                list.subSet(state.opGen.nextRange().getStart(),state.opGen.nextRange().getEnd());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(16)
    public void sixteenThreads(LFSkipListEqualRatioLowContentionBenchmark.OperationState state) {
        switch (state.opGen.nextOperation()) {
            case ADD:
                list.add(state.opGen.nextValue());
                break;
            case REMOVE:
                list.remove(state.opGen.nextValue());
                break;
            case CONTAINS:
                list.contains(state.opGen.nextValue());
                break;
            default:
                list.subSet(state.opGen.nextRange().getStart(),state.opGen.nextRange().getEnd());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(32)
    public void thirtyTwoThreads(LFSkipListEqualRatioLowContentionBenchmark.OperationState state) {
        switch (state.opGen.nextOperation()) {
            case ADD:
                list.add(state.opGen.nextValue());
                break;
            case REMOVE:
                list.remove(state.opGen.nextValue());
                break;
            case CONTAINS:
                list.contains(state.opGen.nextValue());
                break;
            default:
                list.subSet(state.opGen.nextRange().getStart(),state.opGen.nextRange().getEnd());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(64)
    public void sixtyFourThreads(LFSkipListEqualRatioLowContentionBenchmark.OperationState state) {
        switch (state.opGen.nextOperation()) {
            case ADD:
                list.add(state.opGen.nextValue());
                break;
            case REMOVE:
                list.remove(state.opGen.nextValue());
                break;
            case CONTAINS:
                list.contains(state.opGen.nextValue());
                break;
            default:
                list.subSet(state.opGen.nextRange().getStart(),state.opGen.nextRange().getEnd());
        }
    }
}