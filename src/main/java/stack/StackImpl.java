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

    final int ARRAY_SIZE = 1;
    final int WAITING = 1;
    final Random rnd = new Random();
    private AtomicRef<Node> head = new AtomicRef<>(null);
    private List<AtomicRef<Integer>> eliminationArray = Collections.nCopies(ARRAY_SIZE, new AtomicRef<Integer>(null));

    @Override
    public void push(int x) {
        int randomIndex = rnd.nextInt(ARRAY_SIZE);

        if (eliminationArray.get(randomIndex).compareAndSet(x, null)) {

            for (int i = 0; i < WAITING; i++) {
                Integer value = eliminationArray.get(randomIndex).getValue();
                if (value == null || value != x) {
                    return;
                }
            }
        }

        if (eliminationArray.get(randomIndex).compareAndSet(null, x)) {
            return;
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
        Integer value = eliminationArray.get(randomIndex).getValue();

        if (value != null && eliminationArray.get(randomIndex).compareAndSet(value, null)) {
            return value;
        }

        while (true) {
            Node currentHeadValue = head.getValue();

            if (currentHeadValue == null) {
                return Integer.MIN_VALUE;
            }

            if (head.compareAndSet(currentHeadValue, null)) {
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
