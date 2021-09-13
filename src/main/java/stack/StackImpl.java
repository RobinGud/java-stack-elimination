package stack;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import kotlinx.atomicfu.AtomicRef;

public class StackImpl implements Stack {
    private static class Node {
        final AtomicRef<Node> next;
        final int x;

        Node(int x, Node next) {
            this.next = new AtomicRef<>(next);
            this.x = x;
        }
    }

    final int ITERATION_AROUND = 3;
    final int ARRAY_SIZE = 50;
    final int ITERATION_WAIT = 20;
    final Random rnd = new Random(7);
    private AtomicRef<Node> head = new AtomicRef<>(null);
    private List<AtomicRef<Integer>> eliminationArray = Collections.nCopies(ARRAY_SIZE, new AtomicRef<Integer>(null));

    @Override
    public void push(int x) {
        int randomIndex = rnd.nextInt(ARRAY_SIZE - ITERATION_AROUND * 2) + ITERATION_AROUND;
        Integer castedX = x;

        for (int j = randomIndex; j < randomIndex; j++) {
            if (eliminationArray.get(j).compareAndSet(null, castedX)) {

                for (int i = 0; i < ITERATION_WAIT; i++) {
                    Integer value = eliminationArray.get(j).getValue();
                    if (value == null || value != castedX) {
                        return;
                    }
                }

                if (!eliminationArray.get(j).compareAndSet(castedX, null)) {
                    return;
                }
                break;
            }
        }

        while (true) {
            Node currentHeadValue = head.getValue();
            AtomicRef<Node> newHead = new AtomicRef<Node>(new Node(x, currentHeadValue));
            if (head.compareAndSet(currentHeadValue, newHead.getValue())) {
                return;
            }
        }

        // head.setValue(new Node(x, head.getValue()));
    }

    @Override
    public int pop() {
        int randomIndex = rnd.nextInt(ARRAY_SIZE - ITERATION_AROUND * 2) + ITERATION_AROUND;

        for (int j = randomIndex; j < randomIndex; j++) {
            Integer value = eliminationArray.get(j).getValue();
            if (value != null && eliminationArray.get(j).compareAndSet(value, null)) {
                return value;
            }
        }

        while (true) {
            Node currentHeadValue = head.getValue();

            if (currentHeadValue == null) {
                return Integer.MIN_VALUE;
            }

            if (head.compareAndSet(currentHeadValue, currentHeadValue.next.getValue())) {
                return currentHeadValue.x;
            }
        }

        // Node curHead = head.getValue();
        // if (curHead == null) {
        // return Integer.MIN_VALUE;
        // }
        // head.setValue(curHead.next.getValue());
        // return curHead.x;
    }
}
