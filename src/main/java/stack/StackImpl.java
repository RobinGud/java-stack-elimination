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

    final int ITERATION_AROUND = 1;
    final int ARRAY_SIZE = 60;
    final int WAITING = 20;
    final Random rnd = new Random(0);
    private AtomicRef<Node> head = new AtomicRef<>(null);
    private List<AtomicRef<Integer>> eliminationArray = Collections.nCopies(ARRAY_SIZE, new AtomicRef<Integer>(null));

    @Override
    public void push(int x) {
        int randomIndex = rnd.nextInt(ARRAY_SIZE);


        for (int j = Math.max(0, randomIndex - ITERATION_AROUND); j < Math.min(ARRAY_SIZE, randomIndex + ITERATION_AROUND); j++) {
            Integer coatedX = x;
            if (eliminationArray.get(j).compareAndSet(null, coatedX)) {

                for (int i = 0; i < WAITING; i++) {
                    Integer value = eliminationArray.get(j).getValue();
                    if (value == null || ((int) value) != x) {
                        return;
                    }
                }
            }

            if (!eliminationArray.get(j).compareAndSet(coatedX, null)) {
                return;
            }
            break;
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
        int randomIndex = rnd.nextInt(ARRAY_SIZE);


        for (int j = Math.max(0, randomIndex - ITERATION_AROUND); j < Math.min(ARRAY_SIZE, randomIndex + ITERATION_AROUND); j++) {
            Integer value = eliminationArray.get(randomIndex).getValue();
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
