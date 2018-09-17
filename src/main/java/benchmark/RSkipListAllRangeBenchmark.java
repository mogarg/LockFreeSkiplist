package benchmark;

import org.openjdk.jmh.annotations.*;
import skiplist.RSkipList;

/**
 * Created by Mohit on 04/12/16.
 */

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class RSkipListAllRangeBenchmark {
    private RSkipList<Integer> list;

    public RSkipListAllRangeBenchmark() {
        this.list = new RSkipList<>(6);
    }

    @State(Scope.Thread)
    public static class OperationState {

        public OperationGenerator opGen;

        public OperationState() {
            this.opGen = new OperationGenerator(0, 0, 0, 100);
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(2)
    public void twoThreads(RSkipListRangeHighContentionBenchmark.OperationState state) {
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
                list.getRange(state.opGen.nextRange());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(4)
    public void fourThreads(RSkipListRangeHighContentionBenchmark.OperationState state) {
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
                list.getRange(state.opGen.nextRange());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(8)
    public void eightThreads(RSkipListRangeHighContentionBenchmark.OperationState state) {
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
                list.getRange(state.opGen.nextRange());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(16)
    public void sixteenThreads(RSkipListRangeHighContentionBenchmark.OperationState state) {
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
                list.getRange(state.opGen.nextRange());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(32)
    public void thirtyTwoThreads(RSkipListRangeHighContentionBenchmark.OperationState state) {
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
                list.getRange(state.opGen.nextRange());
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(64)
    public void sixtyFourThreads(RSkipListRangeHighContentionBenchmark.OperationState state) {
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
                list.getRange(state.opGen.nextRange());
        }
    }
}
