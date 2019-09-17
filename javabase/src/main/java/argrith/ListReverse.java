package argrith;

import java.util.Arrays;
import java.util.List;

/**
 * @Author:qishan
 * @Date: 2019/8/27
 * @Desc:
 **/
public class ListReverse {
    public static void main(String[] args) {
        Node node = build(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        toString(node);
        System.out.println("reverse begin");
        Node reverse = reverseRecursion(node);
        toString(reverse);
        System.out.println("reverse no recursion begin");
        Node nr = reverseNoRecursion(reverse);
        toString(nr);
    }

    public static Node build(List<Integer> datas) {
        Node head = new Node(datas.get(0));
        Node origin = head;
        for (int i = 1; i < datas.size(); i++) {
            Node n = new Node(datas.get(i));
            head.next = n;
            head = n;
        }
        return origin;
    }

    public static void toString(Node head) {
        do {
            System.out.print(head + "==>");
            head = head.next;
        } while (head != null);
        System.out.println();
    }

    public static Node reverseNoRecursion(Node current) {
        //initialization
        Node previousNode = null;
        Node nextNode = null;
        while (current != null) {
            //save the next node
            nextNode = current.next;
            //update the value of "next"
            current.next = previousNode;
            //shift the pointers
            previousNode = current;
            current = nextNode;
        }
        return previousNode;
    }

    public static Node reverseRecursion(Node head) {
        if (head == null || head.next == null) {
            return head;
        } else {
            Node newHead = reverseRecursion(head.next);
            head.next.next = head;
            head.next = null;
            return newHead;
        }
    }

    static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Node{");
            sb.append("data=").append(data);
            sb.append('}');
            return sb.toString();
        }
    }
}
