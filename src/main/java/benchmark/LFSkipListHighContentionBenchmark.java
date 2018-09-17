/*
bench * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

import skiplist.LFSkipList;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class LFSkipListHighContentionBenchmark {

  private LFSkipList<Integer> list;

  public LFSkipListHighContentionBenchmark() {
    this.list = new LFSkipList<>(6);
  }

  @State(Scope.Thread)
  public static class OperationState {

    public OperationGenerator opGen;

    public OperationState() {
      this.opGen = new OperationGenerator(100);
    }
  }

  @Benchmark
  @Fork(1)
  @Threads(32)
  public void mostlyAddRemove(OperationState state) {
    switch (state.opGen.nextOperation(0.4, 0.4, 0.1)) {
    case ADD:
      list.add(state.opGen.nextValue());
      break;
    case REMOVE:
      list.remove(state.opGen.nextValue());
      break;
    default:
      list.contains(state.opGen.nextValue());
    }
  }

  @Benchmark
  @Fork(1)
  @Threads(32)
  public void equalMix(OperationState state) {
    switch (state.opGen.nextOperation(0.33, 0.33, 0.33)) {
    case ADD:
      list.add(state.opGen.nextValue());
      break;
    case REMOVE:
      list.remove(state.opGen.nextValue());
      break;
    default:
      list.contains(state.opGen.nextValue());
    }
  }

  @Benchmark
  @Fork(1)
  @Threads(32)
  public void mostlyContains(OperationState state) {
    switch (state.opGen.nextOperation(0.1, 0.01, 0.89)) {
    case ADD:
      list.add(state.opGen.nextValue());
      break;
    case REMOVE:
      list.remove(state.opGen.nextValue());
      break;
    default:
      list.contains(state.opGen.nextValue());
    }
  }
}
