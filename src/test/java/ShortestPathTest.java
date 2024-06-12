import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShortestPathTest {


  @Before
  public void setUp() throws Exception {
    System.out.println("=============测试开始=============");
  }

  @After
  public void tearDown() throws Exception {
    System.out.println("=============测试结束=============");
  }

  @Test
  public void testValidGraphWithPath() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    Node nodeC = new Node("C");
    Node nodeD = new Node("D");
    graph.addEdge(nodeA, nodeB);
    graph.addEdge(nodeA, nodeD);
    graph.addEdge(nodeB, nodeC);
    graph.addEdge(nodeB, nodeC);
    graph.addEdge(nodeB, nodeC);
    graph.addEdge(nodeD, nodeC);
    graph.addEdge(nodeD, nodeC);
    assertEquals("A->D->C\n最短路径长度为：3", Main.calcShortestPath(graph, "A", "C"));
  }

  @Test
  public void testValidGraphSingleEdge() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    graph.addEdge(nodeA, nodeB);
    assertEquals("A->B\n最短路径长度为：1", Main.calcShortestPath(graph, "A", "B"));
  }

  @Test
  public void testNullGraph() {
    assertNull(Main.calcShortestPath(null, "A", "B"));
  }

  @Test
  public void testStartNodeNotExist() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    graph.addEdge(nodeA, nodeB);
    assertNull(Main.calcShortestPath(graph, "X", "B"));
  }

  @Test
  public void testEndNodeNotExist() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    graph.addEdge(nodeA, nodeB);
    assertNull(Main.calcShortestPath(graph, "A", "Y"));
  }

  @Test
  public void testStartEqualsEnd() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    graph.addEdge(nodeA, nodeB);
    assertNull(Main.calcShortestPath(graph, "A", "A"));
  }

  @Test
  public void testNoPathBetweenNodes() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    Node nodeC = new Node("C");
    graph.addEdge(nodeA, nodeB);
    graph.addEdge(nodeA, nodeC);
    assertNull(Main.calcShortestPath(graph, "B", "C"));
  }
}