package ch.epfl.rigelTest.math.graphsAndTrees;

import ch.epfl.rigel.math.graphs.Node;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.OrderedTuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeTest {

    private static Tree<Integer> intTree;
    private static Node<Integer> root, child1, child2, child11, child12, child21;

    @BeforeAll
    static void init() {
        root = new Node<>(0);
        child1 = root.createChild(2);
        child2 = root.createChild(1);
        child21 = child2.createChild(5);
        child11 = child1.createChild(4);
        child12 = child1.createChild(3);
        intTree = new Tree<>(Node.bunk(root, child1, child2, child11, child12, child21));
        System.out.println(intTree);
    }

    @Test
    void construcThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
           new Tree<>(Node.bunk(new Node<>(5), new Node<>(6)));
        });
    }

    @Test
    void findPathBetweenTest() {
        System.out.println(intTree.findPathBetween(child21, child12).get().image(Node::getValue));
    }

    @Test
    void initialCardinalityTest() {
        assertEquals(6, intTree.cardinality());
    }

    @Test
    void partitions() {
        assertEquals(5, intTree.components().cardinality());
    }

    @Test
    void flowTest() {
        OrderedTuple<Node<Integer>> set = intTree.flow(Comparator.comparingInt(Node::getValue),root);
        assertEquals(Set.of(0,2,4), set.image(Node::getValue).getData());
    }

    @Test
    void rootHasMinimalDepth() {
        assertEquals(root, Collections.min(intTree.getData(), Comparator.comparingInt(Node::getDepth)));
    }

    @Test
    void edgeSetTest() {
        assertEquals(5, intTree.edgeSet().cardinality());
    }

    @Test
    void getAtDepthTest() {
        assertEquals(2, intTree.getNodesAtDepth(1).cardinality());
    }

    @Test
    void subTreeTest() {
        assertEquals(3, intTree.subtreeAtPoint(child1).cardinality());
    }
}
