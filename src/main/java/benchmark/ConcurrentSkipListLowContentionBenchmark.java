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
public class ConcurrentSkipListLowContentionBenchmark {
    private ConcurrentSkipListSet<Integer> list;

    public ConcurrentSkipListLowContentionBenchmark() {
        this.list = new ConcurrentSkipListSet<Integer>();
    }

    @State(Scope.Thread)
    public static class OperationState{
        public OperationGenerator opGen;

        public OperationState() {
            this.opGen = new OperationGenerator(10000);
        }
    }

    @Benchmark
    @Fork(1)
    @Threads(32)
    public void mostlyAddRemove(OperationState state){
        switch (state.opGen.nextOperation(0.4,0.4,0.1)){
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
    public void equalMix(OperationState state){
        switch (state.opGen.nextOperation(0.25,0.25,0.25)){
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
    public void mostlyContains(OperationState state){
        switch (state.opGen.nextOperation(0.1,0.01,0.5)){
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
    public void mostlyRange(OperationState state){
        switch (state.opGen.nextOperation(0.1,0.01,0.1)){
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
