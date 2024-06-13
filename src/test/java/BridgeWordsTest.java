import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BridgeWordsTest {

  @Before
  public void setUp() throws Exception {
    System.out.println("=============测试开始=============");
  }

  @After
  public void tearDown() throws Exception {
    System.out.println("=============测试结束=============");
  }

  @Test
  public void testNullGraph() {
    assertEquals(null, Main.queryBridgeWords(null, "A", "B"));
  }

  @Test
  public void testStartWordNotExists() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    Node nodeC = new Node("C");
    graph.addEdge(nodeA, nodeB);
    graph.addEdge(nodeB, nodeC);
    assertEquals("No \"D\" in the graph!", Main.queryBridgeWords(graph, "D", "C"));
  }

  @Test
  public void testEndWordNotExists() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    Node nodeC = new Node("C");
    graph.addEdge(nodeA, nodeB);
    graph.addEdge(nodeB, nodeC);
    assertEquals("No \"D\" in the graph!", Main.queryBridgeWords(graph, "A", "D"));
  }

  @Test
  public void testStartAndEndWordNotExists() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    Node nodeC = new Node("C");
    graph.addEdge(nodeA, nodeB);
    graph.addEdge(nodeB, nodeC);
    assertEquals("No \"D\" and \"E\" in the graph!", Main.queryBridgeWords(graph, "D", "E"));
  }

  @Test
  public void testNoBridgeWords() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    Node nodeC = new Node("C");
    Node nodeD = new Node("D");
    graph.addEdge(nodeA, nodeB);
    graph.addEdge(nodeB, nodeC);
    graph.addEdge(nodeC, nodeD);
    assertEquals("No bridge words from \"A\" to \"D\"!", Main.queryBridgeWords(graph, "A", "D"));
  }

  @Test
  public void testOneBridgeWord() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    Node nodeC = new Node("C");
    graph.addEdge(nodeA, nodeB);
    graph.addEdge(nodeB, nodeC);
    assertEquals("The bridge words from \"A\" to \"C\" is:B", Main.queryBridgeWords(graph, "A", "C"));
  }

  @Test
  public void testBridgeWords() {
    Graph graph = new Graph();
    Node nodeA = new Node("A");
    Node nodeB = new Node("B");
    Node nodeC = new Node("C");
    Node nodeD = new Node("D");
    Node nodeE = new Node("E");
    graph.addEdge(nodeA, nodeB);
    graph.addEdge(nodeA, nodeC);
    graph.addEdge(nodeA, nodeD);
    graph.addEdge(nodeB, nodeE);
    graph.addEdge(nodeC, nodeE);
    graph.addEdge(nodeD, nodeE);
    assertEquals("The bridge words from \"A\" to \"E\" are:[B, C, D]", Main.queryBridgeWords(graph, "A", "E"));
  }

}