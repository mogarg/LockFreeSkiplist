package benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Mohit on 04/12/16.
 */

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class ConcurrentSkipListAllRangeBenchmark {
    private ConcurrentSkipListSet<Integer> list;

    public ConcurrentSkipListAllRangeBenchmark() {
        this.list = new ConcurrentSkipListSet<Integer>();
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
    public void twoThreads(OperationState state) {
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
    public void fourThreads(OperationState state) {
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
    public void eightThreads(OperationState state) {
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
    public void sixteenThreads(OperationState state) {
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
    public void thirtyTwoThreads(OperationState state) {
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
    public void sixtyFourThreads(OperationState state) {
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
