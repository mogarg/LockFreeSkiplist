

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;
import range.IntegerRange;
import skiplist.RSkipList;
import java.util.ArrayList;
import java.util.Collections;
import skiplist.R2SkipList;

//@RunWith(ConcurrentTestRunner.class)
public class R2SkipListTest extends MultithreadedTestCase {

    private R2SkipList<Integer> list;
    ArrayList<Integer> s = new ArrayList<Integer>();

    public R2SkipListTest() {
        this.list = new R2SkipList<>(6);
    }

    public void thread1() throws InterruptedException {

        for (int i=40;i<50;i++){
            list.add(i);
            assertTrue(list.contains(i));
        }
        waitForTick(2);
        list.remove(17);

        waitForTick(4);
        list.add(81);

        waitForTick(6);
        list.add(48);
        list.add(50);
    }

    public void thread2() throws InterruptedException {
        list.add(80);

        waitForTick(1);
        list.add(17);
        list.remove(42);

        waitForTick(4);
        list.remove(80);

        waitForTick(6);
        list.add(49);
        list.add(51);
        list.add(50);
    }

    public void thread3() throws InterruptedException {
        waitForTick(3);
        assertFalse(list.contains(17));
        assertTrue(list.contains(80));
        assertFalse(list.contains(42));

        waitForTick(5);
        assertTrue(list.contains(81));
        assertFalse(list.contains(80));

        waitForTick(7);
        Collections.addAll(s,45,46,47,48);
        assertEquals(list.getRange(new IntegerRange(45, 49)), s);
    }


    @Test
    public void test() throws Throwable{
        TestFramework.runOnce(new RSkipListTest());
    }

}
