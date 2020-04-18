package ch.epfl.rigelTest.math.graphsAndTrees;

import ch.epfl.rigel.math.graphs.Node;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeTest {

    private static Tree<Integer> intTree;
    private static Node<Integer> root, child1, child2, child11, child12;

    @BeforeAll
    static void init() {
        root = new Node<>(0);
        child1 = root.createChild(2);
        child2 = root.createChild(1);
        child11 = child1.createChild(4);
        child12 = child1.createChild(3);
        intTree = new Tree<>(new MathSet<>(root, child1, child2, child11, child12));
    }

    @Test
    void flowTest() {
        OrderedSet<Node<Integer>> set = intTree.flow(Comparator.comparingInt(Node::getValue),root);
        for (Node<Integer> node : set) {
            System.out.println(node.getValue());
        }
    }
}
